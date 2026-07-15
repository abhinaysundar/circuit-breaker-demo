package com.bank.notification.service;

import com.bank.notification.model.Notification;
import com.bank.notification.repository.NotificationRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private volatile boolean failMode = false;
    private volatile boolean slowMode = false;

    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    public List<Notification> getAllNotifications() {
        return notificationRepository.findAll();
    }

    public Notification sendNotification(Map<String, Object> request) {
        if (failMode) {
            throw new RuntimeException("Notification service: Simulated failure");
        }
        if (slowMode) {
            try { Thread.sleep(3000); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        }

        Notification notification = new Notification();
        notification.setFromAccount((String) request.get("fromAccount"));
        notification.setToAccount((String) request.get("toAccount"));
        notification.setMessage((String) request.get("message"));
        return notificationRepository.save(notification);
    }

    public void setFailMode(boolean enabled) { this.failMode = enabled; }
    public void setSlowMode(boolean enabled) { this.slowMode = enabled; }
    public boolean isFailMode() { return failMode; }
    public boolean isSlowMode() { return slowMode; }
}
