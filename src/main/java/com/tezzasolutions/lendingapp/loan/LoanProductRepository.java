package com.tezzasolutions.lendingapp.loan;

import com.tezzasolutions.lendingapp.common.enums.TenureType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface LoanProductRepository extends JpaRepository<LoanProduct, Long> {

    Optional<LoanProduct> findByName(String name);

    List<LoanProduct> findByIsActiveTrue();

    @Query("SELECT lp FROM LoanProduct lp WHERE lp.minAmount <= :amount AND lp.maxAmount >= :amount AND lp.isActive = true")
    List<LoanProduct> findEligibleProducts(@Param("amount") BigDecimal amount);

    @Query("SELECT lp FROM LoanProduct lp WHERE lp.tenureType = :tenureType AND lp.isActive = true")
    List<LoanProduct> findByTenureType(@Param("tenureType") TenureType tenureType);

    @Query("SELECT lp FROM LoanProduct lp WHERE lp.interestRate <= :maxRate AND lp.isActive = true")
    List<LoanProduct> findProductsWithMaxInterestRate(@Param("maxRate") BigDecimal maxRate);

    @Query("SELECT lp FROM LoanProduct lp WHERE lp.minTenure <= :tenure AND lp.maxTenure >= :tenure AND lp.isActive = true")
    List<LoanProduct> findProductsByTenureRange(@Param("tenure") Integer tenure);

    boolean existsByNameIgnoreCase(String name);
}
