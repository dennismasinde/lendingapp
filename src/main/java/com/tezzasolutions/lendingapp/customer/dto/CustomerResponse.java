package com.tezzasolutions.lendingapp.customer.dto;

import com.tezzasolutions.lendingapp.customer.Customer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerResponse {
    private Long id;
    private String firstName;
    private String lastName;
    private String fullName;
    private String email;
    private String phoneNumber;
    private String employmentStatus;
    private BigDecimal monthlyIncome;
    private BigDecimal creditScore;
    private Boolean isActive;
    private String createdAt;
    private String updatedAt;

    public static CustomerResponse from(Customer customer) {
        return CustomerResponse.builder()
                .id(customer.getId())
                .firstName(customer.getFirstName())
                .lastName(customer.getLastName())
                .fullName(customer.getFullName())
                .email(customer.getEmail())
                .phoneNumber(customer.getPhoneNumber())
                .employmentStatus(customer.getEmploymentStatus())
                .monthlyIncome(customer.getMonthlyIncome())
                .creditScore(customer.getCreditScore())
                .isActive(customer.getIsActive())
                .createdAt(customer.getCreatedAt() != null ? customer.getCreatedAt().toString() : null)
                .updatedAt(customer.getUpdatedAt() != null ? customer.getUpdatedAt().toString() : null)
                .build();
    }
}
