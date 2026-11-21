package com.fzgs.personnel.dto;

import java.time.LocalDate;

public record LeaveForm(
        String leaveType,
        LocalDate startDate,
        LocalDate endDate,
        String reason
) {
    public String getLeaveType() { return leaveType; }
    public LocalDate getStartDate() { return startDate; }
    public LocalDate getEndDate() { return endDate; }
    public String getReason() { return reason; }
}
