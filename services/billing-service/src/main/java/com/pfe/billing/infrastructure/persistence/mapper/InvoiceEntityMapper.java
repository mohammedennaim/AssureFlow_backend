package com.pfe.billing.infrastructure.persistence.mapper;

import com.pfe.billing.domain.model.Invoice;
import com.pfe.billing.infrastructure.persistence.entity.InvoiceEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface InvoiceEntityMapper {

    Invoice toDomain(InvoiceEntity entity);

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    InvoiceEntity toEntity(Invoice domain);
}
