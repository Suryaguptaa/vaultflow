package com.finance.vaultflow.service;

import com.finance.vaultflow.dto.*;
import com.finance.vaultflow.enums.TransactionType;
import com.finance.vaultflow.model.FinancialRecord;
import com.finance.vaultflow.repository.FinancialRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final FinancialRecordRepository recordRepository;

    public DashboardSummaryResponse getSummary() {
        BigDecimal totalIncome = recordRepository.sumByType(TransactionType.INCOME);
        BigDecimal totalExpenses = recordRepository.sumByType(TransactionType.EXPENSE);

        totalIncome = totalIncome != null ? totalIncome : BigDecimal.ZERO;
        totalExpenses = totalExpenses != null ? totalExpenses : BigDecimal.ZERO;

        return DashboardSummaryResponse.builder()
                .totalIncome(totalIncome)
                .totalExpenses(totalExpenses)
                .netBalance(totalIncome.subtract(totalExpenses))
                .currency("USD")
                .build();
    }

    public List<CategoryTotalResponse> getByCategory() {
        List<Object[]> rows = recordRepository.sumGroupedByCategory();
        List<CategoryTotalResponse> result = new ArrayList<>();
        for (Object[] row : rows) {
            result.add(new CategoryTotalResponse(
                    (String) row[0],
                    (BigDecimal) row[1]
            ));
        }
        return result;
    }

    public List<RecordResponse> getRecentTransactions() {
        List<FinancialRecord> records = recordRepository.findTop10Recent(PageRequest.of(0, 10));
        return records.stream().map(r -> RecordResponse.builder()
                .id(r.getId())
                .userId(r.getUser().getId())
                .amount(r.getAmount())
                .type(r.getType())
                .category(r.getCategory())
                .date(r.getDate())
                .notes(r.getNotes())
                .createdAt(r.getCreatedAt())
                .updatedAt(r.getUpdatedAt())
                .build()
        ).toList();
    }

    public List<MonthlyTrendResponse> getMonthlyTrends() {
        List<Object[]> rows = recordRepository.monthlyTrends();
        return buildTrends(rows, MonthlyTrendResponse.class);
    }

    public List<WeeklyTrendResponse> getWeeklyTrends() {
        List<Object[]> rows = recordRepository.weeklyTrends();
        return buildTrendsWeekly(rows);
    }

    private List<MonthlyTrendResponse> buildTrends(List<Object[]> rows, Class<?> clazz) {
        Map<String, BigDecimal[]> map = new LinkedHashMap<>();
        for (Object[] row : rows) {
            String period = (String) row[0];
            TransactionType type = (TransactionType) row[1];
            BigDecimal amount = (BigDecimal) row[2];
            map.putIfAbsent(period, new BigDecimal[]{BigDecimal.ZERO, BigDecimal.ZERO});
            if (type == TransactionType.INCOME) {
                map.get(period)[0] = amount;
            } else {
                map.get(period)[1] = amount;
            }
        }
        List<MonthlyTrendResponse> result = new ArrayList<>();
        map.forEach((period, amounts) ->
                result.add(new MonthlyTrendResponse(period, amounts[0], amounts[1])));
        return result;
    }

    private List<WeeklyTrendResponse> buildTrendsWeekly(List<Object[]> rows) {
        Map<String, BigDecimal[]> map = new LinkedHashMap<>();
        for (Object[] row : rows) {
            String period = (String) row[0];
            TransactionType type = (TransactionType) row[1];
            BigDecimal amount = (BigDecimal) row[2];
            map.putIfAbsent(period, new BigDecimal[]{BigDecimal.ZERO, BigDecimal.ZERO});
            if (type == TransactionType.INCOME) {
                map.get(period)[0] = amount;
            } else {
                map.get(period)[1] = amount;
            }
        }
        List<WeeklyTrendResponse> result = new ArrayList<>();
        map.forEach((period, amounts) ->
                result.add(new WeeklyTrendResponse(period, amounts[0], amounts[1])));
        return result;
    }
}