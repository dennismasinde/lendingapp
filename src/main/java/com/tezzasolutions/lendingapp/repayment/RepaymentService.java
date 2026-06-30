package com.tezzasolutions.lendingapp.repayment;

import com.tezzasolutions.lendingapp.common.enums.InstallmentStatus;
import com.tezzasolutions.lendingapp.common.enums.LoanStatus;
import com.tezzasolutions.lendingapp.common.enums.LoanType;
import com.tezzasolutions.lendingapp.common.enums.RepaymentStatus;
import com.tezzasolutions.lendingapp.common.exceptions.LendingAppException;
import com.tezzasolutions.lendingapp.common.exceptions.ResourceNotFoundException;
import com.tezzasolutions.lendingapp.customer.Customer;
import com.tezzasolutions.lendingapp.customer.CustomerService;
import com.tezzasolutions.lendingapp.installment.Installment;
import com.tezzasolutions.lendingapp.installment.InstallmentRepository;
import com.tezzasolutions.lendingapp.loan.Loan;
import com.tezzasolutions.lendingapp.loan.LoanFee;
import com.tezzasolutions.lendingapp.loan.LoanRepository;
import com.tezzasolutions.lendingapp.loan.LoanService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class RepaymentService {

    private final RepaymentRepository repaymentRepository;
    private final LoanRepository loanRepository;
    private final InstallmentRepository installmentRepository;
    private final CustomerService customerService;
    private final LoanService loanService;

    @Transactional
    public Repayment processRepayment(RepaymentRequest request) {
        log.info("Processing repayment for loan: {}", request.getLoanId());

        // Validate loan
        Loan loan = loanService.getLoanById(request.getLoanId());

        if (loan.getStatus() == LoanStatus.CLOSED || loan.getStatus() == LoanStatus.CANCELLED) {
            throw new LendingAppException("Cannot make payment on closed or cancelled loan");
        }

        if (loan.getOutstandingBalance().compareTo(BigDecimal.ZERO) <= 0) {
            throw new LendingAppException("Loan has no outstanding balance");
        }

        // Validate amount
        if (request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new LendingAppException("Payment amount must be greater than zero");
        }

        if (request.getAmount().compareTo(loan.getOutstandingBalance()) > 0) {
            throw new LendingAppException(
                    String.format("Payment amount %s exceeds outstanding balance %s",
                            request.getAmount(), loan.getOutstandingBalance())
            );
        }

        // Process payment
        Repayment repayment = createRepayment(request, loan);

        // Update loan balance
        loan.setOutstandingBalance(loan.getOutstandingBalance().subtract(request.getAmount()));
        loanRepository.save(loan);

        // Update installments if installment loan
        if (loan.getLoanType() == LoanType.INSTALLMENT) {
            updateInstallments(loan, request.getAmount());
        }

        // Check if loan should be closed
        if (loan.getOutstandingBalance().compareTo(BigDecimal.ZERO) == 0) {
            loanService.closeLoan(loan.getId());
        }

        log.info("Repayment processed successfully for loan: {}", loan.getId());
        return repayment;
    }

    @Transactional
    public Repayment reverseRepayment(Long repaymentId) {
        Repayment repayment = repaymentRepository.findById(repaymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Repayment", "id", repaymentId));

        if (repayment.getIsReversed()) {
            throw new LendingAppException("Repayment already reversed");
        }

        Loan loan = repayment.getLoan();

        // Reverse the payment
        loan.setOutstandingBalance(loan.getOutstandingBalance().add(repayment.getAmount()));
        loanRepository.save(loan);

        repayment.setIsReversed(true);
        repayment.setStatus(RepaymentStatus.REVERSED);

        // Update loan status if it was closed
        if (loan.getStatus() == LoanStatus.CLOSED) {
            loan.setStatus(LoanStatus.OPEN);
            loan.setClosedAt(null);
            loanRepository.save(loan);
        }

        log.info("Repayment {} reversed", repaymentId);
        return repaymentRepository.save(repayment);
    }

    @Transactional(readOnly = true)
    public List<Repayment> getLoanRepayments(Long loanId) {
        return repaymentRepository.findByLoanId(loanId);
    }

    @Transactional(readOnly = true)
    public Repayment getRepaymentById(Long repaymentId) {
        return repaymentRepository.findById(repaymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Repayment", "id", repaymentId));
    }

    @Transactional(readOnly = true)
    public BigDecimal getTotalRepaidForLoan(Long loanId) {
        BigDecimal total = repaymentRepository.calculateTotalRepaidAmountForLoan(loanId);
        return total != null ? total : BigDecimal.ZERO;
    }

    private Repayment createRepayment(RepaymentRequest request, Loan loan) {
        Customer customer = customerService.getCustomerById(loan.getCustomer().getId());

        // Calculate how much goes to principal vs interest/fees
        BigDecimal[] allocation = allocatePayment(request.getAmount(), loan);

        Repayment repayment = Repayment.builder()
                .loan(loan)
                .customer(customer)
                .amount(request.getAmount())
                .repaymentDate(Instant.now())
                .method(request.getMethod())
                .transactionReference(generateTransactionReference())
                .status(RepaymentStatus.COMPLETED)
                .principalAmount(allocation[0])
                .interestAmount(allocation[1])
                .feeAmount(allocation[2])
                .penaltyAmount(allocation[3])
                .isReversed(false)
                .build();

        return repaymentRepository.save(repayment);
    }

    private BigDecimal[] allocatePayment(BigDecimal amount, Loan loan) {
        BigDecimal[] allocation = new BigDecimal[4];

        // First, pay off fees and penalties
        BigDecimal totalFees = loan.getLoanFees().stream()
                .filter(fee -> !fee.getIsPaid())
                .map(LoanFee::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Simplified allocation - real systems would have more complex logic
        BigDecimal remaining = amount;

        // Pay fees first
        if (remaining.compareTo(totalFees) > 0) {
            allocation[2] = totalFees;
            remaining = remaining.subtract(totalFees);
        } else {
            allocation[2] = remaining;
            allocation[0] = BigDecimal.ZERO;
            allocation[1] = BigDecimal.ZERO;
            allocation[3] = BigDecimal.ZERO;
            return allocation;
        }

        // Then pay principal
        if (remaining.compareTo(loan.getPrincipalAmount()) > 0) {
            allocation[0] = loan.getPrincipalAmount();
            remaining = remaining.subtract(loan.getPrincipalAmount());
        } else {
            allocation[0] = remaining;
            allocation[1] = BigDecimal.ZERO;
            allocation[3] = BigDecimal.ZERO;
            return allocation;
        }

        // Rest goes to interest
        allocation[1] = remaining;
        allocation[3] = BigDecimal.ZERO;

        return allocation;
    }

    private void updateInstallments(Loan loan, BigDecimal amount) {
        // Find the first unpaid installment and apply payment
        List<Installment> installments = installmentRepository.findLoanInstallmentsByStatus(
                loan.getId(),
                InstallmentStatus.PENDING
        );

        BigDecimal remainingAmount = amount;
        for (Installment installment : installments) {
            if (remainingAmount.compareTo(BigDecimal.ZERO) <= 0) {
                break;
            }

            BigDecimal installmentBalance = installment.getOutstandingBalance();
            if (remainingAmount.compareTo(installmentBalance) >= 0) {
                installment.setOutstandingBalance(BigDecimal.ZERO);
                installment.setStatus(InstallmentStatus.PAID);
                installment.setPaidDate(Instant.now());
                remainingAmount = remainingAmount.subtract(installmentBalance);
            } else {
                installment.setOutstandingBalance(installmentBalance.subtract(remainingAmount));
                installment.setStatus(InstallmentStatus.PARTIALLY_PAID);
                remainingAmount = BigDecimal.ZERO;
            }
            installmentRepository.save(installment);
        }
    }

    private String generateTransactionReference() {
        return "TXN" + System.currentTimeMillis() + Instant.now().getNano();
    }
}