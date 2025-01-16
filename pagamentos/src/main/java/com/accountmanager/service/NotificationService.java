package com.accountmanager.service;

import com.accountmanager.model.Notification;
import com.accountmanager.model.NotificationType;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class NotificationService {
    private final Map<String, List<Notification>> notificationsByAccount = new HashMap<>();

    public void sendWarning(String accountId, String message, NotificationType type) {
        Notification notification = Notification.builder()
                .id(generateId())
                .accountId(accountId)
                .type(type)
                .message(message)
                .sentDate(LocalDateTime.now())
                .acknowledged(false)
                .build();

        notificationsByAccount
                .computeIfAbsent(accountId, k -> new ArrayList<>())
                .add(notification);

        // Aqui você poderia adicionar integração com sistema de e-mail, SMS, etc.
        log.info("Enviando notificação para conta {}: {} - {}", accountId, type, message);
    }

    public List<Notification> getNotificationsForAccount(String accountId) {
        return notificationsByAccount.getOrDefault(accountId, new ArrayList<>());
    }

    public boolean hasRecentNotification(String accountId, NotificationType type, int minutesThreshold) {
        LocalDateTime threshold = LocalDateTime.now().minusMinutes(minutesThreshold);
        return getNotificationsForAccount(accountId).stream()
                .anyMatch(n -> n.getType() == type && n.getSentDate().isAfter(threshold));
    }

    private String generateId() {
        return "NOT" + System.currentTimeMillis();
    }
}