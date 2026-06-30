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
public class LoanRequest {
    private Long customerId;
    private Long productId;
    private BigDecimal amount;
    private Integer tenure;
    private LoanType loanType;
    private BillingCycleType billingCycleType;
    private Boolean isConsolidated;
}

