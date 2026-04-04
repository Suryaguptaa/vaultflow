package com.finance.vaultflow.repository;

import com.finance.vaultflow.enums.TransactionType;
import com.finance.vaultflow.model.FinancialRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface FinancialRecordRepository extends JpaRepository<FinancialRecord, UUID>,
        JpaSpecificationExecutor<FinancialRecord> {

    @Query("SELECT SUM(r.amount) FROM FinancialRecord r WHERE r.type = :type AND r.deletedAt IS NULL")
    java.math.BigDecimal sumByType(TransactionType type);

    @Query("SELECT r.category, SUM(r.amount) FROM FinancialRecord r WHERE r.deletedAt IS NULL GROUP BY r.category")
    List<Object[]> sumGroupedByCategory();

    @Query("SELECT r FROM FinancialRecord r WHERE r.deletedAt IS NULL ORDER BY r.date DESC")
    List<FinancialRecord> findTop10Recent(org.springframework.data.domain.Pageable pageable);

    @Query("SELECT FUNCTION('TO_CHAR', r.date, 'YYYY-MM') as month, r.type, SUM(r.amount) " +
            "FROM FinancialRecord r WHERE r.deletedAt IS NULL " +
            "GROUP BY FUNCTION('TO_CHAR', r.date, 'YYYY-MM'), r.type ORDER BY month")
    List<Object[]> monthlyTrends();

    @Query("SELECT FUNCTION('TO_CHAR', r.date, 'IYYY-IW') as week, r.type, SUM(r.amount) " +
            "FROM FinancialRecord r WHERE r.deletedAt IS NULL " +
            "GROUP BY FUNCTION('TO_CHAR', r.date, 'IYYY-IW'), r.type ORDER BY week")
    List<Object[]> weeklyTrends();
}