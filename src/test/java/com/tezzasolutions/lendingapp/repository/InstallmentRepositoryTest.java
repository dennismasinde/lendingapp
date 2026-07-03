package com.tezzasolutions.lendingapp.repository;

import com.tezzasolutions.lendingapp.customer.Customer;
import com.tezzasolutions.lendingapp.installment.*;
import com.tezzasolutions.lendingapp.common.enums.InstallmentStatus;
import com.tezzasolutions.lendingapp.common.enums.LoanStatus;
import com.tezzasolutions.lendingapp.common.enums.LoanType;
import com.tezzasolutions.lendingapp.common.enums.TenureType;
import com.tezzasolutions.lendingapp.loan.Loan;
import com.tezzasolutions.lendingapp.loan.LoanProduct;
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
class InstallmentRepositoryTest {

    @Autowired
    private InstallmentRepository installmentRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Loan loan;
    private Installment installment1;
    private Installment installment2;
    private Installment installment3;

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

        // Create loan
        loan = Loan.builder()
                .customer(customer)
                .loanProduct(product)
                .principalAmount(BigDecimal.valueOf(300000))
                .totalAmount(BigDecimal.valueOf(337500))
                .outstandingBalance(BigDecimal.valueOf(337500))
                .interestRate(BigDecimal.valueOf(12.5))
                .tenure(12)
                .tenureType(TenureType.MONTHS)
                .disbursementDate(Instant.now())
                .dueDate(Instant.now().plus(30, ChronoUnit.DAYS))
                .status(LoanStatus.OPEN)
                .loanType(LoanType.INSTALLMENT)
                .build();
        loan = entityManager.persist(loan);

        Instant now = Instant.now();

        // Create test installments
        installment1 = Installment.builder()
                .loan(loan)
                .installmentNumber(1)
                .amount(BigDecimal.valueOf(28125))
                .principalAmount(BigDecimal.valueOf(25000))
                .interestAmount(BigDecimal.valueOf(3125))
                .dueDate(now.plus(30, ChronoUnit.DAYS))
                .outstandingBalance(BigDecimal.valueOf(28125))
                .status(InstallmentStatus.PENDING)
                .build();

        installment2 = Installment.builder()
                .loan(loan)
                .installmentNumber(2)
                .amount(BigDecimal.valueOf(28125))
                .principalAmount(BigDecimal.valueOf(25000))
                .interestAmount(BigDecimal.valueOf(3125))
                .dueDate(now.plus(60, ChronoUnit.DAYS))
                .outstandingBalance(BigDecimal.valueOf(28125))
                .status(InstallmentStatus.PENDING)
                .build();

        installment3 = Installment.builder()
                .loan(loan)
                .installmentNumber(3)
                .amount(BigDecimal.valueOf(28125))
                .principalAmount(BigDecimal.valueOf(25000))
                .interestAmount(BigDecimal.valueOf(3125))
                .dueDate(now.minus(10, ChronoUnit.DAYS))
                .outstandingBalance(BigDecimal.valueOf(28125))
                .status(InstallmentStatus.OVERDUE)
                .build();

        // Save installments
        installment1 = installmentRepository.save(installment1);
        installment2 = installmentRepository.save(installment2);
        installment3 = installmentRepository.save(installment3);
    }

    @Test
    void shouldSaveAndFindInstallment() {
        // Given
        Installment newInstallment = Installment.builder()
                .loan(loan)
                .installmentNumber(4)
                .amount(BigDecimal.valueOf(28125))
                .principalAmount(BigDecimal.valueOf(25000))
                .interestAmount(BigDecimal.valueOf(3125))
                .dueDate(Instant.now().plus(90, ChronoUnit.DAYS))
                .outstandingBalance(BigDecimal.valueOf(28125))
                .status(InstallmentStatus.PENDING)
                .build();

        // When
        Installment saved = installmentRepository.save(newInstallment);

        // Then
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getCreatedAt()).isNotNull();
        assertThat(saved.getInstallmentNumber()).isEqualTo(4);

        // When
        Installment found = installmentRepository.findById(saved.getId()).orElse(null);

        // Then
        assertThat(found).isNotNull();
        assertThat(found.getAmount()).isEqualByComparingTo(BigDecimal.valueOf(28125));
    }

    @Test
    void shouldFindInstallmentsByLoanId() {
        // When
        List<Installment> installments = installmentRepository.findByLoanId(loan.getId());

        // Then
        assertThat(installments).hasSize(3);
        assertThat(installments)
                .extracting(Installment::getInstallmentNumber)
                .containsExactlyInAnyOrder(1, 2, 3);
    }

    @Test
    void shouldFindInstallmentsByStatus() {
        // When
        List<Installment> pendingInstallments = installmentRepository.findByStatus(InstallmentStatus.PENDING);
        List<Installment> overdueInstallments = installmentRepository.findByStatus(InstallmentStatus.OVERDUE);

        // Then
        assertThat(pendingInstallments).hasSize(2);
        assertThat(overdueInstallments).hasSize(1);
        assertThat(overdueInstallments.get(0).getInstallmentNumber()).isEqualTo(3);
    }

    @Test
    void shouldFindLoanInstallmentsByStatus() {
        // When
        List<Installment> pendingLoanInstallments = installmentRepository.findLoanInstallmentsByStatus(
                loan.getId(),
                InstallmentStatus.PENDING
        );

        // Then
        assertThat(pendingLoanInstallments).hasSize(2);
        assertThat(pendingLoanInstallments)
                .extracting(Installment::getInstallmentNumber)
                .containsExactlyInAnyOrder(1, 2);
    }

    @Test
    void shouldFindOverdueInstallments() {
        // When
        List<InstallmentStatus> pendingStatuses = List.of(
                InstallmentStatus.PENDING,
                InstallmentStatus.OVERDUE
        );
        List<Installment> overdue = installmentRepository.findOverdueInstallments(
                Instant.now(),
                pendingStatuses
        );

        // Then
        assertThat(overdue).hasSize(1);
        assertThat(overdue.get(0).getInstallmentNumber()).isEqualTo(3);
    }

    @Test
    void shouldCalculateTotalOutstandingInstallmentBalance() {
        // When
        BigDecimal totalOutstanding = installmentRepository.calculateTotalOutstandingInstallmentBalance(
                loan.getId()
        );

        // Then
        assertThat(totalOutstanding).isEqualByComparingTo(
                BigDecimal.valueOf(28125)
                        .add(BigDecimal.valueOf(28125))
                        .add(BigDecimal.valueOf(28125))
        );
    }

    @Test
    void shouldFindLoanInstallmentsOrdered() {
        // When
        List<Installment> orderedInstallments = installmentRepository.findLoanInstallmentsOrdered(
                loan.getId()
        );

        // Then
        assertThat(orderedInstallments).hasSize(3);
        assertThat(orderedInstallments.get(0).getInstallmentNumber()).isEqualTo(1);
        assertThat(orderedInstallments.get(1).getInstallmentNumber()).isEqualTo(2);
        assertThat(orderedInstallments.get(2).getInstallmentNumber()).isEqualTo(3);
    }

    @Test
    void shouldFindInstallmentsDueBetweenAndStatus() {
        // When
        Instant startDate = Instant.now().minus(20, ChronoUnit.DAYS);
        Instant endDate = Instant.now().plus(40, ChronoUnit.DAYS);

        List<Installment> installments = installmentRepository.findInstallmentsDueBetweenAndStatus(
                startDate,
                endDate,
                InstallmentStatus.PENDING
        );

        // Then
        assertThat(installments).hasSize(1);
        assertThat(installments.get(0).getInstallmentNumber()).isEqualTo(1);
    }

    @Test
    void shouldUpdateInstallmentStatus() {
        // Given
        Installment installment = installmentRepository.findById(installment1.getId()).orElseThrow();
        installment.setStatus(InstallmentStatus.PAID);
        installment.setPaidDate(Instant.now());
        installment.setOutstandingBalance(BigDecimal.ZERO);

        // When
        Installment updated = installmentRepository.save(installment);

        // Then
        assertThat(updated.getStatus()).isEqualTo(InstallmentStatus.PAID);
        assertThat(updated.getPaidDate()).isNotNull();
        assertThat(updated.getOutstandingBalance()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(updated.getUpdatedAt()).isAfter(updated.getCreatedAt());
    }
}