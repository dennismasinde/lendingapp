package com.tezzasolutions.lendingapp.fee;

import com.tezzasolutions.lendingapp.common.enums.FeeApplicationTiming;
import com.tezzasolutions.lendingapp.common.enums.FeeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FeeRepository extends JpaRepository<Fee, Long> {

    List<Fee> findByLoanProductId(Long loanProductId);

    @Query("SELECT f FROM Fee f WHERE f.loanProduct.id = :productId AND f.isActive = true")
    List<Fee> findActiveFeesByProduct(@Param("productId") Long productId);

    @Query("SELECT f FROM Fee f WHERE f.loanProduct.id = :productId AND f.feeType = :feeType AND f.isActive = true")
    List<Fee> findFeesByProductAndType(@Param("productId") Long productId, @Param("feeType") FeeType feeType);

    @Query("SELECT f FROM Fee f WHERE f.loanProduct.id = :productId AND f.applicationTiming = :timing AND f.isActive = true")
    List<Fee> findFeesByProductAndApplicationTiming(@Param("productId") Long productId,
                                                    @Param("timing") FeeApplicationTiming timing);

    @Query("SELECT f FROM Fee f WHERE f.loanProduct.id = :productId AND f.daysAfterDue IS NOT NULL")
    List<Fee> findLateFeeConfigurations(@Param("productId") Long productId);

    List<Fee> findByIsActiveTrue();
}
