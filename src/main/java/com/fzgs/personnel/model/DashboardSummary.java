package com.fzgs.personnel.model;

public record DashboardSummary(
        long employeeCount,
        long activeEmployeeCount,
        long departmentCount,
        long pendingLeaveCount
) {
    public long getEmployeeCount() { return employeeCount; }
    public long getActiveEmployeeCount() { return activeEmployeeCount; }
    public long getDepartmentCount() { return departmentCount; }
    public long getPendingLeaveCount() { return pendingLeaveCount; }
}
