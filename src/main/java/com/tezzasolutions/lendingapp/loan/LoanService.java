package com.tezzasolutions.lendingapp.loan;

import com.tezzasolutions.lendingapp.common.enums.BillingCycleType;
import com.tezzasolutions.lendingapp.common.enums.InstallmentStatus;
import com.tezzasolutions.lendingapp.common.enums.LoanStatus;
import com.tezzasolutions.lendingapp.common.enums.LoanType;
import com.tezzasolutions.lendingapp.common.enums.TenureType;
import com.tezzasolutions.lendingapp.common.exceptions.InsufficientLimitException;
import com.tezzasolutions.lendingapp.common.exceptions.InvalidLoanStateException;
import com.tezzasolutions.lendingapp.common.exceptions.LendingAppException;
import com.tezzasolutions.lendingapp.common.exceptions.ResourceNotFoundException;
import com.tezzasolutions.lendingapp.customer.Customer;
import com.tezzasolutions.lendingapp.customer.CustomerLoanLimit;
import com.tezzasolutions.lendingapp.customer.CustomerService;
import com.tezzasolutions.lendingapp.fee.FeeService;
import com.tezzasolutions.lendingapp.installment.Installment;
import com.tezzasolutions.lendingapp.installment.InstallmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class LoanService {

    private final LoanRepository loanRepository;
    private final InstallmentRepository installmentRepository;
    private final CustomerService customerService;
    private final LoanProductService loanProductService;
    private final FeeService feeService;

    @Transactional
    public Loan createLoan(LoanRequest request) {
        log.info("Creating loan for customer: {}", request.getCustomerId());

        // Validate customer
        Customer customer = customerService.getCustomerById(request.getCustomerId());

        // Validate product
        LoanProduct product = loanProductService.getProductById(request.getProductId());

        if (!product.getIsActive()) {
            throw new LendingAppException("Product is not active");
        }

        // Validate amount
        if (request.getAmount().compareTo(product.getMinAmount()) < 0 ||
                request.getAmount().compareTo(product.getMaxAmount()) > 0) {
            throw new LendingAppException(
                    String.format("Amount must be between %s and %s",
                            product.getMinAmount(), product.getMaxAmount())
            );
        }

        // Validate customer limit
        customerService.validateCustomerLimit(customer.getId(), request.getAmount());

        // Calculate total amount with interest
        BigDecimal totalAmount = calculateTotalAmount(
                request.getAmount(),
                product.getInterestRate(),
                request.getTenure()
        );

        // Create loan
        Loan loan = Loan.builder()
                .customer(customer)
                .loanProduct(product)
                .principalAmount(request.getAmount())
                .totalAmount(totalAmount)
                .outstandingBalance(totalAmount)
                .interestRate(product.getInterestRate())
                .tenure(request.getTenure())
                .tenureType(product.getTenureType())
                .disbursementDate(Instant.now())
                .dueDate(calculateDueDate(request.getTenure(), product.getTenureType()))
                .status(LoanStatus.OPEN)
                .loanType(request.getLoanType())
                .billingCycleType(request.getBillingCycleType())
                .isConsolidated(request.getIsConsolidated() != null && request.getIsConsolidated())
                .build();

        Loan savedLoan = loanRepository.save(loan);
        log.info("Loan created with ID: {}", savedLoan.getId());

        // Create installments if installment loan
        if (request.getLoanType() == LoanType.INSTALLMENT) {
            createInstallments(savedLoan);
        }

        // Apply fees
        feeService.applyFeesToLoan(savedLoan);

        // Update customer limit
        updateCustomerLimit(customer.getId(), request.getAmount());

        return savedLoan;
    }

    @Transactional
    public Loan approveLoan(Long loanId) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new ResourceNotFoundException("Loan", "id", loanId));

        if (loan.getStatus() != LoanStatus.PENDING) {
            throw new InvalidLoanStateException("Loan can only be approved from PENDING state");
        }

        loan.setStatus(LoanStatus.APPROVED);
        return loanRepository.save(loan);
    }

    @Transactional
    public Loan disburseLoan(Long loanId) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new ResourceNotFoundException("Loan", "id", loanId));

        if (loan.getStatus() != LoanStatus.APPROVED) {
            throw new InvalidLoanStateException("Loan can only be disbursed from APPROVED state");
        }

        loan.setStatus(LoanStatus.OPEN);
        loan.setDisbursementDate(Instant.now());
        loan.setDueDate(calculateDueDate(loan.getTenure(), loan.getTenureType()));

        return loanRepository.save(loan);
    }

    @Transactional
    public Loan closeLoan(Long loanId) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new ResourceNotFoundException("Loan", "id", loanId));

        if (loan.getStatus() != LoanStatus.OPEN && loan.getStatus() != LoanStatus.OVERDUE) {
            throw new InvalidLoanStateException("Loan can only be closed from OPEN or OVERDUE state");
        }

        if (loan.getOutstandingBalance().compareTo(BigDecimal.ZERO) > 0) {
            throw new LendingAppException("Cannot close loan with outstanding balance");
        }

        loan.setStatus(LoanStatus.CLOSED);
        loan.setClosedAt(Instant.now());

        // Release customer limit
        releaseCustomerLimit(loan.getCustomer().getId(), loan.getPrincipalAmount());

        return loanRepository.save(loan);
    }

    @Transactional
    public Loan cancelLoan(Long loanId) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new ResourceNotFoundException("Loan", "id", loanId));

        if (loan.getStatus() != LoanStatus.PENDING && loan.getStatus() != LoanStatus.APPROVED) {
            throw new InvalidLoanStateException("Loan can only be cancelled from PENDING or APPROVED state");
        }

        loan.setStatus(LoanStatus.CANCELLED);
        loan.setCancelledAt(Instant.now());

        return loanRepository.save(loan);
    }

    @Transactional
    public Loan writeOffLoan(Long loanId) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new ResourceNotFoundException("Loan", "id", loanId));

        if (loan.getStatus() != LoanStatus.OVERDUE) {
            throw new InvalidLoanStateException("Loan can only be written off from OVERDUE state");
        }

        loan.setStatus(LoanStatus.WRITTEN_OFF);
        loan.setWrittenOffAt(Instant.now());

        return loanRepository.save(loan);
    }

    @Transactional(readOnly = true)
    public Loan getLoanById(Long loanId) {
        return loanRepository.findById(loanId)
                .orElseThrow(() -> new ResourceNotFoundException("Loan", "id", loanId));
    }

    @Transactional(readOnly = true)
    public List<Loan> getCustomerLoans(Long customerId) {
        return loanRepository.findByCustomerId(customerId);
    }

    @Transactional(readOnly = true)
    public List<Loan> getCustomerLoansByStatus(Long customerId, LoanStatus status) {
        return loanRepository.findCustomerLoansByStatus(customerId, status);
    }

    @Transactional(readOnly = true)
    public List<Loan> getOverdueLoans() {
        return loanRepository.findByStatus(LoanStatus.OVERDUE);
    }

    @Transactional(readOnly = true)
    public List<Loan> getLoansDueForSweep() {
        return loanRepository.findLoansDueForSweep(
                List.of(LoanStatus.OPEN),
                Instant.now()
        );
    }

    @Transactional(readOnly = true)
    public Long countActiveLoansForCustomer(Long customerId) {
        return loanRepository.countActiveLoansForCustomer(
                customerId,
                List.of(LoanStatus.OPEN, LoanStatus.OVERDUE)
        );
    }

    @Transactional(readOnly = true)
    public BigDecimal sumOutstandingBalanceForCustomer(Long customerId) {
        return loanRepository.sumOutstandingBalanceForCustomer(
                customerId,
                List.of(LoanStatus.OPEN, LoanStatus.OVERDUE)
        );
    }

    @Transactional
    public void processOverdueLoans() {
        List<Loan> overdueLoans = loanRepository.findLoansDueForSweep(
                List.of(LoanStatus.OPEN),
                Instant.now()
        );

        for (Loan loan : overdueLoans) {
            loan.setStatus(LoanStatus.OVERDUE);
            loanRepository.save(loan);

            // Apply late fees
            feeService.applyLateFees(loan);

            log.info("Loan {} marked as overdue", loan.getId());
        }
    }

    @Transactional
    public void updateLoanBalance(Long loanId, BigDecimal amount) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new ResourceNotFoundException("Loan", "id", loanId));

        BigDecimal newBalance = loan.getOutstandingBalance().subtract(amount);
        if (newBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new LendingAppException("Payment amount exceeds outstanding balance");
        }

        loan.setOutstandingBalance(newBalance);
        loanRepository.save(loan);
    }

    @Transactional(readOnly = true)
    public List<Loan> getLoansByProduct(Long productId) {
        return loanRepository.findByLoanProductId(productId);
    }

    @Transactional(readOnly = true)
    public List<Loan> getConsolidatedLoans(String groupId) {
        return loanRepository.findConsolidatedLoansByGroupId(groupId);
    }

    private BigDecimal calculateTotalAmount(BigDecimal principal, BigDecimal interestRate, Integer tenure) {
        BigDecimal monthlyRate = interestRate.divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP);
        BigDecimal interest = principal.multiply(monthlyRate).multiply(BigDecimal.valueOf(tenure));
        return principal.add(interest);
    }

    private Instant calculateDueDate(Integer tenure, TenureType tenureType) {
        return switch (tenureType) {
            case DAYS -> Instant.now().plus(tenure, ChronoUnit.DAYS);
            case MONTHS -> Instant.now().plus(tenure, ChronoUnit.MONTHS);
            case YEARS -> Instant.now().plus(tenure, ChronoUnit.YEARS);
        };
    }

    private void createInstallments(Loan loan) {
        BigDecimal installmentAmount = loan.getTotalAmount()
                .divide(BigDecimal.valueOf(loan.getTenure()), 2, RoundingMode.HALF_UP);
        BigDecimal principalPerInstallment = loan.getPrincipalAmount()
                .divide(BigDecimal.valueOf(loan.getTenure()), 2, RoundingMode.HALF_UP);
        BigDecimal interestPerInstallment = installmentAmount.subtract(principalPerInstallment);

        List<Installment> installments = new ArrayList<>();
        for (int i = 0; i < loan.getTenure(); i++) {
            Instant dueDate = calculateInstallmentDueDate(loan.getDisbursementDate(), i, loan.getTenureType());

            Installment installment = Installment.builder()
                    .loan(loan)
                    .installmentNumber(i + 1)
                    .amount(installmentAmount)
                    .principalAmount(principalPerInstallment)
                    .interestAmount(interestPerInstallment)
                    .dueDate(dueDate)
                    .outstandingBalance(installmentAmount)
                    .status(InstallmentStatus.PENDING)
                    .build();

            installments.add(installment);
        }

        // Save installments
        installmentRepository.saveAll(installments);
    }

    private Instant calculateInstallmentDueDate(Instant startDate, int monthOffset, TenureType tenureType) {
        return switch (tenureType) {
            case DAYS -> startDate.plus((monthOffset + 1) * 30, ChronoUnit.DAYS);
            case MONTHS -> startDate.plus(monthOffset + 1, ChronoUnit.MONTHS);
            case YEARS -> startDate.plus(monthOffset + 1, ChronoUnit.YEARS);
        };
    }

    private void updateCustomerLimit(Long customerId, BigDecimal amount) {
        CustomerLoanLimit limit = customerService.getCustomerLoanLimit(customerId);
        limit.setTotalOutstandingLimit(limit.getTotalOutstandingLimit().add(amount));
        limit.setAvailableLimit(limit.getMaxLoanAmount().subtract(limit.getTotalOutstandingLimit()));
        // Save limit through customer service
        customerService.updateLoanLimit(customerId, limit.getMaxLoanAmount());
    }

    private void releaseCustomerLimit(Long customerId, BigDecimal amount) {
        CustomerLoanLimit limit = customerService.getCustomerLoanLimit(customerId);
        limit.setTotalOutstandingLimit(limit.getTotalOutstandingLimit().subtract(amount));
        limit.setAvailableLimit(limit.getMaxLoanAmount().subtract(limit.getTotalOutstandingLimit()));
        // Save limit through customer service
        customerService.updateLoanLimit(customerId, limit.getMaxLoanAmount());
    }
}