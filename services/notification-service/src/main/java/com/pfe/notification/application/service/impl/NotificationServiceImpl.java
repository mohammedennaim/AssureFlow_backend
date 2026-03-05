package com.pfe.notification.application.service.impl;

import com.pfe.notification.application.dto.CreateNotificationRequest;
import com.pfe.notification.application.dto.NotificationDto;
import com.pfe.notification.application.mapper.NotificationMapper;
import com.pfe.notification.application.service.NotificationService;
import com.pfe.notification.domain.exception.NotificationNotFoundException;
import com.pfe.notification.domain.model.Notification;
import com.pfe.notification.domain.model.NotificationStatus;
import com.pfe.notification.domain.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;

    @Override
    @Transactional
    @CacheEvict(value = "notifications", allEntries = true)
    public NotificationDto createNotification(CreateNotificationRequest request) {
        Notification notification = notificationMapper.toDomain(request);
        notification.setStatus(NotificationStatus.PENDING);
        Notification saved = notificationRepository.save(notification);
        return notificationMapper.toDto(saved);
    }

    @Override
    @Cacheable(value = "notifications", key = "#id")
    public NotificationDto getNotificationById(UUID id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new NotificationNotFoundException(id));
        return notificationMapper.toDto(notification);
    }

    @Override
    @Cacheable(value = "notifications", key = "'policy_' + #policyId")
    public List<NotificationDto> getNotificationsByPolicyId(UUID policyId) {
        return notificationRepository.findByPolicyId(policyId).stream()
                .map(notificationMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Cacheable(value = "notifications", key = "'recipient_' + #recipient")
    public List<NotificationDto> getNotificationsByRecipient(String recipient) {
        return notificationRepository.findByRecipient(recipient).stream()
                .map(notificationMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<NotificationDto> getAllNotifications() {
        return notificationRepository.findAll().stream()
                .map(notificationMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    @CacheEvict(value = "notifications", key = "#id")
    public void sendNotification(UUID id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new NotificationNotFoundException(id));
        notification.send();
        notificationRepository.save(notification);
    }

    @Override
    @Transactional
    @CacheEvict(value = "notifications", key = "#id")
    public void deleteNotification(UUID id) {
        if (notificationRepository.findById(id).isEmpty()) {
            throw new NotificationNotFoundException(id);
        }
        notificationRepository.deleteById(id);
    }
}
