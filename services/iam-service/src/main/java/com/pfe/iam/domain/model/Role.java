package com.pfe.iam.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Role {
    private UUID id;
    private UserRole name;
    private String description;
    @Builder.Default
    private Set<Permission> permissions = new HashSet<>();

    public void grantPermission(Permission permission) {
        this.permissions.add(permission);
    }

    public void revokePermission(Permission permission) {
        this.permissions.remove(permission);
    }
}
