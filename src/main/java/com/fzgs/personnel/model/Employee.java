package com.fzgs.personnel.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public record Employee(
        Long id,
        String employeeNo,
        String name,
        String gender,
        LocalDate birthDate,
        String phone,
        String email,
        String address,
        LocalDate hireDate,
        BigDecimal baseSalary,
        EmployeeStatus status,
        Long departmentId,
        String departmentName,
        Long positionId,
        String positionName
) {
}
