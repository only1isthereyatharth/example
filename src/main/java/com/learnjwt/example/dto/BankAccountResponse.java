package com.learnjwt.example.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class BankAccountResponse {
    @NotBlank
    private String accountNumber;

    @NotBlank
    private String accountType;

    @NotBlank
    private String accountHolderName;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = true)
    private BigDecimal balance;
}
