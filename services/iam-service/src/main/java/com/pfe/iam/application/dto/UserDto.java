package com.pfe.iam.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private String id;
    private String username;
    private String email;
    private boolean active;
    private String role;
    private List<RoleDto> roles;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
