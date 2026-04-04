package com.finance.vaultflow.service;

import com.finance.vaultflow.dto.*;
import com.finance.vaultflow.enums.TransactionType;
import com.finance.vaultflow.exception.AppException;
import com.finance.vaultflow.model.FinancialRecord;
import com.finance.vaultflow.model.User;
import com.finance.vaultflow.repository.FinancialRecordRepository;
import com.finance.vaultflow.repository.RecordSpecification;
import com.finance.vaultflow.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FinancialRecordService {

    private final FinancialRecordRepository recordRepository;
    private final UserRepository userRepository;

    public PagedResponse<RecordResponse> getAllRecords(
            int page, int size, String sortBy, String sortDir,
            TransactionType type, String category,
            LocalDate startDate, LocalDate endDate, String search) {

        Sort sort = sortDir.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Specification<FinancialRecord> spec = RecordSpecification.filter(type, category, startDate, endDate, search);
        Page<FinancialRecord> result = recordRepository.findAll(spec, pageable);

        return PagedResponse.<RecordResponse>builder()
                .data(result.getContent().stream().map(this::toResponse).toList())
                .page(result.getNumber())
                .size(result.getSize())
                .totalElements(result.getTotalElements())
                .totalPages(result.getTotalPages())
                .build();
    }

    public RecordResponse getRecordById(UUID id) {
        return toResponse(findRecordOrThrow(id));
    }

    public RecordResponse createRecord(RecordRequest request) {
        User currentUser = getCurrentUser();
        FinancialRecord record = FinancialRecord.builder()
                .user(currentUser)
                .amount(request.getAmount())
                .type(request.getType())
                .category(request.getCategory())
                .date(request.getDate())
                .notes(request.getNotes())
                .build();
        return toResponse(recordRepository.save(record));
    }

    public RecordResponse updateRecord(UUID id, RecordRequest request) {
        FinancialRecord record = findRecordOrThrow(id);
        record.setAmount(request.getAmount());
        record.setType(request.getType());
        record.setCategory(request.getCategory());
        record.setDate(request.getDate());
        record.setNotes(request.getNotes());
        return toResponse(recordRepository.save(record));
    }

    public void softDeleteRecord(UUID id) {
        FinancialRecord record = findRecordOrThrow(id);
        record.setDeletedAt(LocalDateTime.now());
        recordRepository.save(record);
    }

    private FinancialRecord findRecordOrThrow(UUID id) {

        return recordRepository.findById(id)
                .orElseThrow(() -> new AppException("Record not found", HttpStatus.NOT_FOUND));
    }

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException("Authenticated user not found", HttpStatus.UNAUTHORIZED));
    }

    private RecordResponse toResponse(FinancialRecord r) {
        return RecordResponse.builder()
                .id(r.getId())
                .userId(r.getUser().getId())
                .amount(r.getAmount())
                .type(r.getType())
                .category(r.getCategory())
                .date(r.getDate())
                .notes(r.getNotes())
                .createdAt(r.getCreatedAt())
                .updatedAt(r.getUpdatedAt())
                .build();
    }
}