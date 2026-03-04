package com.pfe.notification.application.mapper;

import com.pfe.notification.application.dto.NotificationTemplateDto;
import com.pfe.notification.domain.model.NotificationTemplate;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface NotificationTemplateMapper {

    NotificationTemplateDto toDto(NotificationTemplate template);

    @Mapping(target = "id", ignore = true)
    NotificationTemplate toDomain(NotificationTemplateDto dto);
}
