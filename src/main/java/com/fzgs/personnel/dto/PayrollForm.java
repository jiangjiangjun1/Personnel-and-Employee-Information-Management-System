package com.fzgs.personnel.dto;

import java.math.BigDecimal;

public record PayrollForm(
        BigDecimal baseSalary,
        BigDecimal bonus,
        BigDecimal allowance,
        BigDecimal deduction,
        BigDecimal socialSecurity
) {
}
