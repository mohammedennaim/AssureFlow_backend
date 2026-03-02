package com.pfe.iam.application.mapper;

import com.pfe.iam.application.dto.RegisterRequest;
import com.pfe.iam.application.dto.UserDto;
import com.pfe.iam.domain.model.Role;
import com.pfe.iam.domain.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "password", ignore = true)
    User toDomain(RegisterRequest request);

    @Mapping(target = "roles", expression = "java(mapRoles(user.getRoles()))")
    UserDto toDto(User user);

    default List<String> mapRoles(Set<Role> roles) {
        if (roles == null) return Collections.emptyList();
        return roles.stream()
                .map(role -> role.getName().name())
                .collect(Collectors.toList());
    }
}
