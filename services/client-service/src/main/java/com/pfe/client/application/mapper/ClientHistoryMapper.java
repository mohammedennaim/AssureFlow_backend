package com.pfe.client.application.mapper;

import com.pfe.client.application.dto.ClientHistoryResponse;
import com.pfe.client.domain.model.ClientHistory;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ClientHistoryMapper {
    ClientHistoryMapper INSTANCE = Mappers.getMapper(ClientHistoryMapper.class);

    ClientHistoryResponse toResponse(ClientHistory history);

    List<ClientHistoryResponse> toResponseList(List<ClientHistory> histories);
}
