package com.fzgs.personnel.dto;

import com.fzgs.personnel.model.Employee;
import com.fzgs.personnel.model.EmployeeStatus;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

public record EmployeeForm(
        @NotBlank @Size(max = 24) String employeeNo,
        @NotBlank @Size(max = 60) String name,
        @NotBlank String gender,
        LocalDate birthDate,
        String phone,
        @Email String email,
        String address,
        @NotNull LocalDate hireDate,
        @NotNull @DecimalMin("0") BigDecimal baseSalary,
        @NotNull Long departmentId,
        @NotNull Long positionId,
        @NotNull EmployeeStatus status
) {
    public String getEmployeeNo() { return employeeNo; }
    public String getName() { return name; }
    public String getGender() { return gender; }
    public LocalDate getBirthDate() { return birthDate; }
    public String getPhone() { return phone; }
    public String getEmail() { return email; }
    public String getAddress() { return address; }
    public LocalDate getHireDate() { return hireDate; }
    public BigDecimal getBaseSalary() { return baseSalary; }
    public Long getDepartmentId() { return departmentId; }
    public Long getPositionId() { return positionId; }
    public EmployeeStatus getStatus() { return status; }

    public static EmployeeForm from(Employee employee) {
        return new EmployeeForm(
                employee.employeeNo(),
                employee.name(),
                employee.gender(),
                employee.birthDate(),
                employee.phone(),
                employee.email(),
                employee.address(),
                employee.hireDate(),
                employee.baseSalary(),
                employee.departmentId(),
                employee.positionId(),
                employee.status()
        );
    }
}
