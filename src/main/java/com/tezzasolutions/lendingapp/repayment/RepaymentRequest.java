package com.tezzasolutions.lendingapp.repayment;

import com.tezzasolutions.lendingapp.common.enums.RepaymentMethod;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class RepaymentRequest {
    private Long loanId;
    private BigDecimal amount;
    private RepaymentMethod method;
    private String reference;
}
