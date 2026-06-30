package com.tezzasolutions.lendingapp.customer;

import com.tezzasolutions.lendingapp.common.exceptions.InsufficientLimitException;
import com.tezzasolutions.lendingapp.common.exceptions.LendingAppException;
import com.tezzasolutions.lendingapp.common.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerLoanLimitRepository customerLoanLimitRepository;

    @Transactional
    public Customer createCustomer(Customer customer) {
        log.info("Creating new customer: {}", customer.getEmail());

        // Validate unique email and phone
        if (customerRepository.existsByEmailOrPhoneNumber(customer.getEmail(), customer.getPhoneNumber())) {
            throw new LendingAppException("Customer with this email or phone already exists");
        }

        Customer savedCustomer = customerRepository.save(customer);
        log.info("Customer created with ID: {}", savedCustomer.getId());

        // Initialize loan limit for new customer
        initializeLoanLimit(savedCustomer);

        return savedCustomer;
    }

    @Transactional
    public CustomerLoanLimit initializeLoanLimit(Customer customer) {
        CustomerLoanLimit limit = CustomerLoanLimit.builder()
                .customer(customer)
                .maxLoanAmount(BigDecimal.valueOf(100000)) // Default limit
                .totalOutstandingLimit(BigDecimal.ZERO)
                .availableLimit(BigDecimal.valueOf(100000))
                .maxNumberOfLoans(3)
                .isActive(true)
                .lastReviewDate(Instant.now())
                .riskLevel("MEDIUM")
                .build();

        return customerLoanLimitRepository.save(limit);
    }

    @Transactional
    public Customer updateCustomer(Long customerId, Customer customerDetails) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "id", customerId));

        customer.setFirstName(customerDetails.getFirstName());
        customer.setLastName(customerDetails.getLastName());
        customer.setPhoneNumber(customerDetails.getPhoneNumber());
        customer.setAddress(customerDetails.getAddress());
        customer.setEmploymentStatus(customerDetails.getEmploymentStatus());
        customer.setMonthlyIncome(customerDetails.getMonthlyIncome());
        customer.setCreditScore(customerDetails.getCreditScore());

        return customerRepository.save(customer);
    }

    @Transactional(readOnly = true)
    public Customer getCustomerById(Long customerId) {
        return customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "id", customerId));
    }

    @Transactional(readOnly = true)
    public Customer getCustomerByEmail(String email) {
        return customerRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "email", email));
    }

    @Transactional(readOnly = true)
    public Customer getCustomerByPhone(String phone) {
        return customerRepository.findByPhoneNumber(phone)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "phone", phone));
    }

    @Transactional(readOnly = true)
    public CustomerLoanLimit getCustomerLoanLimit(Long customerId) {
        return customerLoanLimitRepository.findByCustomerId(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Loan limit", "customerId", customerId));
    }

    @Transactional
    public CustomerLoanLimit updateLoanLimit(Long customerId, BigDecimal newMaxLimit) {
        CustomerLoanLimit limit = customerLoanLimitRepository.findByCustomerId(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Loan limit", "customerId", customerId));

        // Calculate new available limit based on current outstanding
        BigDecimal newAvailableLimit = newMaxLimit.subtract(limit.getTotalOutstandingLimit());
        if (newAvailableLimit.compareTo(BigDecimal.ZERO) < 0) {
            throw new LendingAppException(
                    String.format("New max limit %s is less than current outstanding %s",
                            newMaxLimit, limit.getTotalOutstandingLimit())
            );
        }

        limit.setMaxLoanAmount(newMaxLimit);
        limit.setAvailableLimit(newAvailableLimit);
        limit.setLastReviewDate(Instant.now());

        return customerLoanLimitRepository.save(limit);
    }

    @Transactional
    public void validateCustomerLimit(Long customerId, BigDecimal amount) {
        CustomerLoanLimit limit = customerLoanLimitRepository.findByCustomerId(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Loan limit", "customerId", customerId));

        if (limit.getAvailableLimit().compareTo(amount) < 0) {
            throw new InsufficientLimitException(
                    String.format("Insufficient available limit. Available: %s, Requested: %s",
                            limit.getAvailableLimit(), amount)
            );
        }
    }

    @Transactional(readOnly = true)
    public boolean customerHasActiveLoans(Long customerId) {
        // Check if customer has any active loans
        long activeCount = 0; // This would use a loan repository method
        return activeCount > 0;
    }

    @Transactional
    public void deactivateCustomer(Long customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "id", customerId));

        // Check if customer has any active loans
        if (customerHasActiveLoans(customerId)) {
            throw new LendingAppException("Cannot deactivate customer with active loans");
        }

        customer.setIsActive(false);
        customerRepository.save(customer);

        // Deactivate loan limit
        CustomerLoanLimit limit = customerLoanLimitRepository.findByCustomerId(customerId)
                .orElse(null);
        if (limit != null) {
            limit.setIsActive(false);
            customerLoanLimitRepository.save(limit);
        }

        log.info("Customer deactivated: {}", customerId);
    }
}