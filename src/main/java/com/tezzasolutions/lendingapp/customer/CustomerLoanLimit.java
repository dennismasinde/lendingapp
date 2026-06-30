package com.tezzasolutions.lendingapp.customer;

import com.tezzasolutions.lendingapp.common.audit.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "customer_loan_limits")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerLoanLimit extends BaseEntity {

    @Column(name = "max_loan_amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal maxLoanAmount;

    @Column(name = "total_outstanding_limit", nullable = false, precision = 19, scale = 2)
    private BigDecimal totalOutstandingLimit;

    @Column(name = "available_limit", nullable = false, precision = 19, scale = 2)
    private BigDecimal availableLimit;

    @Column(name = "max_number_of_loans", nullable = false)
    private Integer maxNumberOfLoans;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    @Column(name = "last_review_date", nullable = false)
    private Instant lastReviewDate;

    @Column(name = "risk_level", length = 20)
    private String riskLevel;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false, unique = true)
    private Customer customer;
}
