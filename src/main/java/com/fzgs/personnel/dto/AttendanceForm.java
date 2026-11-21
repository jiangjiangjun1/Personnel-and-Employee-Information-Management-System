package com.fzgs.personnel.dto;

import com.fzgs.personnel.model.AttendanceRecord;
import com.fzgs.personnel.model.AttendanceStatus;

import java.time.LocalDate;
import java.time.LocalTime;

public record AttendanceForm(
        Long employeeId,
        LocalDate workDate,
        LocalTime checkIn,
        LocalTime checkOut,
        AttendanceStatus status,
        String remark
) {
    public Long getEmployeeId() { return employeeId; }
    public LocalDate getWorkDate() { return workDate; }
    public LocalTime getCheckIn() { return checkIn; }
    public LocalTime getCheckOut() { return checkOut; }
    public AttendanceStatus getStatus() { return status; }
    public String getRemark() { return remark; }

    public static AttendanceForm from(AttendanceRecord record) {
        return new AttendanceForm(
                record.employeeId(),
                record.workDate(),
                record.checkIn(),
                record.checkOut(),
                record.status(),
                record.remark()
        );
    }
}
