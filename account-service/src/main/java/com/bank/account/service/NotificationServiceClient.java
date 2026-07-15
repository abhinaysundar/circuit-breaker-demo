package com.bank.account.service;

import com.bank.account.dto.TransferRequest;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class NotificationServiceClient {

    private static final Logger log = LoggerFactory.getLogger(NotificationServiceClient.class);

    private final RestTemplate restTemplate;
    private final String notificationServiceUrl;

    public NotificationServiceClient(RestTemplate restTemplate,
                                     @Value("${notification.service.url}") String notificationServiceUrl) {
        this.restTemplate = restTemplate;
        this.notificationServiceUrl = notificationServiceUrl;
    }

    @CircuitBreaker(name = "notificationService", fallbackMethod = "sendNotificationFallback")
    public String sendNotification(TransferRequest request, String transactionResult) {
        String url = notificationServiceUrl + "/api/notifications";
        var body = new java.util.HashMap<String, Object>();
        body.put("fromAccount", request.getFromAccount());
        body.put("toAccount", request.getToAccount());
        body.put("amount", request.getAmount());
        body.put("message", "Transfer completed: " + transactionResult);

        var response = restTemplate.postForEntity(url, body, String.class);
        log.info("Notification sent: {}", response.getBody());
        return response.getBody();
    }

    public String sendNotificationFallback(TransferRequest request, String transactionResult, Throwable t) {
        log.warn("Notification service unavailable. Fallback: {}", t.getMessage());
        return "{\"status\":\"PENDING\",\"message\":\"Notification queued\"}";
    }
}
