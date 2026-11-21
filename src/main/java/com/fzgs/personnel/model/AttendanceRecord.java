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
    public Long getId() { return id; }
    public Long getEmployeeId() { return employeeId; }
    public String getEmployeeNo() { return employeeNo; }
    public String getEmployeeName() { return employeeName; }
    public LocalDate getWorkDate() { return workDate; }
    public LocalTime getCheckIn() { return checkIn; }
    public LocalTime getCheckOut() { return checkOut; }
    public AttendanceStatus getStatus() { return status; }
    public String getRemark() { return remark; }
}
