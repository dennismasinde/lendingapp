package com.tezzasolutions.lendingapp.loan;

import com.tezzasolutions.lendingapp.common.exceptions.LendingAppException;
import com.tezzasolutions.lendingapp.common.exceptions.ResourceNotFoundException;
import com.tezzasolutions.lendingapp.common.enums.TenureType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class LoanProductService {

    private final LoanProductRepository loanProductRepository;

    @Transactional
    public LoanProduct createLoanProduct(LoanProduct product) {
        log.info("Creating loan product: {}", product.getName());

        if (loanProductRepository.existsByNameIgnoreCase(product.getName())) {
            throw new LendingAppException("Loan product with name '" + product.getName() + "' already exists");
        }

        validateProductDetails(product);

        return loanProductRepository.save(product);
    }

    @Transactional
    public LoanProduct updateLoanProduct(Long productId, LoanProduct productDetails) {
        LoanProduct product = loanProductRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Loan product", "id", productId));

        product.setName(productDetails.getName());
        product.setDescription(productDetails.getDescription());
        product.setMinAmount(productDetails.getMinAmount());
        product.setMaxAmount(productDetails.getMaxAmount());
        product.setInterestRate(productDetails.getInterestRate());
        product.setMinTenure(productDetails.getMinTenure());
        product.setMaxTenure(productDetails.getMaxTenure());
        product.setTenureType(productDetails.getTenureType());

        validateProductDetails(product);

        return loanProductRepository.save(product);
    }

    @Transactional(readOnly = true)
    public LoanProduct getProductById(Long productId) {
        return loanProductRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Loan product", "id", productId));
    }

    @Transactional(readOnly = true)
    public List<LoanProduct> getActiveProducts() {
        return loanProductRepository.findByIsActiveTrue();
    }

    @Transactional(readOnly = true)
    public LoanProduct getProductByName(String name) {
        return loanProductRepository.findByName(name)
                .orElseThrow(() -> new ResourceNotFoundException("Loan product", "name", name));
    }

    @Transactional
    public void deactivateProduct(Long productId) {
        LoanProduct product = loanProductRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Loan product", "id", productId));

        product.setIsActive(false);
        loanProductRepository.save(product);
        log.info("Loan product deactivated: {}", product.getName());
    }

    @Transactional(readOnly = true)
    public List<LoanProduct> getEligibleProducts(BigDecimal amount) {
        return loanProductRepository.findEligibleProducts(amount);
    }

    @Transactional(readOnly = true)
    public List<LoanProduct> getProductsByTenureType(TenureType tenureType) {
        return loanProductRepository.findByTenureType(tenureType);
    }

    private void validateProductDetails(LoanProduct product) {
        if (product.getMinAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new LendingAppException("Minimum amount must be greater than zero");
        }

        if (product.getMaxAmount().compareTo(product.getMinAmount()) < 0) {
            throw new LendingAppException("Maximum amount must be greater than minimum amount");
        }

        if (product.getInterestRate().compareTo(BigDecimal.ZERO) < 0) {
            throw new LendingAppException("Interest rate cannot be negative");
        }

        if (product.getMinTenure() <= 0 || product.getMaxTenure() <= 0) {
            throw new LendingAppException("Tenure must be greater than zero");
        }

        if (product.getMinTenure() > product.getMaxTenure()) {
            throw new LendingAppException("Minimum tenure must be less than maximum tenure");
        }
    }
}