package com.tezzasolutions.lendingapp.customer.dto;

import com.tezzasolutions.lendingapp.customer.CustomerLoanLimit;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerLoanLimitResponse {
    private Long id;
    private Long customerId;
    private BigDecimal maxLoanAmount;
    private BigDecimal totalOutstandingLimit;
    private BigDecimal availableLimit;
    private Integer maxNumberOfLoans;
    private Boolean isActive;
    private String riskLevel;
    private String lastReviewDate;

    public static CustomerLoanLimitResponse from(CustomerLoanLimit limit) {
        return CustomerLoanLimitResponse.builder()
                .id(limit.getId())
                .customerId(limit.getCustomer().getId())
                .maxLoanAmount(limit.getMaxLoanAmount())
                .totalOutstandingLimit(limit.getTotalOutstandingLimit())
                .availableLimit(limit.getAvailableLimit())
                .maxNumberOfLoans(limit.getMaxNumberOfLoans())
                .isActive(limit.getIsActive())
                .riskLevel(limit.getRiskLevel())
                .lastReviewDate(limit.getLastReviewDate() != null ? limit.getLastReviewDate().toString() : null)
                .build();
    }
}
