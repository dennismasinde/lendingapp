package com.tezzasolutions.lendingapp.repository;

import com.tezzasolutions.lendingapp.customer.Customer;
import com.tezzasolutions.lendingapp.loan.Loan;
import com.tezzasolutions.lendingapp.loan.LoanProduct;
import com.tezzasolutions.lendingapp.common.enums.LoanType;
import com.tezzasolutions.lendingapp.repayment.*;
import com.tezzasolutions.lendingapp.common.enums.RepaymentMethod;
import com.tezzasolutions.lendingapp.common.enums.RepaymentStatus;
import com.tezzasolutions.lendingapp.common.enums.TenureType;
import com.tezzasolutions.lendingapp.common.enums.LoanStatus;
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
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class RepaymentRepositoryTest {

    @Autowired
    private RepaymentRepository repaymentRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Customer customer;
    private Loan loan;
    private Repayment repayment1;
    private Repayment repayment2;
    private Repayment repayment3;

    @BeforeEach
    void setUp() {
        // Create customer
        customer = Customer.builder()
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
                .disbursementDate(Instant.now().minus(30, ChronoUnit.DAYS))
                .dueDate(Instant.now().plus(30, ChronoUnit.DAYS))
                .status(LoanStatus.OPEN)
                .loanType(LoanType.LUMP_SUM)
                .build();
        loan = entityManager.persist(loan);

        Instant now = Instant.now();

        // Create test repayments
        repayment1 = Repayment.builder()
                .loan(loan)
                .customer(customer)
                .amount(BigDecimal.valueOf(25000))
                .repaymentDate(now.minus(20, ChronoUnit.DAYS))
                .method(RepaymentMethod.BANK_TRANSFER)
                .transactionReference("TXN001")
                .status(RepaymentStatus.COMPLETED)
                .principalAmount(BigDecimal.valueOf(25000))
                .interestAmount(BigDecimal.ZERO)
                .feeAmount(BigDecimal.ZERO)
                .penaltyAmount(BigDecimal.ZERO)
                .isReversed(false)
                .build();

        repayment2 = Repayment.builder()
                .loan(loan)
                .customer(customer)
                .amount(BigDecimal.valueOf(15000))
                .repaymentDate(now.minus(10, ChronoUnit.DAYS))
                .method(RepaymentMethod.MOBILE_MONEY)
                .transactionReference("TXN002")
                .status(RepaymentStatus.COMPLETED)
                .principalAmount(BigDecimal.valueOf(15000))
                .interestAmount(BigDecimal.ZERO)
                .feeAmount(BigDecimal.ZERO)
                .penaltyAmount(BigDecimal.ZERO)
                .isReversed(false)
                .build();

        repayment3 = Repayment.builder()
                .loan(loan)
                .customer(customer)
                .amount(BigDecimal.valueOf(5000))
                .repaymentDate(now.minus(5, ChronoUnit.DAYS))
                .method(RepaymentMethod.CARD)
                .transactionReference("TXN003")
                .status(RepaymentStatus.PENDING)
                .principalAmount(BigDecimal.valueOf(5000))
                .interestAmount(BigDecimal.ZERO)
                .feeAmount(BigDecimal.ZERO)
                .penaltyAmount(BigDecimal.ZERO)
                .isReversed(false)
                .build();

        // Save repayments
        repayment1 = repaymentRepository.save(repayment1);
        repayment2 = repaymentRepository.save(repayment2);
        repayment3 = repaymentRepository.save(repayment3);
    }

    @Test
    void shouldSaveAndFindRepayment() {
        // Given
        Repayment newRepayment = Repayment.builder()
                .loan(loan)
                .customer(customer)
                .amount(BigDecimal.valueOf(10000))
                .repaymentDate(Instant.now())
                .method(RepaymentMethod.CASH)
                .transactionReference("TXN004")
                .status(RepaymentStatus.PENDING)
                .principalAmount(BigDecimal.valueOf(10000))
                .interestAmount(BigDecimal.ZERO)
                .feeAmount(BigDecimal.ZERO)
                .penaltyAmount(BigDecimal.ZERO)
                .isReversed(false)
                .build();

        // When
        Repayment saved = repaymentRepository.save(newRepayment);

        // Then
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getCreatedAt()).isNotNull();
        assertThat(saved.getTransactionReference()).isEqualTo("TXN004");

        // When
        Repayment found = repaymentRepository.findById(saved.getId()).orElse(null);

        // Then
        assertThat(found).isNotNull();
        assertThat(found.getMethod()).isEqualTo(RepaymentMethod.CASH);
        assertThat(found.getAmount()).isEqualByComparingTo(BigDecimal.valueOf(10000));
    }

    @Test
    void shouldFindRepaymentsByLoanId() {
        // When
        List<Repayment> repayments = repaymentRepository.findByLoanId(loan.getId());

        // Then
        assertThat(repayments).hasSize(3);
        assertThat(repayments)
                .extracting(Repayment::getTransactionReference)
                .containsExactlyInAnyOrder("TXN001", "TXN002", "TXN003");
    }

    @Test
    void shouldFindRepaymentsByCustomerId() {
        // When
        List<Repayment> repayments = repaymentRepository.findByCustomerId(customer.getId());

        // Then
        assertThat(repayments).hasSize(3);
        assertThat(repayments)
                .extracting(Repayment::getAmount)
                .containsExactlyInAnyOrder(
                        BigDecimal.valueOf(25000),
                        BigDecimal.valueOf(15000),
                        BigDecimal.valueOf(5000)
                );
    }

    @Test
    void shouldFindRepaymentsByStatus() {
        // When
        List<Repayment> completedRepayments = repaymentRepository.findByStatus(RepaymentStatus.COMPLETED);
        List<Repayment> pendingRepayments = repaymentRepository.findByStatus(RepaymentStatus.PENDING);

        // Then
        assertThat(completedRepayments).hasSize(2);
        assertThat(completedRepayments)
                .extracting(Repayment::getTransactionReference)
                .containsExactlyInAnyOrder("TXN001", "TXN002");
        assertThat(pendingRepayments).hasSize(1);
        assertThat(pendingRepayments.get(0).getTransactionReference()).isEqualTo("TXN003");
    }

    @Test
    void shouldFindRepaymentsBetweenDates() {
        // When
        Instant startDate = Instant.now().minus(15, ChronoUnit.DAYS);
        Instant endDate = Instant.now().minus(5, ChronoUnit.DAYS);

        List<Repayment> repayments = repaymentRepository.findRepaymentsBetween(startDate, endDate);

        // Then
        assertThat(repayments).hasSize(2);
        assertThat(repayments)
                .extracting(Repayment::getTransactionReference)
                .containsExactlyInAnyOrder("TXN002", "TXN003");
    }

    @Test
    void shouldCalculateTotalRepaidAmountForLoan() {
        // When
        BigDecimal totalRepaid = repaymentRepository.calculateTotalRepaidAmountForLoan(loan.getId());

        // Then
        assertThat(totalRepaid).isEqualByComparingTo(
                BigDecimal.valueOf(25000).add(BigDecimal.valueOf(15000))
        );
    }

    @Test
    void shouldFindLatestRepaymentForLoan() {
        // When
        List<Repayment> latestRepayments = repaymentRepository.findLatestRepaymentForLoan(loan.getId());

        // Then
        assertThat(latestRepayments).hasSize(3);
        assertThat(latestRepayments.get(0).getRepaymentDate())
                .isAfter(latestRepayments.get(1).getRepaymentDate());
        assertThat(latestRepayments.get(1).getRepaymentDate())
                .isAfter(latestRepayments.get(2).getRepaymentDate());
    }

    @Test
    void shouldFindCompletedRepaymentsByCustomer() {
        // When
        List<Repayment> completedRepayments = repaymentRepository.findCompletedRepaymentsByCustomer(
                customer.getId()
        );

        // Then
        assertThat(completedRepayments).hasSize(2);
        assertThat(completedRepayments)
                .extracting(Repayment::getStatus)
                .allMatch(status -> status == RepaymentStatus.COMPLETED);
    }

    @Test
    void shouldFindRepaymentByTransactionReference() {
        // When
        Optional<Repayment> found = repaymentRepository.findByTransactionReference("TXN001");

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getAmount()).isEqualByComparingTo(BigDecimal.valueOf(25000));
        assertThat(found.get().getMethod()).isEqualTo(RepaymentMethod.BANK_TRANSFER);
    }

    @Test
    void shouldUpdateRepaymentStatus() {
        // Given
        Repayment repayment = repaymentRepository.findById(repayment3.getId()).orElseThrow();
        repayment.setStatus(RepaymentStatus.COMPLETED);
        repayment.setRepaymentDate(Instant.now());

        // When
        Repayment updated = repaymentRepository.save(repayment);

        // Then
        assertThat(updated.getStatus()).isEqualTo(RepaymentStatus.COMPLETED);
        assertThat(updated.getUpdatedAt()).isAfter(updated.getCreatedAt());
    }

    @Test
    void shouldReverseRepayment() {
        // Given
        Repayment repayment = repaymentRepository.findById(repayment2.getId()).orElseThrow();
        repayment.setIsReversed(true);
        repayment.setStatus(RepaymentStatus.REVERSED);

        // When
        Repayment updated = repaymentRepository.save(repayment);

        // Then
        assertThat(updated.getIsReversed()).isTrue();
        assertThat(updated.getStatus()).isEqualTo(RepaymentStatus.REVERSED);
    }
}