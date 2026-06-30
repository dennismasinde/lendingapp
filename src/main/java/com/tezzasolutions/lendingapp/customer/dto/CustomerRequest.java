package com.tezzasolutions.lendingapp.customer.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.tezzasolutions.lendingapp.customer.Customer;
import com.tezzasolutions.lendingapp.customer.CustomerLoanLimit;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerRequest {
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateOfBirth;
    private String nationalId;
    private String address;
    private String city;
    private String country;
    private String postalCode;
    private String employmentStatus;
    private String employerName;
    private BigDecimal monthlyIncome;
    private BigDecimal creditScore;
}

