package com.pfe.notification.application.mapper;

import com.pfe.notification.application.dto.CreateNotificationRequest;
import com.pfe.notification.application.dto.NotificationDto;
import com.pfe.notification.domain.model.Notification;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface NotificationMapper {

    NotificationDto toDto(Notification notification);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "sentAt", ignore = true)
    Notification toDomain(CreateNotificationRequest request);
}
