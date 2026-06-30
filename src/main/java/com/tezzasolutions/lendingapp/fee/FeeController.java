package com.tezzasolutions.lendingapp.fee;

import com.tezzasolutions.lendingapp.common.dto.ApiResponse;
import com.tezzasolutions.lendingapp.fee.dto.FeeRequest;
import com.tezzasolutions.lendingapp.fee.dto.FeeResponse;
import com.tezzasolutions.lendingapp.loan.LoanProduct;
import com.tezzasolutions.lendingapp.loan.LoanProductService;
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
@RequestMapping("/api/v1/fees")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Fee Management", description = "APIs for managing loan fees")
public class FeeController {

    private final FeeService feeService;
    private final LoanProductService loanProductService;

    @PostMapping
    @Operation(summary = "Create a new fee")
    public ResponseEntity<ApiResponse<FeeResponse>> createFee( @Valid @RequestBody FeeRequest request) {
        log.info("Creating fee: {}", request.getName());

        LoanProduct product = loanProductService.getProductById(request.getLoanProductId());

        Fee fee = Fee.builder()
                .name(request.getName())
                .feeType(request.getFeeType())
                .calculationType(request.getCalculationType())
                .amount(request.getAmount())
                .percentage(request.getPercentage())
                .daysAfterDue(request.getDaysAfterDue())
                .applicationTiming(request.getApplicationTiming())
                .loanProduct(product)
                .isActive(true)
                .build();

        Fee saved = feeService.createFee(fee);
        FeeResponse response = FeeResponse.from(saved);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(response, "/api/v1/fees"));
    }

    @GetMapping("/product/{productId}")
    @Operation(summary = "Get fees by product")
    public ResponseEntity<ApiResponse<List<FeeResponse>>> getFeesByProduct(@PathVariable Long productId) {
        log.info("Fetching fees for product: {}", productId);

        List<Fee> fees = feeService.getFeesByProduct(productId);
        List<FeeResponse> responses = fees.stream()
                .map(FeeResponse::from)
                .collect(Collectors.toList());

        return ResponseEntity.ok(
                ApiResponse.success(responses, "Fees retrieved successfully", "/api/v1/fees/product/" + productId)
        );
    }

    @GetMapping("/product/{productId}/active")
    @Operation(summary = "Get active fees by product")
    public ResponseEntity<ApiResponse<List<FeeResponse>>> getActiveFeesByProduct(@PathVariable Long productId) {
        log.info("Fetching active fees for product: {}", productId);

        List<Fee> fees = feeService.getActiveFeesByProduct(productId);
        List<FeeResponse> responses = fees.stream()
                .map(FeeResponse::from)
                .collect(Collectors.toList());

        return ResponseEntity.ok(
                ApiResponse.success(responses, "Active fees retrieved successfully", "/api/v1/fees/product/" + productId + "/active")
        );
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update fee")
    public ResponseEntity<ApiResponse<FeeResponse>> updateFee( @PathVariable Long id, @Valid @RequestBody FeeRequest request) {
        log.info("Updating fee with ID: {}", id);

        LoanProduct product = loanProductService.getProductById(request.getLoanProductId());

        Fee fee = Fee.builder()
                .name(request.getName())
                .feeType(request.getFeeType())
                .calculationType(request.getCalculationType())
                .amount(request.getAmount())
                .percentage(request.getPercentage())
                .daysAfterDue(request.getDaysAfterDue())
                .applicationTiming(request.getApplicationTiming())
                .loanProduct(product)
                .isActive(true)
                .build();

        Fee updated = feeService.updateFee(id, fee);
        FeeResponse response = FeeResponse.from(updated);

        return ResponseEntity.ok(
                ApiResponse.success(response, "Fee updated successfully", "/api/v1/fees/" + id)
        );
    }

    @PatchMapping("/{id}/deactivate")
    @Operation(summary = "Deactivate fee")
    public ResponseEntity<ApiResponse<Void>> deactivateFee(@PathVariable Long id) {
        log.info("Deactivating fee with ID: {}", id);

        feeService.deactivateFee(id);

        return ResponseEntity.ok(
                ApiResponse.success(null, "Fee deactivated successfully", "/api/v1/fees/" + id + "/deactivate")
        );
    }
}
