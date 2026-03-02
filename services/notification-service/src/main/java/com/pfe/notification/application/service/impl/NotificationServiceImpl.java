package com.pfe.notification.application.service.impl;

import com.pfe.notification.application.dto.NotificationRequest;
import com.pfe.notification.application.dto.NotificationResponse;
import com.pfe.notification.application.service.NotificationService;
import com.pfe.notification.domain.model.Notification;
import com.pfe.notification.domain.model.NotificationType;
import com.pfe.notification.domain.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;

    @Override
    @Transactional
    public NotificationResponse sendNotification(NotificationRequest request) {
        log.info("Sending {} notification to recipient: {}", request.getType(), request.getRecipientId());

        Notification notification = Notification.builder()
                .id(UUID.randomUUID().toString())
                .recipientId(request.getRecipientId())
                .type(NotificationType.valueOf(request.getType()))
                .subject(request.getSubject())
                .message(request.getMessage())
                .read(false)
                .build();

        return toResponse(notificationRepository.save(notification));
    }

    @Override
    @Transactional(readOnly = true)
    public NotificationResponse getNotificationById(String id) {
        return toResponse(notificationRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Notification not found: " + id)));
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationResponse> getNotificationsByRecipient(String recipientId) {
        return notificationRepository.findByRecipientId(recipientId).stream()
                .map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationResponse> getUnreadNotifications(String recipientId) {
        return notificationRepository.findUnreadByRecipientId(recipientId).stream()
                .map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationResponse> getAllNotifications() {
        return notificationRepository.findAll().stream()
                .map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public NotificationResponse markAsRead(String id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Notification not found: " + id));
        notification.markAsRead();
        return toResponse(notificationRepository.save(notification));
    }

    @Override
    @Transactional
    public void deleteNotification(String id) {
        if (notificationRepository.findById(id).isEmpty()) {
            throw new NoSuchElementException("Notification not found: " + id);
        }
        notificationRepository.deleteById(id);
    }

    private NotificationResponse toResponse(Notification n) {
        return NotificationResponse.builder()
                .id(n.getId())
                .recipientId(n.getRecipientId())
                .type(n.getType() != null ? n.getType().name() : null)
                .subject(n.getSubject())
                .message(n.getMessage())
                .read(n.isRead())
                .createdAt(n.getCreatedAt())
                .readAt(n.getReadAt())
                .build();
    }
}
