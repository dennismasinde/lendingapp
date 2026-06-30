package com.tezzasolutions.lendingapp.customer;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    Optional<Customer> findByEmail(String email);

    Optional<Customer> findByPhoneNumber(String phoneNumber);

    Optional<Customer> findByNationalId(String nationalId);

    @Query("SELECT c FROM Customer c WHERE c.creditScore >= :minScore AND c.isActive = true")
    List<Customer> findCustomersWithMinCreditScore(@Param("minScore") BigDecimal minScore);

    @Query("SELECT c FROM Customer c WHERE c.employmentStatus = :status AND c.isActive = true")
    List<Customer> findByEmploymentStatus(@Param("status") String status);

    @Query("SELECT COUNT(c) > 0 FROM Customer c WHERE c.email = :email OR c.phoneNumber = :phoneNumber")
    boolean existsByEmailOrPhoneNumber(@Param("email") String email, @Param("phoneNumber") String phoneNumber);

    @Query("SELECT c FROM Customer c WHERE c.isActive = true AND c.loanLimit.availableLimit >= :amount")
    List<Customer> findCustomersWithAvailableLimit(@Param("amount") BigDecimal amount);

    List<Customer> findByIsActiveTrue();
}