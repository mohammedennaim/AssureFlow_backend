package com.pfe.notification.application.service.impl;

import com.pfe.notification.application.dto.NotificationTemplateDto;
import com.pfe.notification.application.mapper.NotificationTemplateMapper;
import com.pfe.notification.application.service.NotificationTemplateService;
import com.pfe.notification.domain.exception.NotificationTemplateNotFoundException;
import com.pfe.notification.domain.model.NotificationTemplate;
import com.pfe.notification.domain.repository.NotificationTemplateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationTemplateServiceImpl implements NotificationTemplateService {

    private final NotificationTemplateRepository templateRepository;
    private final NotificationTemplateMapper templateMapper;

    @Override
    @Transactional
    public NotificationTemplateDto createTemplate(NotificationTemplateDto dto) {
        NotificationTemplate template = templateMapper.toDomain(dto);
        NotificationTemplate saved = templateRepository.save(template);
        return templateMapper.toDto(saved);
    }

    @Override
    public NotificationTemplateDto getTemplateById(UUID id) {
        NotificationTemplate template = templateRepository.findById(id)
                .orElseThrow(() -> new NotificationTemplateNotFoundException(id));
        return templateMapper.toDto(template);
    }

    @Override
    public NotificationTemplateDto getTemplateByName(String name) {
        NotificationTemplate template = templateRepository.findByName(name)
                .orElseThrow(() -> new NotificationTemplateNotFoundException(name));
        return templateMapper.toDto(template);
    }

    @Override
    public List<NotificationTemplateDto> getAllTemplates() {
        return templateRepository.findAll().stream()
                .map(templateMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public NotificationTemplateDto updateTemplate(UUID id, NotificationTemplateDto dto) {
        NotificationTemplate template = templateRepository.findById(id)
                .orElseThrow(() -> new NotificationTemplateNotFoundException(id));

        if (dto.getName() != null)
            template.setName(dto.getName());
        if (dto.getBodyTemplate() != null)
            template.setBodyTemplate(dto.getBodyTemplate());
        if (dto.getStatus() != null)
            template.setStatus(dto.getStatus());

        NotificationTemplate saved = templateRepository.save(template);
        return templateMapper.toDto(saved);
    }

    @Override
    @Transactional
    public void deleteTemplate(UUID id) {
        if (templateRepository.findById(id).isEmpty()) {
            throw new NotificationTemplateNotFoundException(id);
        }
        templateRepository.deleteById(id);
    }
}
