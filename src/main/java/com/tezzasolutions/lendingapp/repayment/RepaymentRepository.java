package com.tezzasolutions.lendingapp.repayment;

import com.tezzasolutions.lendingapp.common.enums.RepaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface RepaymentRepository extends JpaRepository<Repayment, Long> {

    List<Repayment> findByLoanId(Long loanId);

    List<Repayment> findByCustomerId(Long customerId);

    List<Repayment> findByStatus(RepaymentStatus status);

    @Query("SELECT r FROM Repayment r WHERE r.repaymentDate BETWEEN :startDate AND :endDate")
    List<Repayment> findRepaymentsBetween(@Param("startDate") Instant startDate, @Param("endDate") Instant endDate);

    @Query("SELECT SUM(r.amount) FROM Repayment r WHERE r.loan.id = :loanId AND r.status = 'COMPLETED' AND r.isReversed = false")
    BigDecimal calculateTotalRepaidAmountForLoan(@Param("loanId") Long loanId);

    @Query("SELECT r FROM Repayment r WHERE r.loan.id = :loanId ORDER BY r.repaymentDate DESC")
    List<Repayment> findLatestRepaymentForLoan(@Param("loanId") Long loanId);

    @Query("SELECT r FROM Repayment r WHERE r.customer.id = :customerId AND r.status = 'COMPLETED' ORDER BY r.repaymentDate DESC")
    List<Repayment> findCompletedRepaymentsByCustomer(@Param("customerId") Long customerId);

    @Query("SELECT r FROM Repayment r WHERE r.transactionReference = :reference")
    Optional<Repayment> findByTransactionReference(@Param("reference") String reference);
}
