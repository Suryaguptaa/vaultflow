package com.finance.vaultflow.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class WeeklyTrendResponse {
    private String week;
    private BigDecimal income;
    private BigDecimal expense;
}