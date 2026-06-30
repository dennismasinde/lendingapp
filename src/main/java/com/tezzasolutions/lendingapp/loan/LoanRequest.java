package com.tezzasolutions.lendingapp.loan;

import com.tezzasolutions.lendingapp.common.enums.BillingCycleType;
import com.tezzasolutions.lendingapp.common.enums.LoanType;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class LoanRequest {
    private Long customerId;
    private Long productId;
    private BigDecimal amount;
    private Integer tenure;
    private LoanType loanType;
    private BillingCycleType billingCycleType;
    private Boolean isConsolidated;
}
