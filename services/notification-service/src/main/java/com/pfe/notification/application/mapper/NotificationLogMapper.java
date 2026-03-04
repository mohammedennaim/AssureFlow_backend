package com.pfe.notification.application.mapper;

import com.pfe.notification.application.dto.NotificationLogDto;
import com.pfe.notification.domain.model.NotificationLog;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface NotificationLogMapper {

    NotificationLogDto toDto(NotificationLog log);

    NotificationLog toDomain(NotificationLogDto dto);
}
