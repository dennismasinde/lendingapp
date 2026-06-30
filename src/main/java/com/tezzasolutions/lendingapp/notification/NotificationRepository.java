package com.tezzasolutions.lendingapp.notification;

import com.tezzasolutions.lendingapp.common.enums.NotificationChannel;
import com.tezzasolutions.lendingapp.common.enums.NotificationType;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByCustomerId(Long customerId);

    @Query("SELECT n FROM Notification n WHERE n.customer.id = :customerId AND n.isRead = false")
    List<Notification> findUnreadNotifications(@Param("customerId") Long customerId);

    @Query("SELECT n FROM Notification n WHERE n.sentAt >= :date AND n.isSent = true")
    List<Notification> findNotificationsSentSince(@Param("date") Instant date);

    @Query("SELECT n FROM Notification n WHERE n.type = :type AND n.customer.id = :customerId AND n.isSent = true")
    List<Notification> findCustomerNotificationsByType(@Param("customerId") Long customerId,
                                                       @Param("type") NotificationType type);

    @Query("SELECT n FROM Notification n WHERE n.channel = :channel AND n.isSent = false ORDER BY n.sentAt ASC")
    List<Notification> findPendingNotificationsByChannel(@Param("channel") NotificationChannel channel);

    @Query("SELECT COUNT(n) FROM Notification n WHERE n.customer.id = :customerId AND n.isRead = false")
    Long countUnreadNotifications(@Param("customerId") Long customerId);

    @Modifying
    @Transactional
    @Query("UPDATE Notification n SET n.isRead = true, n.readAt = :readAt WHERE n.customer.id = :customerId AND n.isRead = false")
    int markAllAsRead(@Param("customerId") Long customerId, @Param("readAt") Instant readAt);

    @Query("SELECT n FROM Notification n WHERE n.customer.id = :customerId AND n.type IN :types ORDER BY n.sentAt DESC")
    List<Notification> findCustomerNotificationsByTypes(@Param("customerId") Long customerId,
                                                        @Param("types") List<NotificationType> types);

    void deleteByIsSentTrueAndSentAtBefore(Instant cutoffDate);
}
