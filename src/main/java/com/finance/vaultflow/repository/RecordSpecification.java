package com.finance.vaultflow.repository;

import com.finance.vaultflow.enums.TransactionType;
import com.finance.vaultflow.model.FinancialRecord;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class RecordSpecification {

    public static Specification<FinancialRecord> filter(
            TransactionType type,
            String category,
            LocalDate startDate,
            LocalDate endDate,
            String search) {

        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            predicates.add(cb.isNull(root.get("deletedAt")));

            if (type != null) {
                predicates.add(cb.equal(root.get("type"), type));
            }

            if (category != null && !category.isBlank()) {
                predicates.add(cb.equal(cb.lower(root.get("category")),
                        category.toLowerCase()));
            }

            if (startDate != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("date"), startDate));
            }

            if (endDate != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("date"), endDate));
            }

            if (search != null && !search.isBlank()) {
                String pattern = "%" + search.toLowerCase() + "%";
                Predicate categoryMatch = cb.like(cb.lower(root.get("category")), pattern);
                Predicate notesMatch = cb.like(cb.lower(root.get("notes")), pattern);
                predicates.add(cb.or(categoryMatch, notesMatch));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}