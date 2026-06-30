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
public class LoanProductResponse {
    private Long id;
    private String name;
    private String description;
    private BigDecimal minAmount;
    private BigDecimal maxAmount;
    private BigDecimal interestRate;
    private Integer minTenure;
    private Integer maxTenure;
    private TenureType tenureType;
    private Boolean isActive;
    private String createdAt;
    private String updatedAt;

    public static LoanProductResponse from(LoanProduct product) {
        return LoanProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .minAmount(product.getMinAmount())
                .maxAmount(product.getMaxAmount())
                .interestRate(product.getInterestRate())
                .minTenure(product.getMinTenure())
                .maxTenure(product.getMaxTenure())
                .tenureType(product.getTenureType())
                .isActive(product.getIsActive())
                .createdAt(product.getCreatedAt() != null ? product.getCreatedAt().toString() : null)
                .updatedAt(product.getUpdatedAt() != null ? product.getUpdatedAt().toString() : null)
                .build();
    }
}
