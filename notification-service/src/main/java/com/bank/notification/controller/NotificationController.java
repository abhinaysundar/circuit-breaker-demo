package com.bank.notification.controller;

import com.bank.notification.model.Notification;
import com.bank.notification.service.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping
    public ResponseEntity<List<Notification>> getAllNotifications() {
        return ResponseEntity.ok(notificationService.getAllNotifications());
    }

    @PostMapping
    public ResponseEntity<Notification> sendNotification(@RequestBody Map<String, Object> request) {
        return ResponseEntity.ok(notificationService.sendNotification(request));
    }

    @PostMapping("/failure")
    public ResponseEntity<String> toggleFailure(@RequestParam boolean enabled) {
        notificationService.setFailMode(enabled);
        return ResponseEntity.ok("Fail mode set to " + enabled);
    }

    @PostMapping("/slow")
    public ResponseEntity<String> toggleSlow(@RequestParam boolean enabled) {
        notificationService.setSlowMode(enabled);
        return ResponseEntity.ok("Slow mode set to " + enabled);
    }

    @GetMapping("/status")
    public ResponseEntity<Map<String, Boolean>> getStatus() {
        return ResponseEntity.ok(Map.of(
            "failMode", notificationService.isFailMode(),
            "slowMode", notificationService.isSlowMode()
        ));
    }
}
