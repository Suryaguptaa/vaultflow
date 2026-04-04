package com.finance.vaultflow.dto;

import com.finance.vaultflow.enums.Role;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateRoleRequest {

    @NotNull
    private Role role;
}