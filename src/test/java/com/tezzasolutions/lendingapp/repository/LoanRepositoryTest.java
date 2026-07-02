package com.tezzasolutions.lendingapp.repository;

import com.tezzasolutions.lendingapp.common.enums.BillingCycleType;
import com.tezzasolutions.lendingapp.common.enums.LoanStatus;
import com.tezzasolutions.lendingapp.common.enums.LoanType;
import com.tezzasolutions.lendingapp.common.enums.TenureType;
import com.tezzasolutions.lendingapp.customer.Customer;
import com.tezzasolutions.lendingapp.loan.Loan;
import com.tezzasolutions.lendingapp.loan.LoanProduct;
import com.tezzasolutions.lendingapp.loan.LoanRepository;
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
class LoanRepositoryTest {

    @Autowired
    private LoanRepository loanRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Customer customer;
    private LoanProduct product;
    private Loan loan1;
    private Loan loan2;
    private Loan loan3;

    @BeforeEach
    void setUp() {
        // Create customer
        customer = Customer.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .phoneNumber("+254700000001")
                .employmentStatus("EMPLOYED")
                .monthlyIncome(BigDecimal.valueOf(75000))
                .creditScore(BigDecimal.valueOf(85.5))
                .isActive(true)
                .build();
        customer = entityManager.persist(customer);

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

        Instant now = Instant.now();

        // Create test loans
        loan1 = Loan.builder()
                .customer(customer)
                .loanProduct(product)
                .principalAmount(BigDecimal.valueOf(100000))
                .totalAmount(BigDecimal.valueOf(112500))
                .outstandingBalance(BigDecimal.valueOf(100000))
                .interestRate(BigDecimal.valueOf(12.5))
                .tenure(12)
                .tenureType(TenureType.MONTHS)
                .disbursementDate(now)
                .dueDate(now.plus(30, ChronoUnit.DAYS))
                .status(LoanStatus.OPEN)
                .loanType(LoanType.LUMP_SUM)
                .billingCycleType(BillingCycleType.INDIVIDUAL)
                .isConsolidated(false)
                .build();

        loan2 = Loan.builder()
                .customer(customer)
                .loanProduct(product)
                .principalAmount(BigDecimal.valueOf(200000))
                .totalAmount(BigDecimal.valueOf(225000))
                .outstandingBalance(BigDecimal.valueOf(225000))
                .interestRate(BigDecimal.valueOf(12.5))
                .tenure(6)
                .tenureType(TenureType.MONTHS)
                .disbursementDate(now.minus(60, ChronoUnit.DAYS))
                .dueDate(now.minus(1, ChronoUnit.DAYS))
                .status(LoanStatus.OVERDUE)
                .loanType(LoanType.INSTALLMENT)
                .billingCycleType(BillingCycleType.INDIVIDUAL)
                .isConsolidated(false)
                .build();

        loan3 = Loan.builder()
                .customer(customer)
                .loanProduct(product)
                .principalAmount(BigDecimal.valueOf(50000))
                .totalAmount(BigDecimal.valueOf(56250))
                .outstandingBalance(BigDecimal.valueOf(56250))
                .interestRate(BigDecimal.valueOf(12.5))
                .tenure(3)
                .tenureType(TenureType.MONTHS)
                .disbursementDate(now.minus(30, ChronoUnit.DAYS))
                .dueDate(now.plus(60, ChronoUnit.DAYS))
                .status(LoanStatus.OPEN)
                .loanType(LoanType.LUMP_SUM)
                .billingCycleType(BillingCycleType.CONSOLIDATED)
                .isConsolidated(true)
                .consolidatedGroupId("GROUP001")
                .build();

        // Save loans
        loan1 = loanRepository.save(loan1);
        loan2 = loanRepository.save(loan2);
        loan3 = loanRepository.save(loan3);
    }

    @Test
    void shouldSaveAndFindLoan() {
        // Given
        Loan newLoan = Loan.builder()
                .customer(customer)
                .loanProduct(product)
                .principalAmount(BigDecimal.valueOf(150000))
                .totalAmount(BigDecimal.valueOf(168750))
                .outstandingBalance(BigDecimal.valueOf(168750))
                .interestRate(BigDecimal.valueOf(12.5))
                .tenure(9)
                .tenureType(TenureType.MONTHS)
                .disbursementDate(Instant.now())
                .dueDate(Instant.now().plus(270, ChronoUnit.DAYS))
                .status(LoanStatus.OPEN)
                .loanType(LoanType.INSTALLMENT)
                .build();

        // When
        Loan saved = loanRepository.save(newLoan);

        // Then
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getCreatedAt()).isNotNull();
        assertThat(saved.getTotalAmount()).isEqualByComparingTo(BigDecimal.valueOf(168750));

        // When
        Loan found = loanRepository.findById(saved.getId()).orElse(null);

        // Then
        assertThat(found).isNotNull();
        assertThat(found.getLoanType()).isEqualTo(LoanType.INSTALLMENT);
    }

    @Test
    void shouldFindLoansByCustomerId() {
        // When
        List<Loan> customerLoans = loanRepository.findByCustomerId(customer.getId());

        // Then
        assertThat(customerLoans).hasSize(3);
        assertThat(customerLoans)
                .extracting(Loan::getPrincipalAmount)
                .containsExactlyInAnyOrder(
                        BigDecimal.valueOf(100000),
                        BigDecimal.valueOf(200000),
                        BigDecimal.valueOf(50000)
                );
    }

    @Test
    void shouldFindLoansByStatus() {
        // When
        List<Loan> openLoans = loanRepository.findByStatus(LoanStatus.OPEN);
        List<Loan> overdueLoans = loanRepository.findByStatus(LoanStatus.OVERDUE);

        // Then
        assertThat(openLoans).hasSize(2);
        assertThat(overdueLoans).hasSize(1);
        assertThat(overdueLoans.get(0).getPrincipalAmount())
                .isEqualByComparingTo(BigDecimal.valueOf(200000));
    }

    @Test
    void shouldFindLoansDueForSweep() {
        // When
        List<LoanStatus> statuses = List.of(LoanStatus.OPEN, LoanStatus.OVERDUE);
        List<Loan> overdueLoans = loanRepository.findLoansDueForSweep(
                statuses,
                Instant.now()
        );

        // Then
        assertThat(overdueLoans).hasSize(1);
        assertThat(overdueLoans.get(0).getStatus()).isEqualTo(LoanStatus.OVERDUE);
    }

    @Test
    void shouldFindCustomerLoansByStatus() {
        // When
        List<Loan> customerOpenLoans = loanRepository.findCustomerLoansByStatus(
                customer.getId(),
                LoanStatus.OPEN
        );

        // Then
        assertThat(customerOpenLoans).hasSize(2);
        assertThat(customerOpenLoans)
                .extracting(Loan::getPrincipalAmount)
                .containsExactlyInAnyOrder(
                        BigDecimal.valueOf(100000),
                        BigDecimal.valueOf(50000)
                );
    }

    @Test
    void shouldFindLoansDueBetween() {
        // When
        Instant startDate = Instant.now().minus(10, ChronoUnit.DAYS);
        Instant endDate = Instant.now().plus(10, ChronoUnit.DAYS);
        List<Loan> loansDueBetween = loanRepository.findLoansDueBetween(startDate, endDate);

        // Then
        assertThat(loansDueBetween).hasSize(1);
        assertThat(loansDueBetween.get(0).getStatus()).isEqualTo(LoanStatus.OVERDUE);
    }

    @Test
    void shouldFindConsolidatedLoansByGroupId() {
        // When
        List<Loan> consolidatedLoans = loanRepository.findConsolidatedLoansByGroupId("GROUP001");

        // Then
        assertThat(consolidatedLoans).hasSize(1);
        assertThat(consolidatedLoans.get(0).getIsConsolidated()).isTrue();
        assertThat(consolidatedLoans.get(0).getPrincipalAmount())
                .isEqualByComparingTo(BigDecimal.valueOf(50000));
    }

    @Test
    void shouldCountActiveLoansForCustomer() {
        // When
        List<LoanStatus> activeStatuses = List.of(LoanStatus.OPEN, LoanStatus.OVERDUE);
        Long activeCount = loanRepository.countActiveLoansForCustomer(
                customer.getId(),
                activeStatuses
        );

        // Then
        assertThat(activeCount).isEqualTo(3);
    }

    @Test
    void shouldSumOutstandingBalanceForCustomer() {
        // When
        List<LoanStatus> activeStatuses = List.of(LoanStatus.OPEN, LoanStatus.OVERDUE);
        BigDecimal totalOutstanding = loanRepository.sumOutstandingBalanceForCustomer(
                customer.getId(),
                activeStatuses
        );

        // Then
        assertThat(totalOutstanding).isEqualByComparingTo(
                BigDecimal.valueOf(100000)
                        .add(BigDecimal.valueOf(225000))
                        .add(BigDecimal.valueOf(56250))
        );
    }

    @Test
    void shouldFindLoansByStatusAndDisbursementDate() {
        // When
        Instant cutoffDate = Instant.now().minus(30, ChronoUnit.DAYS);
        List<Loan> loans = loanRepository.findLoansByStatusAndDisbursementDateBefore(
                LoanStatus.OPEN,
                cutoffDate
        );

        // Then
        assertThat(loans).hasSize(1);
        assertThat(loans.get(0).getPrincipalAmount())
                .isEqualByComparingTo(BigDecimal.valueOf(50000));
    }

    @Test
    void shouldUpdateLoanStatusToClosed() {
        // Given
        Loan loan = loanRepository.findById(loan1.getId()).orElseThrow();
        loan.setStatus(LoanStatus.CLOSED);
        loan.setClosedAt(Instant.now());

        // When
        Loan updated = loanRepository.save(loan);

        // Then
        assertThat(updated.getStatus()).isEqualTo(LoanStatus.CLOSED);
        assertThat(updated.getClosedAt()).isNotNull();
        assertThat(updated.getUpdatedAt()).isAfter(updated.getCreatedAt());
    }
}