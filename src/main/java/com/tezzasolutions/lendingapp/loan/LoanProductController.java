package com.tezzasolutions.lendingapp.loan;

import com.tezzasolutions.lendingapp.common.dto.ApiResponse;
import com.tezzasolutions.lendingapp.loan.dto.LoanProductRequest;
import com.tezzasolutions.lendingapp.loan.dto.LoanProductResponse;
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
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Loan Product Management", description = "APIs for managing loan products")
public class LoanProductController {

    private final LoanProductService loanProductService;

    @PostMapping
    @Operation(summary = "Create a new loan product")
    public ResponseEntity<ApiResponse<LoanProductResponse>> createProduct( @Valid @RequestBody LoanProductRequest request) {
        log.info("Creating loan product: {}", request.getName());

        LoanProduct product = LoanProduct.builder()
                .name(request.getName())
                .description(request.getDescription())
                .minAmount(request.getMinAmount())
                .maxAmount(request.getMaxAmount())
                .interestRate(request.getInterestRate())
                .minTenure(request.getMinTenure())
                .maxTenure(request.getMaxTenure())
                .tenureType(request.getTenureType())
                .isActive(true)
                .build();

        LoanProduct saved = loanProductService.createLoanProduct(product);
        LoanProductResponse response = LoanProductResponse.from(saved);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(response, "/api/v1/products"));
    }

    @GetMapping
    @Operation(summary = "Get all active loan products")
    public ResponseEntity<ApiResponse<List<LoanProductResponse>>> getActiveProducts() {
        log.info("Fetching all active loan products");

        List<LoanProduct> products = loanProductService.getActiveProducts();
        List<LoanProductResponse> responses = products.stream()
                .map(LoanProductResponse::from)
                .collect(Collectors.toList());

        return ResponseEntity.ok(
                ApiResponse.success(responses, "Products retrieved successfully", "/api/v1/products")
        );
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get loan product by ID")
    public ResponseEntity<ApiResponse<LoanProductResponse>> getProduct( @PathVariable Long id) {
        log.info("Fetching loan product with ID: {}", id);

        LoanProduct product = loanProductService.getProductById(id);
        LoanProductResponse response = LoanProductResponse.from(product);

        return ResponseEntity.ok(
                ApiResponse.success(response, "Product retrieved successfully", "/api/v1/products/" + id)
        );
    }

    @GetMapping("/eligible")
    @Operation(summary = "Get eligible loan products for amount")
    public ResponseEntity<ApiResponse<List<LoanProductResponse>>> getEligibleProducts( @RequestParam BigDecimal amount) {
        log.info("Fetching eligible products for amount: {}", amount);

        List<LoanProduct> products = loanProductService.getEligibleProducts(amount);
        List<LoanProductResponse> responses = products.stream()
                .map(LoanProductResponse::from)
                .collect(Collectors.toList());

        return ResponseEntity.ok(
                ApiResponse.success(responses, "Eligible products retrieved successfully", "/api/v1/products/eligible")
        );
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update loan product")
    public ResponseEntity<ApiResponse<LoanProductResponse>> updateProduct( @PathVariable Long id, @Valid @RequestBody LoanProductRequest request) {
        log.info("Updating loan product with ID: {}", id);

        LoanProduct product = LoanProduct.builder()
                .name(request.getName())
                .description(request.getDescription())
                .minAmount(request.getMinAmount())
                .maxAmount(request.getMaxAmount())
                .interestRate(request.getInterestRate())
                .minTenure(request.getMinTenure())
                .maxTenure(request.getMaxTenure())
                .tenureType(request.getTenureType())
                .build();

        LoanProduct updated = loanProductService.updateLoanProduct(id, product);
        LoanProductResponse response = LoanProductResponse.from(updated);

        return ResponseEntity.ok(
                ApiResponse.success(response, "Product updated successfully", "/api/v1/products/" + id)
        );
    }

    @PatchMapping("/{id}/deactivate")
    @Operation(summary = "Deactivate loan product")
    public ResponseEntity<ApiResponse<Void>> deactivateProduct(@PathVariable Long id) {
        log.info("Deactivating loan product with ID: {}", id);

        loanProductService.deactivateProduct(id);

        return ResponseEntity.ok(
                ApiResponse.success(null, "Product deactivated successfully", "/api/v1/products/" + id + "/deactivate")
        );
    }
}
