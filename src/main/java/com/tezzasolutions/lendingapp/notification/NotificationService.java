package com.tezzasolutions.lendingapp.notification;

import com.tezzasolutions.lendingapp.common.enums.NotificationChannel;
import com.tezzasolutions.lendingapp.common.enums.NotificationType;
import com.tezzasolutions.lendingapp.customer.Customer;
import com.tezzasolutions.lendingapp.loan.Loan;
import com.tezzasolutions.lendingapp.repayment.Repayment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final NotificationRepository notificationRepository;

    @Transactional
    public Notification createNotification(NotificationRequest request) {
        Notification notification = Notification.builder()
                .customer(request.getCustomer())
                .subject(request.getSubject())
                .content(request.getContent())
                .type(request.getType())
                .channel(request.getChannel())
                .sentAt(Instant.now())
                .isSent(false)
                .isRead(false)
                .templateId(request.getTemplateId())
                .variables(request.getVariables())
                .build();

        return notificationRepository.save(notification);
    }

    @Transactional
    public void sendLoanCreatedNotification(Loan loan) {
        Map<String, String> variables = new HashMap<>();
        variables.put("loanAmount", loan.getPrincipalAmount().toString());
        variables.put("customerName", loan.getCustomer().getFullName());
        variables.put("loanId", loan.getId().toString());

        NotificationRequest request = NotificationRequest.builder()
                .customer(loan.getCustomer())
                .subject("Loan Created Successfully")
                .content("Your loan application has been created successfully.")
                .type(NotificationType.LOAN_CREATED)
                .channel(NotificationChannel.EMAIL)
                .templateId("LOAN_CREATED_TEMPLATE")
                .variables(variables)
                .build();

        createAndSendNotification(request);
    }

    @Transactional
    public void sendLoanApprovedNotification(Loan loan) {
        Map<String, String> variables = new HashMap<>();
        variables.put("loanAmount", loan.getPrincipalAmount().toString());
        variables.put("customerName", loan.getCustomer().getFullName());

        NotificationRequest request = NotificationRequest.builder()
                .customer(loan.getCustomer())
                .subject("Loan Approved")
                .content("Congratulations! Your loan has been approved.")
                .type(NotificationType.LOAN_APPROVED)
                .channel(NotificationChannel.EMAIL)
                .templateId("LOAN_APPROVED_TEMPLATE")
                .variables(variables)
                .build();

        createAndSendNotification(request);
    }

    @Transactional
    public void sendRepaymentReminder(Loan loan, int daysUntilDue) {
        Map<String, String> variables = new HashMap<>();
        variables.put("amount", loan.getOutstandingBalance().toString());
        variables.put("dueDate", loan.getDueDate().toString());
        variables.put("days", String.valueOf(daysUntilDue));

        NotificationRequest request = NotificationRequest.builder()
                .customer(loan.getCustomer())
                .subject("Payment Reminder")
                .content(String.format("Your payment of %s is due in %d days",
                        loan.getOutstandingBalance(), daysUntilDue))
                .type(NotificationType.REPAYMENT_REMINDER)
                .channel(NotificationChannel.SMS)
                .templateId("PAYMENT_REMINDER_TEMPLATE")
                .variables(variables)
                .build();

        createAndSendNotification(request);
    }

    @Transactional
    public void sendOverdueNotification(Loan loan) {
        Map<String, String> variables = new HashMap<>();
        variables.put("amount", loan.getOutstandingBalance().toString());
        variables.put("daysOverdue", String.valueOf(
                Instant.now().getEpochSecond() - loan.getDueDate().getEpochSecond()
        ));

        NotificationRequest request = NotificationRequest.builder()
                .customer(loan.getCustomer())
                .subject("Overdue Payment Notice")
                .content("Your loan payment is overdue. Please make payment immediately.")
                .type(NotificationType.OVERDUE_NOTICE)
                .channel(NotificationChannel.PUSH)
                .templateId("OVERDUE_TEMPLATE")
                .variables(variables)
                .build();

        createAndSendNotification(request);
    }

    @Transactional
    public void sendPaymentAcknowledgement(Repayment repayment) {
        Map<String, String> variables = new HashMap<>();
        variables.put("amount", repayment.getAmount().toString());
        variables.put("paymentDate", repayment.getRepaymentDate().toString());
        variables.put("transactionRef", repayment.getTransactionReference());

        NotificationRequest request = NotificationRequest.builder()
                .customer(repayment.getCustomer())
                .subject("Payment Received")
                .content(String.format("Your payment of %s has been received", repayment.getAmount()))
                .type(NotificationType.PAYMENT_ACKNOWLEDGMENT)
                .channel(NotificationChannel.EMAIL)
                .templateId("PAYMENT_ACKNOWLEDGMENT_TEMPLATE")
                .variables(variables)
                .build();

        createAndSendNotification(request);
    }

    @Transactional
    public void sendLoanClosedNotification(Loan loan) {
        Map<String, String> variables = new HashMap<>();
        variables.put("customerName", loan.getCustomer().getFullName());
        variables.put("loanId", loan.getId().toString());

        NotificationRequest request = NotificationRequest.builder()
                .customer(loan.getCustomer())
                .subject("Loan Closed")
                .content("Your loan has been successfully closed.")
                .type(NotificationType.LOAN_CLOSED)
                .channel(NotificationChannel.EMAIL)
                .templateId("LOAN_CLOSED_TEMPLATE")
                .variables(variables)
                .build();

        createAndSendNotification(request);
    }

    @Transactional
    public void sendBulkNotifications(List<NotificationRequest> requests) {
        for (NotificationRequest request : requests) {
            createAndSendNotification(request);
        }
    }

    @Transactional
    public void markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));

        notification.setIsRead(true);
        notification.setReadAt(Instant.now());
        notificationRepository.save(notification);
    }

    @Transactional
    public void markAllAsRead(Long customerId) {
        notificationRepository.markAllAsRead(customerId, Instant.now());
    }

    @Transactional(readOnly = true)
    public List<Notification> getCustomerNotifications(Long customerId) {
        return notificationRepository.findByCustomerId(customerId);
    }

    @Transactional(readOnly = true)
    public List<Notification> getUnreadNotifications(Long customerId) {
        return notificationRepository.findUnreadNotifications(customerId);
    }

    @Transactional(readOnly = true)
    public Long countUnreadNotifications(Long customerId) {
        return notificationRepository.countUnreadNotifications(customerId);
    }

    private void createAndSendNotification(NotificationRequest request) {
        Notification notification = createNotification(request);

        // Simulate sending notification
        try {
            sendNotification(notification);
            notification.setIsSent(true);
            notificationRepository.save(notification);
            log.info("Notification sent: {}", notification.getId());
        } catch (Exception e) {
            log.error("Failed to send notification: {}", e.getMessage());
        }
    }

    private void sendNotification(Notification notification) {
        // This would be implemented to send via actual channels
        // Email, SMS, Push notification integration
        log.info("Sending notification via {} to {}: {}",
                notification.getChannel(),
                notification.getCustomer().getEmail(),
                notification.getSubject()
        );

        // Simulate successful send
    }
}
