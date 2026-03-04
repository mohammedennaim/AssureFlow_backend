package com.pfe.notification.application.service.impl;

import com.pfe.notification.application.dto.NotificationLogDto;
import com.pfe.notification.application.mapper.NotificationLogMapper;
import com.pfe.notification.application.service.NotificationLogService;
import com.pfe.notification.domain.repository.NotificationLogRepository;
import com.pfe.commons.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationLogServiceImpl implements NotificationLogService {

    private final NotificationLogRepository logRepository;
    private final NotificationLogMapper logMapper;

    @Override
    public NotificationLogDto getLogById(UUID id) {
        return logRepository.findById(id)
                .map(logMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("NotificationLog", "id", id));
    }

    @Override
    public List<NotificationLogDto> getLogsByNotificationId(UUID notificationId) {
        return logRepository.findByNotificationId(notificationId).stream()
                .map(logMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<NotificationLogDto> getAllLogs() {
        return logRepository.findAll().stream()
                .map(logMapper::toDto)
                .collect(Collectors.toList());
    }
}
