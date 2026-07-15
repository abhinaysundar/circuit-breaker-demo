package com.bank.account.service;

import com.bank.account.dto.TransferRequest;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class TransactionServiceClient {

    private static final Logger log = LoggerFactory.getLogger(TransactionServiceClient.class);

    private final RestTemplate restTemplate;
    private final String transactionServiceUrl;

    public TransactionServiceClient(RestTemplate restTemplate,
                                    @Value("${transaction.service.url}") String transactionServiceUrl) {
        this.restTemplate = restTemplate;
        this.transactionServiceUrl = transactionServiceUrl;
    }

    @CircuitBreaker(name = "transactionService", fallbackMethod = "recordTransactionFallback")
    public String recordTransaction(TransferRequest request) {
        String url = transactionServiceUrl + "/api/transactions";
        var body = new java.util.HashMap<String, Object>();
        body.put("fromAccount", request.getFromAccount());
        body.put("toAccount", request.getToAccount());
        body.put("amount", request.getAmount());
        body.put("type", "TRANSFER");

        var response = restTemplate.postForEntity(url, body, String.class);
        log.info("Transaction recorded: {}", response.getBody());
        return response.getBody();
    }

    public String recordTransactionFallback(TransferRequest request, Throwable t) {
        log.warn("Transaction service unavailable. Fallback: {}", t.getMessage());
        return "{\"status\":\"PENDING\",\"message\":\"Transaction queued for processing\"}";
    }
}
