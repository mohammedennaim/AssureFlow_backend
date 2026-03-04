package com.pfe.notification.infrastructure.persistence.mapper;

import com.pfe.notification.domain.model.Notification;
import com.pfe.notification.infrastructure.persistence.entity.NotificationEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface NotificationEntityMapper {

    Notification toDomain(NotificationEntity entity);

    @Mapping(target = "createdAt", ignore = true)
    NotificationEntity toEntity(Notification domain);
}
