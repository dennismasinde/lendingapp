package com.tezzasolutions.lendingapp.repository;

import com.tezzasolutions.lendingapp.customer.*;
import com.tezzasolutions.lendingapp.customer.CustomerLoanLimit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class CustomerRepositoryTest {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Customer customer1;
    private Customer customer2;
    private Customer customer3;

    @BeforeEach
    void setUp() {
        // Create test customers
        customer1 = Customer.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .phoneNumber("+254700000001")
                .dateOfBirth(LocalDate.of(1990, 1, 15))
                .nationalId("12345678")
                .address("123 Main St, Nairobi")
                .city("Nairobi")
                .country("Kenya")
                .postalCode("00100")
                .employmentStatus("EMPLOYED")
                .employerName("Tech Corp")
                .monthlyIncome(BigDecimal.valueOf(75000))
                .creditScore(BigDecimal.valueOf(85.5))
                .isActive(true)
                .build();

        customer2 = Customer.builder()
                .firstName("Jane")
                .lastName("Smith")
                .email("jane.smith@example.com")
                .phoneNumber("+254700000002")
                .dateOfBirth(LocalDate.of(1985, 5, 20))
                .nationalId("87654321")
                .address("456 Park Ave, Mombasa")
                .city("Mombasa")
                .country("Kenya")
                .postalCode("80100")
                .employmentStatus("SELF_EMPLOYED")
                .employerName("Jane's Consulting")
                .monthlyIncome(BigDecimal.valueOf(120000))
                .creditScore(BigDecimal.valueOf(92.0))
                .isActive(true)
                .build();

        customer3 = Customer.builder()
                .firstName("Bob")
                .lastName("Wilson")
                .email("bob.wilson@example.com")
                .phoneNumber("+254700000003")
                .dateOfBirth(LocalDate.of(1978, 11, 8))
                .nationalId("98765432")
                .address("789 Oak Rd, Kisumu")
                .city("Kisumu")
                .country("Kenya")
                .postalCode("40100")
                .employmentStatus("UNEMPLOYED")
                .monthlyIncome(BigDecimal.valueOf(0))
                .creditScore(BigDecimal.valueOf(45.0))
                .isActive(false)
                .build();

        // Save customers
        customer1 = customerRepository.save(customer1);
        customer2 = customerRepository.save(customer2);
        customer3 = customerRepository.save(customer3);
    }

    @Test
    void shouldSaveAndFindCustomer() {
        // Given
        Customer newCustomer = Customer.builder()
                .firstName("Alice")
                .lastName("Johnson")
                .email("alice.johnson@example.com")
                .phoneNumber("+254700000004")
                .employmentStatus("EMPLOYED")
                .monthlyIncome(BigDecimal.valueOf(65000))
                .creditScore(BigDecimal.valueOf(78.0))
                .isActive(true)
                .build();

        // When
        Customer saved = customerRepository.save(newCustomer);

        // Then
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getCreatedAt()).isNotNull();
        assertThat(saved.getUpdatedAt()).isNotNull();
        assertThat(saved.getVersion()).isEqualTo(0L);

        // When
        Optional<Customer> found = customerRepository.findById(saved.getId());

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getEmail()).isEqualTo("alice.johnson@example.com");
        assertThat(found.get().getFullName()).isEqualTo("Alice Johnson");
    }

    @Test
    void shouldFindCustomerByEmail() {
        // When
        Optional<Customer> found = customerRepository.findByEmail("john.doe@example.com");

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getFirstName()).isEqualTo("John");
        assertThat(found.get().getLastName()).isEqualTo("Doe");
    }

    @Test
    void shouldFindCustomerByPhoneNumber() {
        // When
        Optional<Customer> found = customerRepository.findByPhoneNumber("+254700000002");

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getEmail()).isEqualTo("jane.smith@example.com");
    }

    @Test
    void shouldFindCustomerByNationalId() {
        // When
        Optional<Customer> found = customerRepository.findByNationalId("12345678");

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getFirstName()).isEqualTo("John");
    }

    @Test
    void shouldFindCustomersWithMinCreditScore() {
        // When
        List<Customer> customers = customerRepository.findCustomersWithMinCreditScore(BigDecimal.valueOf(80.0));

        // Then
        assertThat(customers).hasSize(1);
        assertThat(customers.get(0).getEmail()).isEqualTo("jane.smith@example.com");
    }

    @Test
    void shouldFindCustomersByEmploymentStatus() {
        // When
        List<Customer> employed = customerRepository.findByEmploymentStatus("EMPLOYED");
        List<Customer> unemployed = customerRepository.findByEmploymentStatus("UNEMPLOYED");

        // Then
        assertThat(employed).hasSize(1);
        assertThat(employed.get(0).getEmail()).isEqualTo("john.doe@example.com");
        assertThat(unemployed).hasSize(1);
        assertThat(unemployed.get(0).getEmail()).isEqualTo("bob.wilson@example.com");
    }

    @Test
    void shouldCheckIfCustomerExistsByEmailOrPhone() {
        // When
        boolean exists1 = customerRepository.existsByEmailOrPhoneNumber(
                "john.doe@example.com", "+254700000009"
        );
        boolean exists2 = customerRepository.existsByEmailOrPhoneNumber(
                "nonexistent@example.com", "+254700000002"
        );
        boolean exists3 = customerRepository.existsByEmailOrPhoneNumber(
                "nonexistent@example.com", "+254700000009"
        );

        // Then
        assertThat(exists1).isTrue();
        assertThat(exists2).isTrue();
        assertThat(exists3).isFalse();
    }

    @Test
    void shouldFindCustomersWithAvailableLimit() {
        // Given - Create loan limits for customers
        CustomerLoanLimit limit1 = CustomerLoanLimit.builder()
                .customer(customer1)
                .maxLoanAmount(BigDecimal.valueOf(500000))
                .totalOutstandingLimit(BigDecimal.valueOf(200000))
                .availableLimit(BigDecimal.valueOf(300000))
                .maxNumberOfLoans(3)
                .isActive(true)
                .lastReviewDate(Instant.now())
                .riskLevel("LOW")
                .build();

        CustomerLoanLimit limit2 = CustomerLoanLimit.builder()
                .customer(customer2)
                .maxLoanAmount(BigDecimal.valueOf(1000000))
                .totalOutstandingLimit(BigDecimal.valueOf(800000))
                .availableLimit(BigDecimal.valueOf(200000))
                .maxNumberOfLoans(5)
                .isActive(true)
                .lastReviewDate(Instant.now())
                .riskLevel("LOW")
                .build();

        entityManager.persist(limit1);
        entityManager.persist(limit2);
        entityManager.flush();

        // When
        List<Customer> customers = customerRepository.findCustomersWithAvailableLimit(BigDecimal.valueOf(250000));

        // Then
        assertThat(customers).hasSize(1);
        assertThat(customers.get(0).getEmail()).isEqualTo("john.doe@example.com");
    }

    @Test
    void shouldFindAllActiveCustomers() {
        // When
        List<Customer> activeCustomers = customerRepository.findByIsActiveTrue();

        // Then
        assertThat(activeCustomers).hasSize(2);
        assertThat(activeCustomers)
                .extracting(Customer::getEmail)
                .containsExactlyInAnyOrder("john.doe@example.com", "jane.smith@example.com");
    }

    @Test
    void shouldUpdateCustomer() {
        // Given
        Customer customer = customerRepository.findById(customer1.getId()).orElseThrow();
        customer.setFirstName("Jonathan");
        customer.setMonthlyIncome(BigDecimal.valueOf(85000));

        // When
        Customer updated = customerRepository.save(customer);

        // Then
        assertThat(updated.getFirstName()).isEqualTo("Jonathan");
        assertThat(updated.getMonthlyIncome()).isEqualTo(BigDecimal.valueOf(85000));
        assertThat(updated.getUpdatedAt()).isAfter(customer.getCreatedAt());
        assertThat(updated.getVersion()).isEqualTo(1L);
    }

    @Test
    void shouldDeleteCustomer() {
        // Given
        Customer customer = customerRepository.save(Customer.builder()
                .firstName("Temp")
                .lastName("User")
                .email("temp@example.com")
                .phoneNumber("+254700000005")
                .employmentStatus("EMPLOYED")
                .isActive(true)
                .build());

        Long customerId = customer.getId();

        // When
        customerRepository.deleteById(customerId);

        // Then
        Optional<Customer> found = customerRepository.findById(customerId);
        assertThat(found).isEmpty();
    }
}
