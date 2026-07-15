package com.bank.transaction.service;

import com.bank.transaction.model.Transaction;
import com.bank.transaction.repository.TransactionRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private volatile boolean failMode = false;
    private volatile boolean slowMode = false;

    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }

    public Transaction recordTransaction(Map<String, Object> request) {
        if (failMode) {
            throw new RuntimeException("Transaction service: Simulated failure");
        }
        if (slowMode) {
            try { Thread.sleep(3000); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        }

        Transaction tx = new Transaction();
        tx.setFromAccount((String) request.get("fromAccount"));
        tx.setToAccount((String) request.get("toAccount"));
        tx.setAmount(new java.math.BigDecimal(request.get("amount").toString()));
        tx.setType((String) request.get("type"));
        return transactionRepository.save(tx);
    }

    public void setFailMode(boolean enabled) { this.failMode = enabled; }
    public void setSlowMode(boolean enabled) { this.slowMode = enabled; }
    public boolean isFailMode() { return failMode; }
    public boolean isSlowMode() { return slowMode; }
}
