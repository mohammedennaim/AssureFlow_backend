package com.pfe.notification.application.service;

import com.pfe.notification.application.dto.NotificationTemplateDto;

import java.util.List;
import java.util.UUID;

public interface NotificationTemplateService {

    NotificationTemplateDto createTemplate(NotificationTemplateDto dto);

    NotificationTemplateDto getTemplateById(UUID id);

    NotificationTemplateDto getTemplateByName(String name);

    List<NotificationTemplateDto> getAllTemplates();

    NotificationTemplateDto updateTemplate(UUID id, NotificationTemplateDto dto);

    void deleteTemplate(UUID id);
}
