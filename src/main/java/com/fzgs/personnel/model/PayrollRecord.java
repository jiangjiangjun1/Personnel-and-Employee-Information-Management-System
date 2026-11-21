package com.fzgs.personnel.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PayrollRecord(
        Long id,
        Long employeeId,
        String employeeNo,
        String employeeName,
        String payMonth,
        BigDecimal baseSalary,
        BigDecimal bonus,
        BigDecimal allowance,
        BigDecimal deduction,
        BigDecimal socialSecurity,
        BigDecimal netSalary,
        PayrollStatus status,
        LocalDateTime paidAt,
        Long createdBy,
        String creatorName
) {
}
