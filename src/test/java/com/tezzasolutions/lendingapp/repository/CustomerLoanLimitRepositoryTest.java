package com.tezzasolutions.lendingapp.repository;

import com.tezzasolutions.lendingapp.customer.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class CustomerLoanLimitRepositoryTest {

    @Autowired
    private CustomerLoanLimitRepository customerLoanLimitRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Customer customer1;
    private Customer customer2;
    private CustomerLoanLimit limit1;
    private CustomerLoanLimit limit2;

    @BeforeEach
    void setUp() {
        // Create customers
        customer1 = Customer.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .phoneNumber("+254700000001")
                .employmentStatus("EMPLOYED")
                .creditScore(BigDecimal.valueOf(85.5))
                .isActive(true)
                .build();
        customer1 = entityManager.persist(customer1);

        customer2 = Customer.builder()
                .firstName("Jane")
                .lastName("Smith")
                .email("jane.smith@example.com")
                .phoneNumber("+254700000002")
                .employmentStatus("SELF_EMPLOYED")
                .creditScore(BigDecimal.valueOf(92.0))
                .isActive(true)
                .build();
        customer2 = entityManager.persist(customer2);

        // Create loan limits
        limit1 = CustomerLoanLimit.builder()
                .customer(customer1)
                .maxLoanAmount(BigDecimal.valueOf(500000))
                .totalOutstandingLimit(BigDecimal.valueOf(200000))
                .availableLimit(BigDecimal.valueOf(300000))
                .maxNumberOfLoans(3)
                .isActive(true)
                .lastReviewDate(Instant.now())
                .riskLevel("LOW")
                .build();

        limit2 = CustomerLoanLimit.builder()
                .customer(customer2)
                .maxLoanAmount(BigDecimal.valueOf(1000000))
                .totalOutstandingLimit(BigDecimal.valueOf(500000))
                .availableLimit(BigDecimal.valueOf(500000))
                .maxNumberOfLoans(5)
                .isActive(true)
                .lastReviewDate(Instant.now())
                .riskLevel("MEDIUM")
                .build();

        // Save limits
        limit1 = customerLoanLimitRepository.save(limit1);
        limit2 = customerLoanLimitRepository.save(limit2);
    }

    @Test
    void shouldSaveAndFindCustomerLoanLimit() {
        // Given
        Customer customer3 = Customer.builder()
                .firstName("Bob")
                .lastName("Wilson")
                .email("bob.wilson@example.com")
                .phoneNumber("+254700000003")
                .employmentStatus("EMPLOYED")
                .build();
        customer3 = entityManager.persist(customer3);

        CustomerLoanLimit newLimit = CustomerLoanLimit.builder()
                .customer(customer3)
                .maxLoanAmount(BigDecimal.valueOf(250000))
                .totalOutstandingLimit(BigDecimal.valueOf(100000))
                .availableLimit(BigDecimal.valueOf(150000))
                .maxNumberOfLoans(2)
                .isActive(true)
                .lastReviewDate(Instant.now())
                .riskLevel("HIGH")
                .build();

        // When
        CustomerLoanLimit saved = customerLoanLimitRepository.save(newLimit);

        // Then
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getCreatedAt()).isNotNull();
        assertThat(saved.getAvailableLimit()).isEqualByComparingTo(BigDecimal.valueOf(150000));

        // When
        CustomerLoanLimit found = customerLoanLimitRepository.findById(saved.getId()).orElse(null);

        // Then
        assertThat(found).isNotNull();
        assertThat(found.getRiskLevel()).isEqualTo("HIGH");
    }

    @Test
    void shouldFindByCustomerId() {
        // When
        Optional<CustomerLoanLimit> found = customerLoanLimitRepository.findByCustomerId(customer1.getId());

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getMaxLoanAmount()).isEqualByComparingTo(BigDecimal.valueOf(500000));
        assertThat(found.get().getAvailableLimit()).isEqualByComparingTo(BigDecimal.valueOf(300000));
    }

    @Test
    void shouldFindCustomersWithAvailableLimitGreaterThan() {
        // When
        List<CustomerLoanLimit> limits = customerLoanLimitRepository.findCustomersWithAvailableLimitGreaterThan(
                BigDecimal.valueOf(350000)
        );

        // Then
        assertThat(limits).hasSize(1);
        assertThat(limits.get(0).getCustomer().getId()).isEqualTo(customer2.getId());
        assertThat(limits.get(0).getAvailableLimit()).isEqualByComparingTo(BigDecimal.valueOf(500000));
    }

    @Test
    void shouldFindCustomersWithMaxLimitLessThan() {
        // When
        List<CustomerLoanLimit> limits = customerLoanLimitRepository.findCustomersWithMaxLimitLessThan(
                BigDecimal.valueOf(750000)
        );

        // Then
        assertThat(limits).hasSize(1);
        assertThat(limits.get(0).getCustomer().getId()).isEqualTo(customer1.getId());
        assertThat(limits.get(0).getMaxLoanAmount()).isEqualByComparingTo(BigDecimal.valueOf(500000));
    }

    @Test
    void shouldUpdateAvailableLimit() {
        // When
        int updatedCount = customerLoanLimitRepository.updateAvailableLimit(
                customer1.getId(),
                BigDecimal.valueOf(400000)
        );

        // Then
        assertThat(updatedCount).isEqualTo(1);

        // Verify
        Optional<CustomerLoanLimit> updated = customerLoanLimitRepository.findByCustomerId(customer1.getId());
        assertThat(updated).isPresent();
        assertThat(updated.get().getAvailableLimit()).isEqualByComparingTo(BigDecimal.valueOf(400000));
        assertThat(updated.get().getUpdatedAt()).isAfter(updated.get().getCreatedAt());
    }

    @Test
    void shouldFindByRiskLevel() {
        // When
        List<CustomerLoanLimit> lowRisk = customerLoanLimitRepository.findByRiskLevel("LOW");
        List<CustomerLoanLimit> mediumRisk = customerLoanLimitRepository.findByRiskLevel("MEDIUM");
        List<CustomerLoanLimit> highRisk = customerLoanLimitRepository.findByRiskLevel("HIGH");

        // Then
        assertThat(lowRisk).hasSize(1);
        assertThat(lowRisk.get(0).getCustomer().getId()).isEqualTo(customer1.getId());
        assertThat(mediumRisk).hasSize(1);
        assertThat(mediumRisk.get(0).getCustomer().getId()).isEqualTo(customer2.getId());
        assertThat(highRisk).isEmpty();
    }

    @Test
    void shouldCheckIfCustomerHasLimit() {
        // When
        boolean hasLimit1 = customerLoanLimitRepository.existsByCustomerId(customer1.getId());
        boolean hasLimit2 = customerLoanLimitRepository.existsByCustomerId(999L);

        // Then
        assertThat(hasLimit1).isTrue();
        assertThat(hasLimit2).isFalse();
    }

    @Test
    void shouldUpdateLimitDetails() {
        // Given
        CustomerLoanLimit limit = customerLoanLimitRepository.findByCustomerId(customer1.getId()).orElseThrow();
        limit.setMaxLoanAmount(BigDecimal.valueOf(750000));
        limit.setRiskLevel("MEDIUM");
        limit.setMaxNumberOfLoans(4);

        // When
        CustomerLoanLimit updated = customerLoanLimitRepository.save(limit);

        // Then
        assertThat(updated.getMaxLoanAmount()).isEqualByComparingTo(BigDecimal.valueOf(750000));
        assertThat(updated.getRiskLevel()).isEqualTo("MEDIUM");
        assertThat(updated.getMaxNumberOfLoans()).isEqualTo(4);
        assertThat(updated.getVersion()).isEqualTo(1L);
    }

    @Test
    void shouldDeactivateLimit() {
        // Given
        CustomerLoanLimit limit = customerLoanLimitRepository.findByCustomerId(customer2.getId()).orElseThrow();
        limit.setIsActive(false);

        // When
        CustomerLoanLimit updated = customerLoanLimitRepository.save(limit);

        // Then
        assertThat(updated.getIsActive()).isFalse();

        // When checking available limits
        List<CustomerLoanLimit> availableLimits = customerLoanLimitRepository
                .findCustomersWithAvailableLimitGreaterThan(BigDecimal.valueOf(100000));
        assertThat(availableLimits).hasSize(1);
        assertThat(availableLimits.get(0).getCustomer().getId()).isEqualTo(customer1.getId());
    }
}