package com.pfe.iam.application.service;

import com.pfe.iam.application.dto.CreateUserRequest;
import com.pfe.iam.application.dto.UpdateUserRequest;
import com.pfe.iam.application.dto.UserDto;

import java.util.List;

public interface UserService {
    List<UserDto> getAllUsers();

    UserDto createUser(CreateUserRequest request);

    UserDto getUserById(String id);

    UserDto getUserByEmail(String email);

    UserDto updateUser(String id, UpdateUserRequest request);

    void deleteUser(String id);

    UserDto assignRole(String userId, String roleId);

    UserDto removeRole(String userId, String roleId);
}
