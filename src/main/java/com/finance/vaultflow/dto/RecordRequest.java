package com.finance.vaultflow.dto;

import com.finance.vaultflow.enums.TransactionType;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class RecordRequest {

    @NotNull
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    private BigDecimal amount;

    @NotNull
    private TransactionType type;

    @NotBlank
    @Size(max = 100)
    private String category;

    @NotNull
    @PastOrPresent(message = "Date must not be in the future")
    private LocalDate date;

    @Size(max = 500)
    private String notes;
}