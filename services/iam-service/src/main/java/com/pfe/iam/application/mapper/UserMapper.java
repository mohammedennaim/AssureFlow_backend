package com.pfe.iam.application.mapper;

import com.pfe.iam.application.dto.RegisterRequest;
import com.pfe.iam.application.dto.UserDto;
import com.pfe.iam.domain.model.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toDomain(RegisterRequest request);

    UserDto toDto(User user);
}
