package com.tezzasolutions.lendingapp.loan;

import com.tezzasolutions.lendingapp.common.enums.LoanStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Repository
public interface LoanRepository extends JpaRepository<Loan, Long> {

    List<Loan> findByCustomerId(Long customerId);

    List<Loan> findByStatus(LoanStatus status);

    @Query("SELECT l FROM Loan l WHERE l.status IN :statuses AND l.dueDate < :date")
    List<Loan> findLoansDueForSweep(@Param("statuses") List<LoanStatus> statuses, @Param("date") Instant date);

    @Query("SELECT l FROM Loan l WHERE l.customer.id = :customerId AND l.status = :status")
    List<Loan> findCustomerLoansByStatus(@Param("customerId") Long customerId, @Param("status") LoanStatus status);

    @Query("SELECT l FROM Loan l WHERE l.dueDate BETWEEN :startDate AND :endDate")
    List<Loan> findLoansDueBetween(@Param("startDate") Instant startDate, @Param("endDate") Instant endDate);

    @Query("SELECT l FROM Loan l WHERE l.isConsolidated = true AND l.consolidatedGroupId = :groupId")
    List<Loan> findConsolidatedLoansByGroupId(@Param("groupId") String groupId);

    @Query("SELECT COUNT(l) FROM Loan l WHERE l.customer.id = :customerId AND l.status IN :activeStatuses")
    Long countActiveLoansForCustomer(@Param("customerId") Long customerId, @Param("activeStatuses") List<LoanStatus> activeStatuses);

    @Query("SELECT SUM(l.outstandingBalance) FROM Loan l WHERE l.customer.id = :customerId AND l.status IN :activeStatuses")
    BigDecimal sumOutstandingBalanceForCustomer(@Param("customerId") Long customerId, @Param("activeStatuses") List<LoanStatus> activeStatuses);

    @Query("SELECT l FROM Loan l WHERE l.status = :status AND l.disbursementDate <= :date")
    List<Loan> findLoansByStatusAndDisbursementDateBefore(@Param("status") LoanStatus status, @Param("date") Instant date);
}
