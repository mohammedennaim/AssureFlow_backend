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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;
    private final EmailNotificationService emailNotificationService;

    @Override
    @PreAuthorize("hasAnyRole('ADMIN', 'AGENT')")
    @Transactional
    @CacheEvict(value = "notifications", allEntries = true)
    public NotificationDto createNotification(CreateNotificationRequest request) {
        Notification notification = notificationMapper.toDomain(request);
        notification.setStatus(NotificationStatus.PENDING);
        Notification saved = notificationRepository.save(notification);
        return notificationMapper.toDto(saved);
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN', 'AGENT', 'CLIENT')")
    @Cacheable(value = "notifications", key = "#id")
    public NotificationDto getNotificationById(UUID id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new NotificationNotFoundException(id));
        return notificationMapper.toDto(notification);
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN', 'AGENT', 'CLIENT')")
    @Cacheable(value = "notifications", key = "'policy_' + #policyId")
    public List<NotificationDto> getNotificationsByPolicyId(UUID policyId) {
        return notificationRepository.findByPolicyId(policyId).stream()
                .map(notificationMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN', 'AGENT', 'CLIENT')")
    @Cacheable(value = "notifications", key = "'recipient_' + #recipient")
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
    @CacheEvict(value = "notifications", key = "#id")
    public void sendNotification(UUID id) {
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
                log.info("[NOTIFICATION] Email sent successfully: id={}", id);
            } else {
                log.info("[NOTIFICATION] Channel={} — recipient={} — non-email delivery not yet implemented",
                        notification.getChannel(), notification.getRecipient());
                notification.send();
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
    @CacheEvict(value = "notifications", key = "#id")
    public void deleteNotification(UUID id) {
        if (notificationRepository.findById(id).isEmpty()) {
            throw new NotificationNotFoundException(id);
        }
        notificationRepository.deleteById(id);
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN', 'AGENT', 'CLIENT')")
    @Transactional
    @CacheEvict(value = "notifications", key = "#id")
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
    @CacheEvict(value = "notifications", allEntries = true)
    public void markAllAsRead(String recipient) {
        List<Notification> notifications = notificationRepository.findByRecipient(recipient);
        notifications.forEach(Notification::markAsRead);
        notificationRepository.saveAll(notifications);
        log.info("[NOTIFICATION] Marked all as read for recipient: {}", recipient);
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN', 'AGENT', 'CLIENT')")
    @Cacheable(value = "notifications", key = "'unread_count_' + #recipient")
    public long getUnreadCount(String recipient) {
        return notificationRepository.countByRecipientAndReadFalse(recipient);
    }
}
