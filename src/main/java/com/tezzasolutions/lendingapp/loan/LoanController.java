package com.tezzasolutions.lendingapp.loan;

import com.tezzasolutions.lendingapp.common.dto.ApiResponse;
import com.tezzasolutions.lendingapp.common.enums.LoanStatus;
import com.tezzasolutions.lendingapp.loan.dto.*;
import com.tezzasolutions.lendingapp.loan.dto.LoanResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/loans")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Loan Management", description = "APIs for managing loans")
public class LoanController {

    private final LoanService loanService;

    @PostMapping
    @Operation(summary = "Create a new loan")
    public ResponseEntity<ApiResponse<LoanResponse>> createLoan( @Valid @RequestBody LoanRequest request) {
        log.info("Creating loan for customer: {}", request.getCustomerId());

        Loan loan = loanService.createLoan(request);
        LoanResponse response = LoanResponse.from(loan);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(response, "/api/v1/loans"));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get loan by ID")
    public ResponseEntity<ApiResponse<LoanResponse>> getLoan(@PathVariable Long id) {
        log.info("Fetching loan with ID: {}", id);

        Loan loan = loanService.getLoanById(id);
        LoanResponse response = LoanResponse.from(loan);

        return ResponseEntity.ok(
                ApiResponse.success(response, "Loan retrieved successfully", "/api/v1/loans/" + id)
        );
    }

    @GetMapping("/customer/{customerId}")
    @Operation(summary = "Get all loans for a customer")
    public ResponseEntity<ApiResponse<List<LoanResponse>>> getCustomerLoans( @PathVariable Long customerId) {
        log.info("Fetching loans for customer: {}", customerId);

        List<Loan> loans = loanService.getCustomerLoans(customerId);
        List<LoanResponse> responses = loans.stream()
                .map(LoanResponse::from)
                .collect(Collectors.toList());

        return ResponseEntity.ok(
                ApiResponse.success(responses, "Loans retrieved successfully", "/api/v1/loans/customer/" + customerId)
        );
    }

    @GetMapping("/customer/{customerId}/status/{status}")
    @Operation(summary = "Get loans by customer and status")
    public ResponseEntity<ApiResponse<List<LoanResponse>>> getCustomerLoansByStatus( @PathVariable Long customerId, @PathVariable LoanStatus status) {
        log.info("Fetching loans for customer: {} with status: {}", customerId, status);

        List<Loan> loans = loanService.getCustomerLoansByStatus(customerId, status);
        List<LoanResponse> responses = loans.stream()
                .map(LoanResponse::from)
                .collect(Collectors.toList());

        return ResponseEntity.ok(
                ApiResponse.success(responses, "Loans retrieved successfully", "/api/v1/loans/customer/" + customerId + "/status/" + status)
        );
    }

    @GetMapping("/overdue")
    @Operation(summary = "Get all overdue loans")
    public ResponseEntity<ApiResponse<List<LoanResponse>>> getOverdueLoans() {
        log.info("Fetching overdue loans");

        List<Loan> loans = loanService.getOverdueLoans();
        List<LoanResponse> responses = loans.stream()
                .map(LoanResponse::from)
                .collect(Collectors.toList());

        return ResponseEntity.ok(
                ApiResponse.success(responses, "Overdue loans retrieved successfully", "/api/v1/loans/overdue")
        );
    }

    @PostMapping("/{id}/approve")
    @Operation(summary = "Approve a loan")
    public ResponseEntity<ApiResponse<LoanResponse>> approveLoan(@PathVariable Long id) {
        log.info("Approving loan with ID: {}", id);

        Loan loan = loanService.approveLoan(id);
        LoanResponse response = LoanResponse.from(loan);

        return ResponseEntity.ok(
                ApiResponse.success(response, "Loan approved successfully", "/api/v1/loans/" + id + "/approve")
        );
    }

    @PostMapping("/{id}/disburse")
    @Operation(summary = "Disburse a loan")
    public ResponseEntity<ApiResponse<LoanResponse>> disburseLoan(@PathVariable Long id) {
        log.info("Disbursing loan with ID: {}", id);

        Loan loan = loanService.disburseLoan(id);
        LoanResponse response = LoanResponse.from(loan);

        return ResponseEntity.ok(
                ApiResponse.success(response, "Loan disbursed successfully", "/api/v1/loans/" + id + "/disburse")
        );
    }

    @PostMapping("/{id}/cancel")
    @Operation(summary = "Cancel a loan")
    public ResponseEntity<ApiResponse<LoanResponse>> cancelLoan(@PathVariable Long id) {
        log.info("Cancelling loan with ID: {}", id);

        Loan loan = loanService.cancelLoan(id);
        LoanResponse response = LoanResponse.from(loan);

        return ResponseEntity.ok(
                ApiResponse.success(response, "Loan cancelled successfully", "/api/v1/loans/" + id + "/cancel")
        );
    }

    @PostMapping("/{id}/write-off")
    @Operation(summary = "Write off a loan")
    public ResponseEntity<ApiResponse<LoanResponse>> writeOffLoan(@PathVariable Long id) {
        log.info("Writing off loan with ID: {}", id);

        Loan loan = loanService.writeOffLoan(id);
        LoanResponse response = LoanResponse.from(loan);

        return ResponseEntity.ok(
                ApiResponse.success(response, "Loan written off successfully", "/api/v1/loans/" + id + "/write-off")
        );
    }

    @PostMapping("/process-overdue")
    @Operation(summary = "Process overdue loans")
    public ResponseEntity<ApiResponse<Void>> processOverdueLoans() {
        log.info("Processing overdue loans");

        loanService.processOverdueLoans();

        return ResponseEntity.ok(
                ApiResponse.success(null, "Overdue loans processed successfully", "/api/v1/loans/process-overdue")
        );
    }
}
