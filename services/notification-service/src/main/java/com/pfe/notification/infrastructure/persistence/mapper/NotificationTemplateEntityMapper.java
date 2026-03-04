package com.pfe.notification.infrastructure.persistence.mapper;

import com.pfe.notification.domain.model.NotificationTemplate;
import com.pfe.notification.infrastructure.persistence.entity.NotificationTemplateEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface NotificationTemplateEntityMapper {

    NotificationTemplate toDomain(NotificationTemplateEntity entity);

    NotificationTemplateEntity toEntity(NotificationTemplate domain);
}
