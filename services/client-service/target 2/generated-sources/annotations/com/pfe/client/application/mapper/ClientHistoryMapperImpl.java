package com.pfe.client.application.mapper;

import com.pfe.client.application.dto.ClientHistoryResponse;
import com.pfe.client.domain.model.ClientHistory;
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
public class ClientHistoryMapperImpl implements ClientHistoryMapper {

    @Override
    public ClientHistoryResponse toResponse(ClientHistory history) {
        if ( history == null ) {
            return null;
        }

        ClientHistoryResponse.ClientHistoryResponseBuilder clientHistoryResponse = ClientHistoryResponse.builder();

        clientHistoryResponse.action( history.getAction() );
        clientHistoryResponse.clientId( history.getClientId() );
        clientHistoryResponse.id( history.getId() );
        clientHistoryResponse.performedAt( history.getPerformedAt() );
        clientHistoryResponse.performedBy( history.getPerformedBy() );

        return clientHistoryResponse.build();
    }

    @Override
    public List<ClientHistoryResponse> toResponseList(List<ClientHistory> histories) {
        if ( histories == null ) {
            return null;
        }

        List<ClientHistoryResponse> list = new ArrayList<ClientHistoryResponse>( histories.size() );
        for ( ClientHistory clientHistory : histories ) {
            list.add( toResponse( clientHistory ) );
        }

        return list;
    }
}
