package com.fzgs.personnel.dto;

import java.time.LocalDate;

public record LeaveForm(
        String leaveType,
        LocalDate startDate,
        LocalDate endDate,
        String reason
) {
}
