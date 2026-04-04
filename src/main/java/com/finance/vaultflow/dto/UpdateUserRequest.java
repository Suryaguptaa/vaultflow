package com.finance.vaultflow.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class UpdateUserRequest {

    @NotBlank
    @Size(max = 100)
    private String name;

    @NotBlank
    @Email
    private String email;
}