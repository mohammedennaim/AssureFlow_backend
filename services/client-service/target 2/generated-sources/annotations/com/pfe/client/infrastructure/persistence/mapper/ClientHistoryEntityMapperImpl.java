package com.pfe.client.infrastructure.persistence.mapper;

import com.pfe.client.domain.model.ClientHistory;
import com.pfe.client.infrastructure.persistence.entity.ClientHistoryEntity;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-03-14T22:53:30+0000",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.45.0.v20260224-0835, environment: Java 21.0.10 (Eclipse Adoptium)"
)
@Component
public class ClientHistoryEntityMapperImpl implements ClientHistoryEntityMapper {

    @Override
    public ClientHistoryEntity toEntity(ClientHistory history) {
        if ( history == null ) {
            return null;
        }

        ClientHistoryEntity.ClientHistoryEntityBuilder clientHistoryEntity = ClientHistoryEntity.builder();

        clientHistoryEntity.action( history.getAction() );
        clientHistoryEntity.clientId( history.getClientId() );
        clientHistoryEntity.id( history.getId() );
        clientHistoryEntity.performedAt( history.getPerformedAt() );
        clientHistoryEntity.performedBy( history.getPerformedBy() );

        return clientHistoryEntity.build();
    }

    @Override
    public ClientHistory toDomain(ClientHistoryEntity entity) {
        if ( entity == null ) {
            return null;
        }

        ClientHistory.ClientHistoryBuilder clientHistory = ClientHistory.builder();

        clientHistory.action( entity.getAction() );
        clientHistory.clientId( entity.getClientId() );
        clientHistory.id( entity.getId() );
        clientHistory.performedAt( entity.getPerformedAt() );
        clientHistory.performedBy( entity.getPerformedBy() );

        return clientHistory.build();
    }

    @Override
    public List<ClientHistory> toDomainList(List<ClientHistoryEntity> entities) {
        if ( entities == null ) {
            return null;
        }

        List<ClientHistory> list = new ArrayList<ClientHistory>( entities.size() );
        for ( ClientHistoryEntity clientHistoryEntity : entities ) {
            list.add( toDomain( clientHistoryEntity ) );
        }

        return list;
    }
}
