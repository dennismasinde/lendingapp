package com.tezzasolutions.lendingapp.repository;

import com.tezzasolutions.lendingapp.BaseDataJpaTest;
import com.tezzasolutions.lendingapp.common.enums.NotificationChannel;
import com.tezzasolutions.lendingapp.common.enums.NotificationType;
import com.tezzasolutions.lendingapp.customer.Customer;
import com.tezzasolutions.lendingapp.customer.CustomerRepository;
import com.tezzasolutions.lendingapp.notification.Notification;
import com.tezzasolutions.lendingapp.notification.NotificationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@TestPropertySource(properties = {
        "spring.flyway.enabled=false",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
class NotificationRepositoryTest {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Customer customer;
    private Notification notification1;
    private Notification notification2;
    private Notification notification3;

    @BeforeEach
    void setUp() {
        // Clean up
        notificationRepository.deleteAll();
        customerRepository.deleteAll();

        // Create and save customer
        customer = Customer.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .phoneNumber("+254700000001")
                .employmentStatus("EMPLOYED")
                .isActive(true)
                .build();
        customer = customerRepository.save(customer);

        Instant now = Instant.now();

        // Create test notifications
        Map<String, String> variables1 = new HashMap<>();
        variables1.put("loanAmount", "100000");
        variables1.put("customerName", "John Doe");

        notification1 = Notification.builder()
                .customer(customer)
                .subject("Loan Approved")
                .content("Your loan of KES 100,000 has been approved")
                .type(NotificationType.LOAN_APPROVED)
                .channel(NotificationChannel.EMAIL)
                .sentAt(now.minus(5, ChronoUnit.DAYS))
                .isSent(true)
                .isRead(true)
                .templateId("LOAN_APPROVED_TEMPLATE")
                .variables(variables1)
                .build();

        Map<String, String> variables2 = new HashMap<>();
        variables2.put("dueDate", "2024-01-15");
        variables2.put("amount", "25000");

        notification2 = Notification.builder()
                .customer(customer)
                .subject("Payment Reminder")
                .content("Your payment of KES 25,000 is due on 2024-01-15")
                .type(NotificationType.REPAYMENT_REMINDER)
                .channel(NotificationChannel.SMS)
                .sentAt(now.minus(2, ChronoUnit.DAYS))
                .isSent(true)
                .isRead(false)
                .templateId("PAYMENT_REMINDER_TEMPLATE")
                .variables(variables2)
                .build();

        notification3 = Notification.builder()
                .customer(customer)
                .subject("Overdue Notice")
                .content("Your loan payment is overdue. Please make payment immediately.")
                .type(NotificationType.OVERDUE_NOTICE)
                .channel(NotificationChannel.PUSH)
                .sentAt(now)
                .isSent(false)
                .isRead(false)
                .templateId("OVERDUE_TEMPLATE")
                .build();

        // Save notifications
        notification1 = notificationRepository.save(notification1);
        notification2 = notificationRepository.save(notification2);
        notification3 = notificationRepository.save(notification3);
    }

    @Test
    void shouldSaveAndFindNotification() {
        // Given
        Map<String, String> variables = new HashMap<>();
        variables.put("paymentAmount", "15000");
        variables.put("paymentDate", "2024-01-10");

        Notification newNotification = Notification.builder()
                .customer(customer)
                .subject("Payment Received")
                .content("Your payment of KES 15,000 has been received")
                .type(NotificationType.PAYMENT_ACKNOWLEDGMENT)
                .channel(NotificationChannel.EMAIL)
                .sentAt(Instant.now())
                .isSent(true)
                .isRead(false)
                .templateId("PAYMENT_RECEIVED_TEMPLATE")
                .variables(variables)
                .build();

        // When
        Notification saved = notificationRepository.save(newNotification);

        // Then
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getCreatedAt()).isNotNull();
        assertThat(saved.getSubject()).isEqualTo("Payment Received");

        // When
        Notification found = notificationRepository.findById(saved.getId()).orElse(null);

        // Then
        assertThat(found).isNotNull();
        assertThat(found.getType()).isEqualTo(NotificationType.PAYMENT_ACKNOWLEDGMENT);
        assertThat(found.getVariables()).containsKey("paymentAmount");
        assertThat(found.getVariables().get("paymentAmount")).isEqualTo("15000");
    }

    @Test
    void shouldFindNotificationsByCustomerId() {
        // When
        List<Notification> notifications = notificationRepository.findByCustomerId(customer.getId());

        // Then
        assertThat(notifications).hasSize(3);
        assertThat(notifications)
                .extracting(Notification::getType)
                .containsExactlyInAnyOrder(
                        NotificationType.LOAN_APPROVED,
                        NotificationType.REPAYMENT_REMINDER,
                        NotificationType.OVERDUE_NOTICE
                );
    }

    @Test
    void shouldFindUnreadNotifications() {
        // When
        List<Notification> unreadNotifications = notificationRepository.findUnreadNotifications(
                customer.getId()
        );

        // Then
        assertThat(unreadNotifications).hasSize(2);
        assertThat(unreadNotifications)
                .extracting(Notification::getType)
                .containsExactlyInAnyOrder(
                        NotificationType.REPAYMENT_REMINDER,
                        NotificationType.OVERDUE_NOTICE
                );
    }

    @Test
    void shouldFindNotificationsSentSince() {
        // When
        Instant since = Instant.now().minus(3, ChronoUnit.DAYS);
        List<Notification> recentNotifications = notificationRepository.findNotificationsSentSince(since);

        // Then
        assertThat(recentNotifications).hasSize(2);
        assertThat(recentNotifications)
                .extracting(Notification::getType)
                .containsExactlyInAnyOrder(
                        NotificationType.REPAYMENT_REMINDER,
                        NotificationType.OVERDUE_NOTICE
                );
    }

    @Test
    void shouldFindCustomerNotificationsByType() {
        // When
        List<Notification> loanApproved = notificationRepository.findCustomerNotificationsByType(
                customer.getId(),
                NotificationType.LOAN_APPROVED
        );

        // Then
        assertThat(loanApproved).hasSize(1);
        assertThat(loanApproved.get(0).getSubject()).isEqualTo("Loan Approved");
    }

    @Test
    void shouldFindPendingNotificationsByChannel() {
        // When
        List<Notification> pendingPush = notificationRepository.findPendingNotificationsByChannel(
                NotificationChannel.PUSH
        );
        List<Notification> pendingEmail = notificationRepository.findPendingNotificationsByChannel(
                NotificationChannel.EMAIL
        );

        // Then
        assertThat(pendingPush).hasSize(1);
        assertThat(pendingPush.get(0).getType()).isEqualTo(NotificationType.OVERDUE_NOTICE);
        assertThat(pendingEmail).isEmpty();
    }

    @Test
    void shouldCountUnreadNotifications() {
        // When
        Long unreadCount = notificationRepository.countUnreadNotifications(customer.getId());

        // Then
        assertThat(unreadCount).isEqualTo(2);
    }

    @Test
    void shouldMarkAllAsRead() {
        // When
        int updatedCount = notificationRepository.markAllAsRead(
                customer.getId(),
                Instant.now()
        );

        // Then
        assertThat(updatedCount).isEqualTo(2);

        // Verify
        List<Notification> unread = notificationRepository.findUnreadNotifications(customer.getId());
        assertThat(unread).isEmpty();
    }

    @Test
    void shouldFindCustomerNotificationsByTypes() {
        // When
        List<NotificationType> types = List.of(
                NotificationType.LOAN_APPROVED,
                NotificationType.REPAYMENT_REMINDER
        );
        List<Notification> notifications = notificationRepository.findCustomerNotificationsByTypes(
                customer.getId(),
                types
        );

        // Then
        assertThat(notifications).hasSize(2);
        assertThat(notifications)
                .extracting(Notification::getType)
                .containsExactlyInAnyOrder(
                        NotificationType.LOAN_APPROVED,
                        NotificationType.REPAYMENT_REMINDER
                );
    }

    @Test
    void shouldDeleteOldSentNotifications() {
        // Given
        Instant cutoffDate = Instant.now().minus(10, ChronoUnit.DAYS);

        // When
        notificationRepository.deleteByIsSentTrueAndSentAtBefore(cutoffDate);

        // Then
        List<Notification> remaining = notificationRepository.findByCustomerId(customer.getId());
        assertThat(remaining).hasSize(2);
        assertThat(remaining)
                .extracting(Notification::getType)
                .containsExactlyInAnyOrder(
                        NotificationType.REPAYMENT_REMINDER,
                        NotificationType.OVERDUE_NOTICE
                );
    }

    @Test
    void shouldUpdateNotificationToSent() {
        // Given
        Notification notification = notificationRepository.findById(notification3.getId()).orElseThrow();
        notification.setIsSent(true);
        notification.setSentAt(Instant.now());

        // When
        Notification updated = notificationRepository.save(notification);

        // Then
        assertThat(updated.getIsSent()).isTrue();
        assertThat(updated.getSentAt()).isNotNull();
        assertThat(updated.getUpdatedAt()).isAfter(updated.getCreatedAt());
    }

    @Test
    void shouldMarkNotificationAsRead() {
        // Given
        Notification notification = notificationRepository.findById(notification2.getId()).orElseThrow();
        notification.setIsRead(true);
        notification.setReadAt(Instant.now());

        // When
        Notification updated = notificationRepository.save(notification);

        // Then
        assertThat(updated.getIsRead()).isTrue();
        assertThat(updated.getReadAt()).isNotNull();
    }
}