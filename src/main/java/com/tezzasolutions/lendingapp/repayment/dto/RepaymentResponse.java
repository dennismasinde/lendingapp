package com.tezzasolutions.lendingapp.repayment.dto;

import com.tezzasolutions.lendingapp.common.enums.RepaymentMethod;
import com.tezzasolutions.lendingapp.common.enums.RepaymentStatus;
import com.tezzasolutions.lendingapp.repayment.Repayment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RepaymentResponse {
    private Long id;
    private Long loanId;
    private Long customerId;
    private String customerName;
    private BigDecimal amount;
    private String repaymentDate;
    private RepaymentMethod method;
    private String transactionReference;
    private RepaymentStatus status;
    private BigDecimal principalAmount;
    private BigDecimal interestAmount;
    private BigDecimal feeAmount;
    private BigDecimal penaltyAmount;
    private Boolean isReversed;
    private String createdAt;
    private String updatedAt;

    public static RepaymentResponse from(Repayment repayment) {
        return RepaymentResponse.builder()
                .id(repayment.getId())
                .loanId(repayment.getLoan().getId())
                .customerId(repayment.getCustomer().getId())
                .customerName(repayment.getCustomer().getFullName())
                .amount(repayment.getAmount())
                .repaymentDate(repayment.getRepaymentDate() != null ? repayment.getRepaymentDate().toString() : null)
                .method(repayment.getMethod())
                .transactionReference(repayment.getTransactionReference())
                .status(repayment.getStatus())
                .principalAmount(repayment.getPrincipalAmount())
                .interestAmount(repayment.getInterestAmount())
                .feeAmount(repayment.getFeeAmount())
                .penaltyAmount(repayment.getPenaltyAmount())
                .isReversed(repayment.getIsReversed())
                .createdAt(repayment.getCreatedAt() != null ? repayment.getCreatedAt().toString() : null)
                .updatedAt(repayment.getUpdatedAt() != null ? repayment.getUpdatedAt().toString() : null)
                .build();
    }
}
