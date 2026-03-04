package com.pfe.notification.infrastructure.persistence.mapper;

import com.pfe.notification.domain.model.NotificationLog;
import com.pfe.notification.infrastructure.persistence.entity.NotificationLogEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface NotificationLogEntityMapper {

    NotificationLog toDomain(NotificationLogEntity entity);

    NotificationLogEntity toEntity(NotificationLog domain);
}
