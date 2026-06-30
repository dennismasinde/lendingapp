package com.tezzasolutions.lendingapp.loan;

import com.tezzasolutions.lendingapp.common.audit.BaseEntity;
import com.tezzasolutions.lendingapp.common.enums.FeeApplicationTiming;
import com.tezzasolutions.lendingapp.fee.Fee;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "loan_fees")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoanFee extends BaseEntity {

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Column(name = "applied_date", nullable = false)
    private Instant appliedDate;

    @Column(name = "paid_date")
    private Instant paidDate;

    @Column(name = "is_paid", nullable = false)
    @Builder.Default
    private Boolean isPaid = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "application_timing", nullable = false)
    private FeeApplicationTiming applicationTiming;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "loan_id", nullable = false)
    private Loan loan;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fee_id", nullable = false)
    private Fee fee;
}
