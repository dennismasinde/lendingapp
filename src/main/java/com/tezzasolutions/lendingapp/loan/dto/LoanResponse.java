package com.tezzasolutions.lendingapp.loan.dto;

import com.tezzasolutions.lendingapp.common.enums.BillingCycleType;
import com.tezzasolutions.lendingapp.common.enums.LoanStatus;
import com.tezzasolutions.lendingapp.common.enums.LoanType;
import com.tezzasolutions.lendingapp.common.enums.TenureType;
import com.tezzasolutions.lendingapp.loan.Loan;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoanResponse {
    private Long id;
    private Long customerId;
    private String customerName;
    private Long productId;
    private String productName;
    private BigDecimal principalAmount;
    private BigDecimal totalAmount;
    private BigDecimal outstandingBalance;
    private BigDecimal interestRate;
    private Integer tenure;
    private TenureType tenureType;
    private String disbursementDate;
    private String dueDate;
    private LoanStatus status;
    private LoanType loanType;
    private BillingCycleType billingCycleType;
    private Boolean isConsolidated;
    private String consolidatedGroupId;
    private String closedAt;
    private String cancelledAt;
    private String writtenOffAt;
    private String createdAt;
    private String updatedAt;

    public static LoanResponse from(Loan loan) {
        return LoanResponse.builder()
                .id(loan.getId())
                .customerId(loan.getCustomer().getId())
                .customerName(loan.getCustomer().getFullName())
                .productId(loan.getLoanProduct().getId())
                .productName(loan.getLoanProduct().getName())
                .principalAmount(loan.getPrincipalAmount())
                .totalAmount(loan.getTotalAmount())
                .outstandingBalance(loan.getOutstandingBalance())
                .interestRate(loan.getInterestRate())
                .tenure(loan.getTenure())
                .tenureType(loan.getTenureType())
                .disbursementDate(loan.getDisbursementDate() != null ? loan.getDisbursementDate().toString() : null)
                .dueDate(loan.getDueDate() != null ? loan.getDueDate().toString() : null)
                .status(loan.getStatus())
                .loanType(loan.getLoanType())
                .billingCycleType(loan.getBillingCycleType())
                .isConsolidated(loan.getIsConsolidated())
                .consolidatedGroupId(loan.getConsolidatedGroupId())
                .closedAt(loan.getClosedAt() != null ? loan.getClosedAt().toString() : null)
                .cancelledAt(loan.getCancelledAt() != null ? loan.getCancelledAt().toString() : null)
                .writtenOffAt(loan.getWrittenOffAt() != null ? loan.getWrittenOffAt().toString() : null)
                .createdAt(loan.getCreatedAt() != null ? loan.getCreatedAt().toString() : null)
                .updatedAt(loan.getUpdatedAt() != null ? loan.getUpdatedAt().toString() : null)
                .build();
    }
}
