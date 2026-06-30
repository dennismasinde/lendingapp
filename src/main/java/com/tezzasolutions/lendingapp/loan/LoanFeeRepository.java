package com.tezzasolutions.lendingapp.loan;

import com.tezzasolutions.lendingapp.common.enums.FeeApplicationTiming;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface LoanFeeRepository extends JpaRepository<LoanFee, Long> {

    List<LoanFee> findByLoanId(Long loanId);

    @Query("SELECT lf FROM LoanFee lf WHERE lf.loan.id = :loanId AND lf.isPaid = false")
    List<LoanFee> findUnpaidLoanFees(@Param("loanId") Long loanId);

    @Query("SELECT lf FROM LoanFee lf WHERE lf.loan.id = :loanId AND lf.applicationTiming = :timing")
    List<LoanFee> findByLoanAndApplicationTiming(@Param("loanId") Long loanId,
                                                 @Param("timing") FeeApplicationTiming timing);

    @Query("SELECT SUM(lf.amount) FROM LoanFee lf WHERE lf.loan.id = :loanId AND lf.isPaid = false")
    BigDecimal sumUnpaidFeesForLoan(@Param("loanId") Long loanId);

    @Query("SELECT lf FROM LoanFee lf WHERE lf.fee.feeType = 'LATE' AND lf.isPaid = false")
    List<LoanFee> findUnpaidLateFees();

    @Query("SELECT lf FROM LoanFee lf WHERE lf.loan.id = :loanId AND lf.fee.id = :feeId")
    List<LoanFee> findByLoanAndFee(@Param("loanId") Long loanId, @Param("feeId") Long feeId);
}
