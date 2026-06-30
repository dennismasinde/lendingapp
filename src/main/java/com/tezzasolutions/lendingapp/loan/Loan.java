package com.tezzasolutions.lendingapp.loan;

import com.tezzasolutions.lendingapp.common.audit.BaseEntity;
import com.tezzasolutions.lendingapp.common.enums.BillingCycleType;
import com.tezzasolutions.lendingapp.common.enums.LoanStatus;
import com.tezzasolutions.lendingapp.common.enums.LoanType;
import com.tezzasolutions.lendingapp.common.enums.TenureType;
import com.tezzasolutions.lendingapp.customer.Customer;
import com.tezzasolutions.lendingapp.installment.Installment;
import com.tezzasolutions.lendingapp.repayment.Repayment;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "loans")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Loan extends BaseEntity {

    @Column(name = "principal_amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal principalAmount;

    @Column(name = "total_amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal totalAmount;

    @Column(name = "outstanding_balance", nullable = false, precision = 19, scale = 2)
    private BigDecimal outstandingBalance;

    @Column(name = "interest_rate", nullable = false, precision = 5, scale = 2)
    private BigDecimal interestRate;

    @Column(nullable = false)
    private Integer tenure;

    @Enumerated(EnumType.STRING)
    @Column(name = "tenure_type", nullable = false)
    private TenureType tenureType;

    @Column(name = "disbursement_date", nullable = false)
    private Instant disbursementDate;

    @Column(name = "due_date")
    private Instant dueDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LoanStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "loan_type", nullable = false)
    private LoanType loanType;

    @Enumerated(EnumType.STRING)
    @Column(name = "billing_cycle_type")
    private BillingCycleType billingCycleType;

    @Column(name = "consolidated_due_date")
    private Instant consolidatedDueDate;

    @Column(name = "is_consolidated", nullable = false)
    @Builder.Default
    private Boolean isConsolidated = false;

    @Column(name = "consolidated_group_id", length = 50)
    private String consolidatedGroupId;

    @Column(name = "closed_at")
    private Instant closedAt;

    @Column(name = "cancelled_at")
    private Instant cancelledAt;

    @Column(name = "written_off_at")
    private Instant writtenOffAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private LoanProduct loanProduct;

    @OneToMany(mappedBy = "loan", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Installment> installments = new ArrayList<>();

    @OneToMany(mappedBy = "loan", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Repayment> repayments = new ArrayList<>();

    @OneToMany(mappedBy = "loan", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<LoanFee> loanFees = new ArrayList<>();
}
