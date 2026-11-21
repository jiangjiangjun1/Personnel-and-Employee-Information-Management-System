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
}
