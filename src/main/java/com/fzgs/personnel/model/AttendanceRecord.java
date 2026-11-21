package com.fzgs.personnel.model;

import java.time.LocalDate;
import java.time.LocalTime;

public record AttendanceRecord(
        Long id,
        Long employeeId,
        String employeeNo,
        String employeeName,
        LocalDate workDate,
        LocalTime checkIn,
        LocalTime checkOut,
        AttendanceStatus status,
        String remark
) {
}
