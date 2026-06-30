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
public class RepaymentRequest {
    private Long loanId;
    private BigDecimal amount;
    private RepaymentMethod method;
    private String reference;
}

