package com.tezzasolutions.lendingapp.repository;

import com.tezzasolutions.lendingapp.fee.*;
import com.tezzasolutions.lendingapp.loan.*;
import com.tezzasolutions.lendingapp.common.enums.FeeApplicationTiming;
import com.tezzasolutions.lendingapp.common.enums.FeeCalculationType;
import com.tezzasolutions.lendingapp.common.enums.FeeType;
import com.tezzasolutions.lendingapp.common.enums.TenureType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class FeeRepositoryTest {

    @Autowired
    private FeeRepository feeRepository;

    @Autowired
    private TestEntityManager entityManager;

    private LoanProduct product;
    private Fee fee1;
    private Fee fee2;
    private Fee fee3;

    @BeforeEach
    void setUp() {
        // Create loan product
        product = LoanProduct.builder()
                .name("Personal Loan")
                .minAmount(BigDecimal.valueOf(10000))
                .maxAmount(BigDecimal.valueOf(500000))
                .interestRate(BigDecimal.valueOf(12.5))
                .minTenure(1)
                .maxTenure(12)
                .tenureType(TenureType.MONTHS)
                .isActive(true)
                .build();
        product = entityManager.persist(product);

        // Create test fees
        fee1 = Fee.builder()
                .name("Service Fee")
                .feeType(FeeType.SERVICE)
                .calculationType(FeeCalculationType.PERCENTAGE)
                .amount(BigDecimal.ZERO)
                .percentage(BigDecimal.valueOf(2.5))
                .applicationTiming(FeeApplicationTiming.ORIGINATION)
                .isActive(true)
                .loanProduct(product)
                .build();

        fee2 = Fee.builder()
                .name("Late Payment Fee")
                .feeType(FeeType.LATE)
                .calculationType(FeeCalculationType.FIXED)
                .amount(BigDecimal.valueOf(1000))
                .percentage(BigDecimal.ZERO)
                .daysAfterDue(5)
                .applicationTiming(FeeApplicationTiming.OVERDUE)
                .isActive(true)
                .loanProduct(product)
                .build();

        fee3 = Fee.builder()
                .name("Daily Maintenance Fee")
                .feeType(FeeType.DAILY)
                .calculationType(FeeCalculationType.FIXED)
                .amount(BigDecimal.valueOf(50))
                .percentage(BigDecimal.ZERO)
                .applicationTiming(FeeApplicationTiming.POST_DISBURSEMENT)
                .isActive(false)
                .loanProduct(product)
                .build();

        // Save fees
        fee1 = feeRepository.save(fee1);
        fee2 = feeRepository.save(fee2);
        fee3 = feeRepository.save(fee3);
    }

    @Test
    void shouldSaveAndFindFee() {
        // Given
        Fee newFee = Fee.builder()
                .name("Processing Fee")
                .feeType(FeeType.PROCESSING)
                .calculationType(FeeCalculationType.FIXED)
                .amount(BigDecimal.valueOf(500))
                .percentage(BigDecimal.ZERO)
                .applicationTiming(FeeApplicationTiming.ORIGINATION)
                .isActive(true)
                .loanProduct(product)
                .build();

        // When
        Fee saved = feeRepository.save(newFee);

        // Then
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getCreatedAt()).isNotNull();
        assertThat(saved.getName()).isEqualTo("Processing Fee");

        // When
        Fee found = feeRepository.findById(saved.getId()).orElse(null);

        // Then
        assertThat(found).isNotNull();
        assertThat(found.getFeeType()).isEqualTo(FeeType.PROCESSING);
        assertThat(found.getAmount()).isEqualByComparingTo(BigDecimal.valueOf(500));
    }

    @Test
    void shouldFindFeesByLoanProductId() {
        // When
        List<Fee> fees = feeRepository.findByLoanProductId(product.getId());

        // Then
        assertThat(fees).hasSize(3);
        assertThat(fees)
                .extracting(Fee::getName)
                .containsExactlyInAnyOrder("Service Fee", "Late Payment Fee", "Daily Maintenance Fee");
    }

    @Test
    void shouldFindActiveFeesByProduct() {
        // When
        List<Fee> activeFees = feeRepository.findActiveFeesByProduct(product.getId());

        // Then
        assertThat(activeFees).hasSize(2);
        assertThat(activeFees)
                .extracting(Fee::getName)
                .containsExactlyInAnyOrder("Service Fee", "Late Payment Fee");
        assertThat(activeFees).allMatch(Fee::getIsActive);
    }

    @Test
    void shouldFindFeesByProductAndType() {
        // When
        List<Fee> serviceFees = feeRepository.findFeesByProductAndType(
                product.getId(),
                FeeType.SERVICE
        );
        List<Fee> lateFees = feeRepository.findFeesByProductAndType(
                product.getId(),
                FeeType.LATE
        );

        // Then
        assertThat(serviceFees).hasSize(1);
        assertThat(serviceFees.get(0).getName()).isEqualTo("Service Fee");
        assertThat(lateFees).hasSize(1);
        assertThat(lateFees.get(0).getName()).isEqualTo("Late Payment Fee");
    }

    @Test
    void shouldFindFeesByProductAndApplicationTiming() {
        // When
        List<Fee> originationFees = feeRepository.findFeesByProductAndApplicationTiming(
                product.getId(),
                FeeApplicationTiming.ORIGINATION
        );
        List<Fee> overdueFees = feeRepository.findFeesByProductAndApplicationTiming(
                product.getId(),
                FeeApplicationTiming.OVERDUE
        );

        // Then
        assertThat(originationFees).hasSize(1);
        assertThat(originationFees.get(0).getName()).isEqualTo("Service Fee");
        assertThat(overdueFees).hasSize(1);
        assertThat(overdueFees.get(0).getName()).isEqualTo("Late Payment Fee");
    }

    @Test
    void shouldFindLateFeeConfigurations() {
        // When
        List<Fee> lateFeeConfigs = feeRepository.findLateFeeConfigurations(product.getId());

        // Then
        assertThat(lateFeeConfigs).hasSize(1);
        assertThat(lateFeeConfigs.get(0).getName()).isEqualTo("Late Payment Fee");
        assertThat(lateFeeConfigs.get(0).getDaysAfterDue()).isEqualTo(5);
    }

    @Test
    void shouldFindAllActiveFees() {
        // When
        List<Fee> allActiveFees = feeRepository.findByIsActiveTrue();

        // Then
        assertThat(allActiveFees).hasSize(2);
        assertThat(allActiveFees)
                .extracting(Fee::getName)
                .containsExactlyInAnyOrder("Service Fee", "Late Payment Fee");
    }

    @Test
    void shouldUpdateFee() {
        // Given
        Fee fee = feeRepository.findById(fee1.getId()).orElseThrow();
        fee.setPercentage(BigDecimal.valueOf(3.0));
        fee.setAmount(BigDecimal.valueOf(100));

        // When
        Fee updated = feeRepository.save(fee);

        // Then
        assertThat(updated.getPercentage()).isEqualByComparingTo(BigDecimal.valueOf(3.0));
        assertThat(updated.getAmount()).isEqualByComparingTo(BigDecimal.valueOf(100));
        assertThat(updated.getUpdatedAt()).isAfter(fee.getCreatedAt());
    }

    @Test
    void shouldDeleteFee() {
        // Given
        Fee fee = Fee.builder()
                .name("Temporary Fee")
                .feeType(FeeType.PROCESSING)
                .calculationType(FeeCalculationType.FIXED)
                .amount(BigDecimal.valueOf(200))
                .percentage(BigDecimal.ZERO)
                .isActive(true)
                .loanProduct(product)
                .build();
        fee = feeRepository.save(fee);
        Long feeId = fee.getId();

        // When
        feeRepository.deleteById(feeId);

        // Then
        Fee found = feeRepository.findById(feeId).orElse(null);
        assertThat(found).isNull();
    }
}
