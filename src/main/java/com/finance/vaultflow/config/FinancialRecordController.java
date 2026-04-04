package com.finance.vaultflow.controller;

import com.finance.vaultflow.dto.*;
import com.finance.vaultflow.enums.TransactionType;
import com.finance.vaultflow.service.FinancialRecordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequestMapping("/api/records")
@RequiredArgsConstructor
@Tag(name = "Financial Records")
public class FinancialRecordController {

    private final FinancialRecordService recordService;

    @GetMapping
    @PreAuthorize("hasAnyRole('VIEWER', 'ANALYST', 'ADMIN')")
    @Operation(summary = "Get all records with filtering and pagination")
    public ResponseEntity<PagedResponse<RecordResponse>> getAllRecords(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "date") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(required = false) TransactionType type,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) String search) {

        return ResponseEntity.ok(recordService.getAllRecords(
                page, size, sortBy, sortDir, type, category, startDate, endDate, search));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('VIEWER', 'ANALYST', 'ADMIN')")
    @Operation(summary = "Get a single record by ID")
    public ResponseEntity<RecordResponse> getRecordById(@PathVariable UUID id) {
        return ResponseEntity.ok(recordService.getRecordById(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ANALYST', 'ADMIN')")
    @Operation(summary = "Create a new financial record")
    public ResponseEntity<RecordResponse> createRecord(@Valid @RequestBody RecordRequest request) {
        return ResponseEntity.ok(recordService.createRecord(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ANALYST', 'ADMIN')")
    @Operation(summary = "Update an existing financial record")
    public ResponseEntity<RecordResponse> updateRecord(@PathVariable UUID id,
                                                       @Valid @RequestBody RecordRequest request) {
        return ResponseEntity.ok(recordService.updateRecord(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Soft delete a financial record")
    public ResponseEntity<Void> deleteRecord(@PathVariable UUID id) {
        recordService.softDeleteRecord(id);
        return ResponseEntity.noContent().build();
    }
}