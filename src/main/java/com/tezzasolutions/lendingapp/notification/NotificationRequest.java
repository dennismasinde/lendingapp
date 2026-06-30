package com.tezzasolutions.lendingapp.notification;

import com.tezzasolutions.lendingapp.common.enums.NotificationChannel;
import com.tezzasolutions.lendingapp.common.enums.NotificationType;
import com.tezzasolutions.lendingapp.customer.Customer;
import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class NotificationRequest {
    private Customer customer;
    private String subject;
    private String content;
    private NotificationType type;
    private NotificationChannel channel;
    private String templateId;
    private Map<String, String> variables;
}
