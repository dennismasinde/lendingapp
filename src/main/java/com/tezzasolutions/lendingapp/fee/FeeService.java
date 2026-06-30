package com.tezzasolutions.lendingapp.fee;

import com.tezzasolutions.lendingapp.common.enums.FeeApplicationTiming;
import com.tezzasolutions.lendingapp.common.enums.FeeType;
import com.tezzasolutions.lendingapp.common.exceptions.LendingAppException;
import com.tezzasolutions.lendingapp.common.exceptions.ResourceNotFoundException;
import com.tezzasolutions.lendingapp.loan.Loan;
import com.tezzasolutions.lendingapp.loan.LoanFee;
import com.tezzasolutions.lendingapp.loan.LoanFeeRepository;
import com.tezzasolutions.lendingapp.loan.LoanRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class FeeService {

    private final FeeRepository feeRepository;
    private final LoanFeeRepository loanFeeRepository;
    private final LoanRepository loanRepository;

    @Transactional
    public Fee createFee(Fee fee) {
        log.info("Creating fee: {}", fee.getName());

        // Validate fee
        if (fee.getAmount() == null || fee.getAmount().compareTo(BigDecimal.ZERO) < 0) {
            throw new LendingAppException("Fee amount cannot be negative");
        }

        if (fee.getPercentage() != null && fee.getPercentage().compareTo(BigDecimal.ZERO) < 0) {
            throw new LendingAppException("Fee percentage cannot be negative");
        }

        return feeRepository.save(fee);
    }

    @Transactional
    public void applyFeesToLoan(Loan loan) {
        List<Fee> fees = feeRepository.findActiveFeesByProduct(loan.getLoanProduct().getId());

        for (Fee fee : fees) {
            if (fee.getApplicationTiming() == FeeApplicationTiming.ORIGINATION) {
                applyFee(loan, fee);
            }
        }
    }

    @Transactional
    public void applyLateFees(Loan loan) {
        List<Fee> lateFees = feeRepository.findLateFeeConfigurations(loan.getLoanProduct().getId());

        for (Fee fee : lateFees) {
            if (fee.getFeeType() == FeeType.LATE && fee.getIsActive()) {
                applyFee(loan, fee);
            }
        }
    }

    @Transactional
    public void applyDailyFees(Loan loan) {
        List<Fee> dailyFees = feeRepository.findFeesByProductAndType(
                loan.getLoanProduct().getId(),
                FeeType.DAILY
        );

        for (Fee fee : dailyFees) {
            if (fee.getIsActive()) {
                applyFee(loan, fee);
            }
        }
    }

    @Transactional
    public void applyFeeToLoan(Long loanId, Long feeId) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new ResourceNotFoundException("Loan", "id", loanId));

        Fee fee = feeRepository.findById(feeId)
                .orElseThrow(() -> new ResourceNotFoundException("Fee", "id", feeId));

        applyFee(loan, fee);
    }

    private void applyFee(Loan loan, Fee fee) {
        BigDecimal feeAmount = calculateFeeAmount(loan, fee);

        LoanFee loanFee = LoanFee.builder()
                .loan(loan)
                .fee(fee)
                .amount(feeAmount)
                .appliedDate(Instant.now())
                .isPaid(false)
                .applicationTiming(fee.getApplicationTiming())
                .build();

        loanFeeRepository.save(loanFee);

        // Update loan outstanding balance
        loan.setOutstandingBalance(loan.getOutstandingBalance().add(feeAmount));
        loanRepository.save(loan);

        log.info("Fee {} applied to loan {}: {}", fee.getName(), loan.getId(), feeAmount);
    }

    private BigDecimal calculateFeeAmount(Loan loan, Fee fee) {
        return switch (fee.getCalculationType()) {
            case FIXED -> fee.getAmount();
            case PERCENTAGE -> loan.getPrincipalAmount()
                    .multiply(fee.getPercentage())
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        };
    }

    @Transactional
    public void markFeeAsPaid(Long loanFeeId) {
        LoanFee loanFee = loanFeeRepository.findById(loanFeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Loan fee", "id", loanFeeId));

        if (loanFee.getIsPaid()) {
            throw new LendingAppException("Fee already paid");
        }

        loanFee.setIsPaid(true);
        loanFee.setPaidDate(Instant.now());
        loanFeeRepository.save(loanFee);

        log.info("Fee {} marked as paid", loanFeeId);
    }

    @Transactional(readOnly = true)
    public List<Fee> getFeesByProduct(Long productId) {
        return feeRepository.findByLoanProductId(productId);
    }

    @Transactional(readOnly = true)
    public List<Fee> getActiveFeesByProduct(Long productId) {
        return feeRepository.findActiveFeesByProduct(productId);
    }

    @Transactional(readOnly = true)
    public List<Fee> getFeesByType(FeeType feeType) {
        return feeRepository.findByFeeType(feeType);
    }

    @Transactional
    public Fee updateFee(Long feeId, Fee feeDetails) {
        Fee fee = feeRepository.findById(feeId)
                .orElseThrow(() -> new ResourceNotFoundException("Fee", "id", feeId));

        fee.setName(feeDetails.getName());
        fee.setFeeType(feeDetails.getFeeType());
        fee.setCalculationType(feeDetails.getCalculationType());
        fee.setAmount(feeDetails.getAmount());
        fee.setPercentage(feeDetails.getPercentage());
        fee.setDaysAfterDue(feeDetails.getDaysAfterDue());
        fee.setApplicationTiming(feeDetails.getApplicationTiming());
        fee.setIsActive(feeDetails.getIsActive());

        return feeRepository.save(fee);
    }

    @Transactional
    public void deactivateFee(Long feeId) {
        Fee fee = feeRepository.findById(feeId)
                .orElseThrow(() -> new ResourceNotFoundException("Fee", "id", feeId));

        fee.setIsActive(false);
        feeRepository.save(fee);
        log.info("Fee deactivated: {}", fee.getName());
    }

    @Transactional(readOnly = true)
    public BigDecimal calculateTotalUnpaidFees(Long loanId) {
        return loanFeeRepository.sumUnpaidFeesForLoan(loanId);
    }

    @Transactional(readOnly = true)
    public List<LoanFee> getUnpaidFeesForLoan(Long loanId) {
        return loanFeeRepository.findUnpaidLoanFees(loanId);
    }
}