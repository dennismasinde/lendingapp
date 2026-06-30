package com.tezzasolutions.lendingapp.fee.dto;

import com.tezzasolutions.lendingapp.common.enums.FeeApplicationTiming;
import com.tezzasolutions.lendingapp.common.enums.FeeCalculationType;
import com.tezzasolutions.lendingapp.common.enums.FeeType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeeRequest {
    private String name;
    private FeeType feeType;
    private FeeCalculationType calculationType;
    private BigDecimal amount;
    private BigDecimal percentage;
    private Integer daysAfterDue;
    private FeeApplicationTiming applicationTiming;
    private Long loanProductId;
}

