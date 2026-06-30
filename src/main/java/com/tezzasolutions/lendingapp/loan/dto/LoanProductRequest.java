package com.tezzasolutions.lendingapp.loan.dto;

import com.tezzasolutions.lendingapp.common.enums.TenureType;
import com.tezzasolutions.lendingapp.loan.LoanProduct;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoanProductRequest {
    private String name;
    private String description;
    private BigDecimal minAmount;
    private BigDecimal maxAmount;
    private BigDecimal interestRate;
    private Integer minTenure;
    private Integer maxTenure;
    private TenureType tenureType;
}

