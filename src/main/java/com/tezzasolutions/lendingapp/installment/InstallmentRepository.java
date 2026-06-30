package com.tezzasolutions.lendingapp.installment;

import com.tezzasolutions.lendingapp.common.enums.InstallmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Repository
public interface InstallmentRepository extends JpaRepository<Installment, Long> {

    List<Installment> findByLoanId(Long loanId);

    List<Installment> findByStatus(InstallmentStatus status);

    @Query("SELECT i FROM Installment i WHERE i.loan.id = :loanId AND i.status = :status")
    List<Installment> findLoanInstallmentsByStatus(@Param("loanId") Long loanId, @Param("status") InstallmentStatus status);

    @Query("SELECT i FROM Installment i WHERE i.dueDate < :date AND i.status IN :pendingStatuses")
    List<Installment> findOverdueInstallments(@Param("date") Instant date, @Param("pendingStatuses") List<InstallmentStatus> pendingStatuses);

    @Query("SELECT SUM(i.outstandingBalance) FROM Installment i WHERE i.loan.id = :loanId AND i.status != 'PAID'")
    BigDecimal calculateTotalOutstandingInstallmentBalance(@Param("loanId") Long loanId);

    @Query("SELECT i FROM Installment i WHERE i.loan.id = :loanId ORDER BY i.installmentNumber ASC")
    List<Installment> findLoanInstallmentsOrdered(@Param("loanId") Long loanId);

    @Query("SELECT i FROM Installment i WHERE i.dueDate BETWEEN :startDate AND :endDate AND i.status = :status")
    List<Installment> findInstallmentsDueBetweenAndStatus(@Param("startDate") Instant startDate,
                                                          @Param("endDate") Instant endDate,
                                                          @Param("status") InstallmentStatus status);
}
