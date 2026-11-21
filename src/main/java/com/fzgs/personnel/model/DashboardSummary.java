package com.fzgs.personnel.model;

public record DashboardSummary(
        long employeeCount,
        long activeEmployeeCount,
        long departmentCount,
        long pendingLeaveCount
) {
}
