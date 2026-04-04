package com.finance.vaultflow.controller;

import com.finance.vaultflow.dto.*;
import com.finance.vaultflow.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@Tag(name = "Dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/summary")
    @PreAuthorize("hasAnyRole('VIEWER', 'ANALYST', 'ADMIN')")
    @Operation(summary = "Get total income, expenses, and net balance")
    public ResponseEntity<DashboardSummaryResponse> getSummary() {
        return ResponseEntity.ok(dashboardService.getSummary());
    }

    @GetMapping("/by-category")
    @PreAuthorize("hasAnyRole('VIEWER', 'ANALYST', 'ADMIN')")
    @Operation(summary = "Get totals grouped by category")
    public ResponseEntity<List<CategoryTotalResponse>> getByCategory() {
        return ResponseEntity.ok(dashboardService.getByCategory());
    }

    @GetMapping("/recent")
    @PreAuthorize("hasAnyRole('VIEWER', 'ANALYST', 'ADMIN')")
    @Operation(summary = "Get last 10 recent transactions")
    public ResponseEntity<List<RecordResponse>> getRecentTransactions() {
        return ResponseEntity.ok(dashboardService.getRecentTransactions());
    }

    @GetMapping("/trends/monthly")
    @PreAuthorize("hasAnyRole('VIEWER', 'ANALYST', 'ADMIN')")
    @Operation(summary = "Get income vs expense per month")
    public ResponseEntity<List<MonthlyTrendResponse>> getMonthlyTrends() {
        return ResponseEntity.ok(dashboardService.getMonthlyTrends());
    }

    @GetMapping("/trends/weekly")
    @PreAuthorize("hasAnyRole('VIEWER', 'ANALYST', 'ADMIN')")
    @Operation(summary = "Get income vs expense per week")
    public ResponseEntity<List<WeeklyTrendResponse>> getWeeklyTrends() {
        return ResponseEntity.ok(dashboardService.getWeeklyTrends());
    }
}