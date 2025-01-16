package com.accountmanager.model;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class Notification {
    private String id;
    private String accountId;
    private NotificationType type;
    private String message;
    private LocalDateTime sentDate;
    private boolean acknowledged;
}