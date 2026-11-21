package com.fzgs.personnel.dto;

import java.math.BigDecimal;

public record PayrollForm(
        BigDecimal baseSalary,
        BigDecimal bonus,
        BigDecimal allowance,
        BigDecimal deduction,
        BigDecimal socialSecurity
) {
    public BigDecimal getBaseSalary() { return baseSalary; }
    public BigDecimal getBonus() { return bonus; }
    public BigDecimal getAllowance() { return allowance; }
    public BigDecimal getDeduction() { return deduction; }
    public BigDecimal getSocialSecurity() { return socialSecurity; }
}
