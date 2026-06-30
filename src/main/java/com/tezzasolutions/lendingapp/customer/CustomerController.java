package com.tezzasolutions.lendingapp.customer;

import com.tezzasolutions.lendingapp.common.dto.ApiResponse;
import com.tezzasolutions.lendingapp.customer.dto.CustomerLoanLimitResponse;
import com.tezzasolutions.lendingapp.customer.dto.CustomerRequest;
import com.tezzasolutions.lendingapp.customer.dto.CustomerResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/v1/customers")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Customer Management", description = "APIs for managing customers")
public class CustomerController {

    private final CustomerService customerService;

    @PostMapping
    @Operation(summary = "Create a new customer")
    public ResponseEntity<ApiResponse<CustomerResponse>> createCustomer( @Valid @RequestBody CustomerRequest request) {
        log.info("Creating customer with email: {}", request.getEmail());

        Customer customer = Customer.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .dateOfBirth(request.getDateOfBirth())
                .nationalId(request.getNationalId())
                .address(request.getAddress())
                .city(request.getCity())
                .country(request.getCountry())
                .postalCode(request.getPostalCode())
                .employmentStatus(request.getEmploymentStatus())
                .employerName(request.getEmployerName())
                .monthlyIncome(request.getMonthlyIncome())
                .creditScore(request.getCreditScore())
                .isActive(true)
                .build();

        Customer saved = customerService.createCustomer(customer);
        CustomerResponse response = CustomerResponse.from(saved);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(response, "/api/v1/customers"));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get customer by ID")
    public ResponseEntity<ApiResponse<CustomerResponse>> getCustomer(@PathVariable Long id) {
        log.info("Fetching customer with ID: {}", id);

        Customer customer = customerService.getCustomerById(id);
        CustomerResponse response = CustomerResponse.from(customer);

        return ResponseEntity.ok(
                ApiResponse.success(response, "Customer retrieved successfully", "/api/v1/customers/" + id)
        );
    }

    @GetMapping("/email/{email}")
    @Operation(summary = "Get customer by email")
    public ResponseEntity<ApiResponse<CustomerResponse>> getCustomerByEmail(@PathVariable String email) {
        log.info("Fetching customer with email: {}", email);

        Customer customer = customerService.getCustomerByEmail(email);
        CustomerResponse response = CustomerResponse.from(customer);

        return ResponseEntity.ok(
                ApiResponse.success(response, "Customer retrieved successfully", "/api/v1/customers/email/" + email)
        );
    }

    @GetMapping("/{id}/loan-limit")
    @Operation(summary = "Get customer loan limit")
    public ResponseEntity<ApiResponse<CustomerLoanLimitResponse>> getCustomerLoanLimit(@PathVariable Long id) {
        log.info("Fetching loan limit for customer: {}", id);

        CustomerLoanLimit limit = customerService.getCustomerLoanLimit(id);
        CustomerLoanLimitResponse response = CustomerLoanLimitResponse.from(limit);

        return ResponseEntity.ok(
                ApiResponse.success(response, "Loan limit retrieved successfully", "/api/v1/customers/" + id + "/loan-limit")
        );
    }

    @PutMapping("/{id}/loan-limit")
    @Operation(summary = "Update customer loan limit")
    public ResponseEntity<ApiResponse<CustomerLoanLimitResponse>> updateCustomerLoanLimit( @PathVariable Long id, @RequestParam BigDecimal newLimit) {
        log.info("Updating loan limit for customer: {} to {}", id, newLimit);

        CustomerLoanLimit limit = customerService.updateLoanLimit(id, newLimit);
        CustomerLoanLimitResponse response = CustomerLoanLimitResponse.from(limit);

        return ResponseEntity.ok(
                ApiResponse.success(response, "Loan limit updated successfully", "/api/v1/customers/" + id + "/loan-limit")
        );
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update customer")
    public ResponseEntity<ApiResponse<CustomerResponse>> updateCustomer( @PathVariable Long id, @Valid @RequestBody CustomerRequest request) {
        log.info("Updating customer with ID: {}", id);

        Customer customer = Customer.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .phoneNumber(request.getPhoneNumber())
                .address(request.getAddress())
                .employmentStatus(request.getEmploymentStatus())
                .monthlyIncome(request.getMonthlyIncome())
                .creditScore(request.getCreditScore())
                .build();

        Customer updated = customerService.updateCustomer(id, customer);
        CustomerResponse response = CustomerResponse.from(updated);

        return ResponseEntity.ok(
                ApiResponse.success(response, "Customer updated successfully", "/api/v1/customers/" + id)
        );
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deactivate customer")
    public ResponseEntity<ApiResponse<Void>> deactivateCustomer(@PathVariable Long id) {
        log.info("Deactivating customer with ID: {}", id);

        customerService.deactivateCustomer(id);

        return ResponseEntity.ok(
                ApiResponse.success(null, "Customer deactivated successfully", "/api/v1/customers/" + id)
        );
    }
}
