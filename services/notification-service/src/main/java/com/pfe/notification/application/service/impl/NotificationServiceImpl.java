package com.pfe.notification.application.service.impl;

import com.pfe.notification.application.dto.CreateNotificationRequest;
import com.pfe.notification.application.dto.NotificationDto;
import com.pfe.notification.application.mapper.NotificationMapper;
import com.pfe.notification.application.service.NotificationService;
import com.pfe.notification.domain.exception.NotificationNotFoundException;
import com.pfe.notification.domain.model.Notification;
import com.pfe.notification.domain.model.NotificationChannel;
import com.pfe.notification.domain.model.NotificationStatus;
import com.pfe.notification.domain.repository.NotificationRepository;
import com.pfe.notification.infrastructure.email.EmailNotificationService;
import com.pfe.notification.infrastructure.sms.TwilioSmsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;
    private final EmailNotificationService emailNotificationService;
    private final TwilioSmsService twilioSmsService;

    @Override
    @PreAuthorize("hasAnyRole('ADMIN', 'AGENT')")
    @Transactional
    public NotificationDto createNotification(CreateNotificationRequest request) {
        return createNotificationInternal(request);
    }

    /**
     * Internal method for creating notifications without security checks.
     * Used by Kafka consumers and other internal services.
     */
    @Transactional
    public NotificationDto createNotificationInternal(CreateNotificationRequest request) {
        Notification notification = notificationMapper.toDomain(request);
        notification.setStatus(NotificationStatus.PENDING);
        Notification saved = notificationRepository.save(notification);
        return notificationMapper.toDto(saved);
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN', 'AGENT', 'CLIENT')")
    public NotificationDto getNotificationById(UUID id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new NotificationNotFoundException(id));
        return notificationMapper.toDto(notification);
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN', 'AGENT', 'CLIENT')")
    public List<NotificationDto> getNotificationsByPolicyId(UUID policyId) {
        return notificationRepository.findByPolicyId(policyId).stream()
                .map(notificationMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN', 'AGENT', 'CLIENT')")
    public List<NotificationDto> getNotificationsByRecipient(String recipient) {
        return notificationRepository.findByRecipient(recipient).stream()
                .map(notificationMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN', 'AGENT')")
    public List<NotificationDto> getAllNotifications() {
        return notificationRepository.findAll().stream()
                .map(notificationMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN', 'AGENT')")
    public Page<NotificationDto> getAllNotificationsPaged(int page, int size) {
        return notificationRepository.findAllPaged(
                PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt")))
                .map(notificationMapper::toDto);
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN', 'AGENT')")
    @Transactional
    public void sendNotification(UUID id) {
        sendNotificationInternal(id);
    }

    /**
     * Internal method for sending notifications without security checks.
     * Used by Kafka consumers and other internal services.
     */
    @Transactional
    public void sendNotificationInternal(UUID id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new NotificationNotFoundException(id));

        try {
            if (NotificationChannel.EMAIL.equals(notification.getChannel())
                    && notification.getRecipient() != null && notification.getRecipient().contains("@")) {
                emailNotificationService.sendEmail(
                        notification.getRecipient(),
                        notification.getSubject(),
                        notification.getContent());
                notification.send();
                log.info("[NOTIFICATION] Email sent successfully: id={}, recipient={}", id, notification.getRecipient());
            } else if (NotificationChannel.SMS.equals(notification.getChannel())
                    && notification.getRecipient() != null) {
                twilioSmsService.sendSms(notification.getRecipient(), notification.getContent());
                notification.send();
                log.info("[NOTIFICATION] SMS sent successfully via Twilio: id={}, recipient={}", id, notification.getRecipient());
            } else if (NotificationChannel.IN_APP.equals(notification.getChannel())) {
                // In-app notifications are stored in DB and retrieved by frontend
                notification.send();
                log.info("[NOTIFICATION] In-app notification marked as sent: id={}, type={}", id, notification.getType());
            } else {
                log.warn("[NOTIFICATION] Unknown channel or missing recipient: channel={}, id={}", 
                        notification.getChannel(), id);
                notification.fail();
            }
        } catch (Exception e) {
            log.error("[NOTIFICATION] Failed to send notification id={}: {}", id, e.getMessage(), e);
            notification.fail();
            throw new RuntimeException("Failed to send notification: " + e.getMessage(), e);
        } finally {
            notificationRepository.save(notification);
        }
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public void deleteNotification(UUID id) {
        if (notificationRepository.findById(id).isEmpty()) {
            throw new NotificationNotFoundException(id);
        }
        notificationRepository.deleteById(id);
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN', 'AGENT', 'CLIENT')")
    @Transactional
    public void markAsRead(UUID id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new NotificationNotFoundException(id));
        notification.markAsRead();
        notificationRepository.save(notification);
        log.info("[NOTIFICATION] Marked as read: id={}", id);
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN', 'AGENT', 'CLIENT')")
    @Transactional
    public void markAllAsRead(String recipient) {
        List<Notification> notifications = notificationRepository.findByRecipient(recipient);
        notifications.forEach(Notification::markAsRead);
        notificationRepository.saveAll(notifications);
        log.info("[NOTIFICATION] Marked all as read for recipient: {}", recipient);
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN', 'AGENT', 'CLIENT')")
    public long getUnreadCount(String recipient) {
        return notificationRepository.countByRecipientAndReadFalse(recipient);
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN', 'AGENT')")
    public Map<String, Object> getDashboardStatistics() {
        Map<String, Object> stats = new HashMap<>();
        
        // Total notifications
        long totalNotifications = notificationRepository.count();
        stats.put("totalNotifications", totalNotifications);
        
        // Notifications by status
        long sentCount = notificationRepository.countByStatus(NotificationStatus.SENT);
        long pendingCount = notificationRepository.countByStatus(NotificationStatus.PENDING);
        long failedCount = notificationRepository.countByStatus(NotificationStatus.FAILED);
        long deliveredCount = notificationRepository.countByStatus(NotificationStatus.DELIVERED);

        stats.put("sentCount", sentCount);
        stats.put("pendingCount", pendingCount);
        stats.put("failedCount", failedCount);
        stats.put("deliveredCount", deliveredCount);

        // Notifications by channel
        long emailCount = notificationRepository.countByChannel(NotificationChannel.EMAIL);
        long smsCount = notificationRepository.countByChannel(NotificationChannel.SMS);
        long inAppCount = notificationRepository.countByChannel(NotificationChannel.IN_APP);

        stats.put("emailCount", emailCount);
        stats.put("smsCount", smsCount);
        stats.put("inAppCount", inAppCount);

        // Success rate
        double successRate = totalNotifications > 0
            ? ((double) (sentCount + deliveredCount) / totalNotifications) * 100
            : 0.0;
        stats.put("successRate", Math.round(successRate * 100.0) / 100.0);

        // Recent notifications (last 7 days)
        long recentNotifications = notificationRepository.countByCreatedAtAfter(
            java.time.LocalDateTime.now().minusDays(7));
        stats.put("recentNotifications", recentNotifications);

        return stats;
    }
}
