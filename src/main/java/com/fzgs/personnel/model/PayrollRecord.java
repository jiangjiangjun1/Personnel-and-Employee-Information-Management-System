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
    public Long getId() { return id; }
    public Long getEmployeeId() { return employeeId; }
    public String getEmployeeNo() { return employeeNo; }
    public String getEmployeeName() { return employeeName; }
    public String getPayMonth() { return payMonth; }
    public BigDecimal getBaseSalary() { return baseSalary; }
    public BigDecimal getBonus() { return bonus; }
    public BigDecimal getAllowance() { return allowance; }
    public BigDecimal getDeduction() { return deduction; }
    public BigDecimal getSocialSecurity() { return socialSecurity; }
    public BigDecimal getNetSalary() { return netSalary; }
    public PayrollStatus getStatus() { return status; }
    public LocalDateTime getPaidAt() { return paidAt; }
    public Long getCreatedBy() { return createdBy; }
    public String getCreatorName() { return creatorName; }
}
