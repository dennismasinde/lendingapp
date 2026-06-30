package com.tezzasolutions.lendingapp.fee.dto;

import com.tezzasolutions.lendingapp.common.enums.FeeApplicationTiming;
import com.tezzasolutions.lendingapp.common.enums.FeeCalculationType;
import com.tezzasolutions.lendingapp.common.enums.FeeType;
import com.tezzasolutions.lendingapp.fee.Fee;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeeResponse {
    private Long id;
    private String name;
    private FeeType feeType;
    private FeeCalculationType calculationType;
    private BigDecimal amount;
    private BigDecimal percentage;
    private Integer daysAfterDue;
    private FeeApplicationTiming applicationTiming;
    private Long loanProductId;
    private Boolean isActive;
    private String createdAt;
    private String updatedAt;

    public static com.tezzasolutions.lendingapp.fee.dto.FeeResponse from(Fee fee) {
        return builder()
                .id(fee.getId())
                .name(fee.getName())
                .feeType(fee.getFeeType())
                .calculationType(fee.getCalculationType())
                .amount(fee.getAmount())
                .percentage(fee.getPercentage())
                .daysAfterDue(fee.getDaysAfterDue())
                .applicationTiming(fee.getApplicationTiming())
                .loanProductId(fee.getLoanProduct().getId())
                .isActive(fee.getIsActive())
                .createdAt(fee.getCreatedAt() != null ? fee.getCreatedAt().toString() : null)
                .updatedAt(fee.getUpdatedAt() != null ? fee.getUpdatedAt().toString() : null)
                .build();
    }
}
