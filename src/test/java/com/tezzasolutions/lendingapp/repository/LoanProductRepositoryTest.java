package com.tezzasolutions.lendingapp.repository;


import com.tezzasolutions.lendingapp.loan.*;
import com.tezzasolutions.lendingapp.common.enums.TenureType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class LoanProductRepositoryTest {

    @Autowired
    private LoanProductRepository loanProductRepository;

    private LoanProduct product1;
    private LoanProduct product2;
    private LoanProduct product3;

    @BeforeEach
    void setUp() {
        // Create test loan products
        product1 = LoanProduct.builder()
                .name("Personal Loan")
                .description("Short-term personal loan")
                .minAmount(BigDecimal.valueOf(10000))
                .maxAmount(BigDecimal.valueOf(500000))
                .interestRate(BigDecimal.valueOf(12.5))
                .minTenure(1)
                .maxTenure(12)
                .tenureType(TenureType.MONTHS)
                .isActive(true)
                .build();

        product2 = LoanProduct.builder()
                .name("Business Loan")
                .description("Long-term business financing")
                .minAmount(BigDecimal.valueOf(500000))
                .maxAmount(BigDecimal.valueOf(5000000))
                .interestRate(BigDecimal.valueOf(10.0))
                .minTenure(6)
                .maxTenure(60)
                .tenureType(TenureType.MONTHS)
                .isActive(true)
                .build();

        product3 = LoanProduct.builder()
                .name("Emergency Loan")
                .description("Quick emergency loans")
                .minAmount(BigDecimal.valueOf(1000))
                .maxAmount(BigDecimal.valueOf(50000))
                .interestRate(BigDecimal.valueOf(18.0))
                .minTenure(7)
                .maxTenure(30)
                .tenureType(TenureType.DAYS)
                .isActive(false)
                .build();

        // Save products
        product1 = loanProductRepository.save(product1);
        product2 = loanProductRepository.save(product2);
        product3 = loanProductRepository.save(product3);
    }

    @Test
    void shouldSaveAndFindLoanProduct() {
        // Given
        LoanProduct newProduct = LoanProduct.builder()
                .name("Student Loan")
                .description("Education financing")
                .minAmount(BigDecimal.valueOf(5000))
                .maxAmount(BigDecimal.valueOf(100000))
                .interestRate(BigDecimal.valueOf(8.5))
                .minTenure(3)
                .maxTenure(24)
                .tenureType(TenureType.MONTHS)
                .isActive(true)
                .build();

        // When
        LoanProduct saved = loanProductRepository.save(newProduct);

        // Then
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getCreatedAt()).isNotNull();
        assertThat(saved.getName()).isEqualTo("Student Loan");

        // When
        Optional<LoanProduct> found = loanProductRepository.findById(saved.getId());

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getDescription()).isEqualTo("Education financing");
        assertThat(found.get().getTenureType()).isEqualTo(TenureType.MONTHS);
    }

    @Test
    void shouldFindProductByName() {
        // When
        Optional<LoanProduct> found = loanProductRepository.findByName("Personal Loan");

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getInterestRate()).isEqualByComparingTo(BigDecimal.valueOf(12.5));
        assertThat(found.get().getMinAmount()).isEqualByComparingTo(BigDecimal.valueOf(10000));
    }

    @Test
    void shouldFindActiveProducts() {
        // When
        List<LoanProduct> activeProducts = loanProductRepository.findByIsActiveTrue();

        // Then
        assertThat(activeProducts).hasSize(2);
        assertThat(activeProducts)
                .extracting(LoanProduct::getName)
                .containsExactlyInAnyOrder("Personal Loan", "Business Loan");
    }

    @Test
    void shouldFindEligibleProductsByAmount() {
        // When
        List<LoanProduct> eligible1 = loanProductRepository.findEligibleProducts(BigDecimal.valueOf(25000));
        List<LoanProduct> eligible2 = loanProductRepository.findEligibleProducts(BigDecimal.valueOf(1000000));
        List<LoanProduct> eligible3 = loanProductRepository.findEligibleProducts(BigDecimal.valueOf(100));

        // Then
        assertThat(eligible1).hasSize(1);
        assertThat(eligible1.get(0).getName()).isEqualTo("Personal Loan");
        assertThat(eligible2).hasSize(1);
        assertThat(eligible2.get(0).getName()).isEqualTo("Business Loan");
        assertThat(eligible3).isEmpty();
    }

    @Test
    void shouldFindProductsByTenureType() {
        // When
        List<LoanProduct> monthlyProducts = loanProductRepository.findByTenureType(TenureType.MONTHS);
        List<LoanProduct> dailyProducts = loanProductRepository.findByTenureType(TenureType.DAYS);

        // Then
        assertThat(monthlyProducts).hasSize(2);
        assertThat(monthlyProducts)
                .extracting(LoanProduct::getName)
                .containsExactlyInAnyOrder("Personal Loan", "Business Loan");
        assertThat(dailyProducts).hasSize(1);
        assertThat(dailyProducts.get(0).getName()).isEqualTo("Emergency Loan");
    }

    @Test
    void shouldFindProductsByMaxInterestRate() {
        // When
        List<LoanProduct> products = loanProductRepository.findProductsWithMaxInterestRate(BigDecimal.valueOf(12.0));

        // Then
        assertThat(products).hasSize(1);
        assertThat(products.get(0).getName()).isEqualTo("Business Loan");
    }

    @Test
    void shouldFindProductsByTenureRange() {
        // When
        List<LoanProduct> products = loanProductRepository.findProductsByTenureRange(24);

        // Then
        assertThat(products).hasSize(2);
        assertThat(products)
                .extracting(LoanProduct::getName)
                .containsExactlyInAnyOrder("Personal Loan", "Business Loan");
    }

    @Test
    void shouldCheckIfProductNameExists() {
        // When
        boolean exists1 = loanProductRepository.existsByNameIgnoreCase("Personal Loan");
        boolean exists2 = loanProductRepository.existsByNameIgnoreCase("personal loan");
        boolean exists3 = loanProductRepository.existsByNameIgnoreCase("NonExistent Product");

        // Then
        assertThat(exists1).isTrue();
        assertThat(exists2).isTrue();
        assertThat(exists3).isFalse();
    }

    @Test
    void shouldUpdateLoanProduct() {
        // Given
        LoanProduct product = loanProductRepository.findById(product1.getId()).orElseThrow();
        product.setInterestRate(BigDecimal.valueOf(15.0));
        product.setMaxAmount(BigDecimal.valueOf(750000));

        // When
        LoanProduct updated = loanProductRepository.save(product);

        // Then
        assertThat(updated.getInterestRate()).isEqualByComparingTo(BigDecimal.valueOf(15.0));
        assertThat(updated.getMaxAmount()).isEqualByComparingTo(BigDecimal.valueOf(750000));
        assertThat(updated.getUpdatedAt()).isAfter(product.getCreatedAt());
    }
}