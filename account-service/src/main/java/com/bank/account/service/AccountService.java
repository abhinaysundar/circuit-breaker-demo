package com.bank.account.service;

import com.bank.account.dto.AccountRequest;
import com.bank.account.dto.AccountResponse;
import com.bank.account.dto.TransferRequest;
import com.bank.account.model.Account;
import com.bank.account.model.AccountStatus;
import com.bank.account.repository.AccountRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final TransactionServiceClient transactionClient;
    private final NotificationServiceClient notificationClient;

    public AccountService(AccountRepository accountRepository,
                          TransactionServiceClient transactionClient,
                          NotificationServiceClient notificationClient) {
        this.accountRepository = accountRepository;
        this.transactionClient = transactionClient;
        this.notificationClient = notificationClient;
    }

    public List<AccountResponse> getAllAccounts() {
        return accountRepository.findAll().stream().map(this::toResponse).collect(Collectors.toList());
    }

    public AccountResponse getAccount(String accountNumber) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new RuntimeException("Account not found: " + accountNumber));
        return toResponse(account);
    }

    @Transactional
    public AccountResponse createAccount(AccountRequest request) {
        Account account = new Account();
        account.setAccountNumber(UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        account.setAccountHolder(request.getAccountHolder());
        account.setBalance(request.getInitialDeposit() != null ? request.getInitialDeposit() : BigDecimal.ZERO);
        account = accountRepository.save(account);
        return toResponse(account);
    }

    @Transactional
    public AccountResponse transfer(TransferRequest request) {
        Account from = accountRepository.findByAccountNumber(request.getFromAccount())
                .orElseThrow(() -> new RuntimeException("Sender account not found"));
        Account to = accountRepository.findByAccountNumber(request.getToAccount())
                .orElseThrow(() -> new RuntimeException("Receiver account not found"));

        if (from.getBalance().compareTo(request.getAmount()) < 0) {
            throw new RuntimeException("Insufficient balance");
        }

        from.setBalance(from.getBalance().subtract(request.getAmount()));
        to.setBalance(to.getBalance().add(request.getAmount()));
        accountRepository.save(from);
        accountRepository.save(to);

        String transactionResult = transactionClient.recordTransaction(request);
        notificationClient.sendNotification(request, transactionResult);

        return toResponse(from);
    }

    private AccountResponse toResponse(Account account) {
        AccountResponse response = new AccountResponse();
        response.setId(account.getId());
        response.setAccountNumber(account.getAccountNumber());
        response.setAccountHolder(account.getAccountHolder());
        response.setBalance(account.getBalance());
        response.setStatus(account.getStatus());
        response.setCreatedAt(account.getCreatedAt());
        response.setMessage("Account processed successfully");
        return response;
    }
}
