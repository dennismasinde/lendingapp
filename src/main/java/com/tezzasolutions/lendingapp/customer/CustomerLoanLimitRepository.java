package com.tezzasolutions.lendingapp.customer;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerLoanLimitRepository extends JpaRepository<CustomerLoanLimit, Long> {

    Optional<CustomerLoanLimit> findByCustomerId(Long customerId);

    @Query("SELECT cll FROM CustomerLoanLimit cll WHERE cll.availableLimit >= :amount AND cll.isActive = true")
    List<CustomerLoanLimit> findCustomersWithAvailableLimitGreaterThan(@Param("amount") BigDecimal amount);

    @Query("SELECT cll FROM CustomerLoanLimit cll WHERE cll.maxLoanAmount < :amount AND cll.isActive = true")
    List<CustomerLoanLimit> findCustomersWithMaxLimitLessThan(@Param("amount") BigDecimal amount);

    @Modifying
    @Transactional
    @Query("UPDATE CustomerLoanLimit cll SET cll.availableLimit = :newLimit WHERE cll.customer.id = :customerId")
    int updateAvailableLimit(@Param("customerId") Long customerId, @Param("newLimit") BigDecimal newLimit);

    @Query("SELECT cll FROM CustomerLoanLimit cll WHERE cll.isActive = true AND cll.riskLevel = :riskLevel")
    List<CustomerLoanLimit> findByRiskLevel(@Param("riskLevel") String riskLevel);

    boolean existsByCustomerId(Long customerId);
}
