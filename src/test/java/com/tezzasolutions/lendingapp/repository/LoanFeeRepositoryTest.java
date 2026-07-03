package com.tezzasolutions.lendingapp.repository;

import com.tezzasolutions.lendingapp.customer.Customer;
import com.tezzasolutions.lendingapp.fee.Fee;
import com.tezzasolutions.lendingapp.loan.*;
import com.tezzasolutions.lendingapp.common.enums.TenureType;
import com.tezzasolutions.lendingapp.common.enums.FeeType;
import com.tezzasolutions.lendingapp.common.enums.FeeCalculationType;
import com.tezzasolutions.lendingapp.common.enums.FeeApplicationTiming;
import com.tezzasolutions.lendingapp.common.enums.LoanStatus;
import com.tezzasolutions.lendingapp.common.enums.LoanType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class LoanFeeRepositoryTest {

    @Autowired
    private LoanFeeRepository loanFeeRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Loan loan;
    private Fee fee1;
    private Fee fee2;
    private LoanFee loanFee1;
    private LoanFee loanFee2;

    @BeforeEach
    void setUp() {
        // Create customer
        Customer customer = Customer.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .phoneNumber("+254700000001")
                .employmentStatus("EMPLOYED")
                .build();
        customer = entityManager.persist(customer);

        // Create loan product
        LoanProduct product = LoanProduct.builder()
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

        // Create fees
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
        fee1 = entityManager.persist(fee1);

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
        fee2 = entityManager.persist(fee2);

        // Create loan
        loan = Loan.builder()
                .customer(customer)
                .loanProduct(product)
                .principalAmount(BigDecimal.valueOf(100000))
                .totalAmount(BigDecimal.valueOf(112500))
                .outstandingBalance(BigDecimal.valueOf(112500))
                .interestRate(BigDecimal.valueOf(12.5))
                .tenure(12)
                .tenureType(TenureType.MONTHS)
                .disbursementDate(Instant.now())
                .dueDate(Instant.now().plus(30, ChronoUnit.DAYS))
                .status(LoanStatus.OPEN)
                .loanType(LoanType.LUMP_SUM)
                .build();
        loan = entityManager.persist(loan);

        // Create loan fees
        loanFee1 = LoanFee.builder()
                .loan(loan)
                .fee(fee1)
                .amount(BigDecimal.valueOf(2500))
                .appliedDate(Instant.now())
                .isPaid(false)
                .applicationTiming(FeeApplicationTiming.ORIGINATION)
                .build();

        loanFee2 = LoanFee.builder()
                .loan(loan)
                .fee(fee2)
                .amount(BigDecimal.valueOf(1000))
                .appliedDate(Instant.now())
                .isPaid(false)
                .applicationTiming(FeeApplicationTiming.OVERDUE)
                .build();

        // Save loan fees
        loanFee1 = loanFeeRepository.save(loanFee1);
        loanFee2 = loanFeeRepository.save(loanFee2);
    }

    @Test
    void shouldSaveAndFindLoanFee() {
        // Given
        LoanFee newLoanFee = LoanFee.builder()
                .loan(loan)
                .fee(fee1)
                .amount(BigDecimal.valueOf(1500))
                .appliedDate(Instant.now())
                .isPaid(false)
                .applicationTiming(FeeApplicationTiming.POST_DISBURSEMENT)
                .build();

        // When
        LoanFee saved = loanFeeRepository.save(newLoanFee);

        // Then
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getCreatedAt()).isNotNull();
        assertThat(saved.getAmount()).isEqualByComparingTo(BigDecimal.valueOf(1500));

        // When
        LoanFee found = loanFeeRepository.findById(saved.getId()).orElse(null);

        // Then
        assertThat(found).isNotNull();
        assertThat(found.getApplicationTiming()).isEqualTo(FeeApplicationTiming.POST_DISBURSEMENT);
    }

    @Test
    void shouldFindLoanFeesByLoanId() {
        // When
        List<LoanFee> loanFees = loanFeeRepository.findByLoanId(loan.getId());

        // Then
        assertThat(loanFees).hasSize(2);
        assertThat(loanFees)
                .extracting(LoanFee::getAmount)
                .containsExactlyInAnyOrder(
                        BigDecimal.valueOf(2500),
                        BigDecimal.valueOf(1000)
                );
    }

    @Test
    void shouldFindUnpaidLoanFees() {
        // When
        List<LoanFee> unpaidFees = loanFeeRepository.findUnpaidLoanFees(loan.getId());

        // Then
        assertThat(unpaidFees).hasSize(2);
        assertThat(unpaidFees).allMatch(fee -> !fee.getIsPaid());
    }

    @Test
    void shouldFindByLoanAndApplicationTiming() {
        // When
        List<LoanFee> originationFees = loanFeeRepository.findByLoanAndApplicationTiming(
                loan.getId(),
                FeeApplicationTiming.ORIGINATION
        );
        List<LoanFee> overdueFees = loanFeeRepository.findByLoanAndApplicationTiming(
                loan.getId(),
                FeeApplicationTiming.OVERDUE
        );

        // Then
        assertThat(originationFees).hasSize(1);
        assertThat(originationFees.get(0).getAmount()).isEqualByComparingTo(BigDecimal.valueOf(2500));
        assertThat(overdueFees).hasSize(1);
        assertThat(overdueFees.get(0).getAmount()).isEqualByComparingTo(BigDecimal.valueOf(1000));
    }

    @Test
    void shouldSumUnpaidFeesForLoan() {
        // When
        BigDecimal totalUnpaid = loanFeeRepository.sumUnpaidFeesForLoan(loan.getId());

        // Then
        assertThat(totalUnpaid).isEqualByComparingTo(
                BigDecimal.valueOf(2500).add(BigDecimal.valueOf(1000))
        );
    }

    @Test
    void shouldFindUnpaidLateFees() {
        // When
        List<LoanFee> unpaidLateFees = loanFeeRepository.findUnpaidLateFees();

        // Then
        assertThat(unpaidLateFees).hasSize(1);
        assertThat(unpaidLateFees.get(0).getAmount()).isEqualByComparingTo(BigDecimal.valueOf(1000));
    }

    @Test
    void shouldFindByLoanAndFee() {
        // When
        List<LoanFee> loanFees = loanFeeRepository.findByLoanAndFee(loan.getId(), fee1.getId());

        // Then
        assertThat(loanFees).hasSize(1);
        assertThat(loanFees.get(0).getAmount()).isEqualByComparingTo(BigDecimal.valueOf(2500));
    }

    @Test
    void shouldMarkFeeAsPaid() {
        // Given
        LoanFee loanFee = loanFeeRepository.findById(loanFee1.getId()).orElseThrow();
        loanFee.setIsPaid(true);
        loanFee.setPaidDate(Instant.now());

        // When
        LoanFee updated = loanFeeRepository.save(loanFee);

        // Then
        assertThat(updated.getIsPaid()).isTrue();
        assertThat(updated.getPaidDate()).isNotNull();
        assertThat(updated.getUpdatedAt()).isAfter(updated.getCreatedAt());
    }

    @Test
    void shouldSumOnlyUnpaidFeesAfterPayment() {
        // Given - Mark one fee as paid
        LoanFee loanFee = loanFeeRepository.findById(loanFee2.getId()).orElseThrow();
        loanFee.setIsPaid(true);
        loanFee.setPaidDate(Instant.now());
        loanFeeRepository.save(loanFee);

        // When
        BigDecimal totalUnpaid = loanFeeRepository.sumUnpaidFeesForLoan(loan.getId());

        // Then
        assertThat(totalUnpaid).isEqualByComparingTo(BigDecimal.valueOf(2500));
    }
}
