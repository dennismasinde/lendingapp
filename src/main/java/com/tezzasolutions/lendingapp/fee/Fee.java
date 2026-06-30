package com.tezzasolutions.lendingapp.fee;

import com.tezzasolutions.lendingapp.common.audit.BaseEntity;
import com.tezzasolutions.lendingapp.common.enums.FeeApplicationTiming;
import com.tezzasolutions.lendingapp.common.enums.FeeCalculationType;
import com.tezzasolutions.lendingapp.common.enums.FeeType;
import com.tezzasolutions.lendingapp.loan.LoanFee;
import com.tezzasolutions.lendingapp.loan.LoanProduct;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "fees")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Fee extends BaseEntity {

    @Column(nullable = false, length = 200)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "fee_type", nullable = false)
    private FeeType feeType;

    @Enumerated(EnumType.STRING)
    @Column(name = "calculation_type", nullable = false)
    private FeeCalculationType calculationType;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Column(precision = 5, scale = 2)
    private BigDecimal percentage;

    @Column(name = "days_after_due")
    private Integer daysAfterDue;

    @Enumerated(EnumType.STRING)
    @Column(name = "application_timing")
    private FeeApplicationTiming applicationTiming;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "loan_product_id", nullable = false)
    private LoanProduct loanProduct;

    @OneToMany(mappedBy = "fee", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<LoanFee> loanFees = new ArrayList<>();
}
