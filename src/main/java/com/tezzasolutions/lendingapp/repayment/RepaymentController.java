package com.tezzasolutions.lendingapp.repayment;

import com.tezzasolutions.lendingapp.common.dto.ApiResponse;
import com.tezzasolutions.lendingapp.repayment.dto.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/repayments")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Repayment Management", description = "APIs for managing loan repayments")
public class RepaymentController {

    private final RepaymentService repaymentService;

    @PostMapping
    @Operation(summary = "Process a repayment")
    public ResponseEntity<ApiResponse<RepaymentResponse>> processRepayment(
            @Valid @RequestBody RepaymentRequest request) {
        log.info("Processing repayment for loan: {}", request.getLoanId());

        Repayment repayment = repaymentService.processRepayment(request);
        RepaymentResponse response = RepaymentResponse.from(repayment);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(response, "/api/v1/repayments"));
    }

    @GetMapping("/loan/{loanId}")
    @Operation(summary = "Get all repayments for a loan")
    public ResponseEntity<ApiResponse<List<RepaymentResponse>>> getLoanRepayments(@PathVariable Long loanId) {
        log.info("Fetching repayments for loan: {}", loanId);

        List<Repayment> repayments = repaymentService.getLoanRepayments(loanId);
        List<RepaymentResponse> responses = repayments.stream()
                .map(RepaymentResponse::from)
                .collect(Collectors.toList());

        return ResponseEntity.ok(
                ApiResponse.success(responses, "Repayments retrieved successfully", "/api/v1/repayments/loan/" + loanId)
        );
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get repayment by ID")
    public ResponseEntity<ApiResponse<RepaymentResponse>> getRepayment(@PathVariable Long id) {
        log.info("Fetching repayment with ID: {}", id);

        Repayment repayment = repaymentService.getRepaymentById(id);
        RepaymentResponse response = RepaymentResponse.from(repayment);

        return ResponseEntity.ok(
                ApiResponse.success(response, "Repayment retrieved successfully", "/api/v1/repayments/" + id)
        );
    }

    @PostMapping("/{id}/reverse")
    @Operation(summary = "Reverse a repayment")
    public ResponseEntity<ApiResponse<RepaymentResponse>> reverseRepayment(@PathVariable Long id) {
        log.info("Reversing repayment with ID: {}", id);

        Repayment repayment = repaymentService.reverseRepayment(id);
        RepaymentResponse response = RepaymentResponse.from(repayment);

        return ResponseEntity.ok(
                ApiResponse.success(response, "Repayment reversed successfully", "/api/v1/repayments/" + id + "/reverse")
        );
    }

    @GetMapping("/loan/{loanId}/total-repaid")
    @Operation(summary = "Get total repaid amount for a loan")
    public ResponseEntity<ApiResponse<BigDecimal>> getTotalRepaidForLoan(@PathVariable Long loanId) {
        log.info("Fetching total repaid for loan: {}", loanId);

        BigDecimal total = repaymentService.getTotalRepaidForLoan(loanId);

        return ResponseEntity.ok(
                ApiResponse.success(total, "Total repaid amount retrieved successfully", "/api/v1/repayments/loan/" + loanId + "/total-repaid")
        );
    }
}
