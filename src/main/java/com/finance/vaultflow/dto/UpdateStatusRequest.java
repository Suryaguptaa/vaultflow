package com.finance.vaultflow.dto;

import com.finance.vaultflow.enums.UserStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateStatusRequest {

    @NotNull
    private UserStatus status;
}