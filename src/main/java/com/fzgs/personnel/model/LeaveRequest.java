package com.fzgs.personnel.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record LeaveRequest(
        Long id,
        Long employeeId,
        String employeeNo,
        String employeeName,
        String leaveType,
        LocalDate startDate,
        LocalDate endDate,
        String reason,
        LeaveStatus status,
        Long approverId,
        String approverName,
        LocalDateTime approvedAt,
        String approverComment,
        LocalDateTime createdAt
) {
    public Long getId() { return id; }
    public Long getEmployeeId() { return employeeId; }
    public String getEmployeeNo() { return employeeNo; }
    public String getEmployeeName() { return employeeName; }
    public String getLeaveType() { return leaveType; }
    public LocalDate getStartDate() { return startDate; }
    public LocalDate getEndDate() { return endDate; }
    public String getReason() { return reason; }
    public LeaveStatus getStatus() { return status; }
    public Long getApproverId() { return approverId; }
    public String getApproverName() { return approverName; }
    public LocalDateTime getApprovedAt() { return approvedAt; }
    public String getApproverComment() { return approverComment; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
