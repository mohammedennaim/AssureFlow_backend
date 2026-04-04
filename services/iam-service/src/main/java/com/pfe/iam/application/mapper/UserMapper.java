package com.pfe.iam.application.mapper;

import com.pfe.iam.application.dto.RegisterRequest;
import com.pfe.iam.application.dto.UserDto;
import com.pfe.iam.domain.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "passwordHash", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    User toDomain(RegisterRequest request);

    @Mapping(target = "role", expression = "java(user.getRole() != null ? user.getRole().getName().name() : null)")
    @Mapping(target = "roles", ignore = true)
    UserDto toDto(User user);
}
