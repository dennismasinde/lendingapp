package com.tezzasolutions.lendingapp.loan;

import com.tezzasolutions.lendingapp.common.audit.BaseEntity;
import com.tezzasolutions.lendingapp.common.enums.TenureType;
import com.tezzasolutions.lendingapp.fee.Fee;
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
@Table(name = "loan_products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoanProduct extends BaseEntity {

    @Column(unique = true, nullable = false, length = 200)
    private String name;

    @Column(length = 1000)
    private String description;

    @Column(name = "min_amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal minAmount;

    @Column(name = "max_amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal maxAmount;

    @Column(name = "interest_rate", nullable = false, precision = 5, scale = 2)
    private BigDecimal interestRate;

    @Column(name = "min_tenure", nullable = false)
    private Integer minTenure;

    @Column(name = "max_tenure", nullable = false)
    private Integer maxTenure;

    @Enumerated(EnumType.STRING)
    @Column(name = "tenure_type", nullable = false)
    private TenureType tenureType;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    @OneToMany(mappedBy = "loanProduct", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Fee> fees = new ArrayList<>();

    @OneToMany(mappedBy = "loanProduct", fetch = FetchType.LAZY)
    @Builder.Default
    private List<Loan> loans = new ArrayList<>();
}
