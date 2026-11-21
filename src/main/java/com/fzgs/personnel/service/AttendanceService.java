package com.fzgs.personnel.service;

import com.fzgs.personnel.common.BusinessException;
import com.fzgs.personnel.common.PageResult;
import com.fzgs.personnel.dto.AttendanceForm;
import com.fzgs.personnel.model.AttendanceRecord;
import com.fzgs.personnel.model.AttendanceStatus;
import com.fzgs.personnel.model.Employee;
import com.fzgs.personnel.model.EmployeeStatus;
import com.fzgs.personnel.repository.AttendanceRepository;
import com.fzgs.personnel.repository.EmployeeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
public class AttendanceService {

    private final AttendanceRepository attendance;
    private final EmployeeRepository employees;

    public AttendanceService(AttendanceRepository attendance, EmployeeRepository employees) {
        this.attendance = attendance;
        this.employees = employees;
    }

    public PageResult<AttendanceRecord> findPage(Long employeeId, LocalDate from, LocalDate to,
                                                 int page, int size) {
        return attendance.findPage(employeeId, from, to, page, size);
    }

    @Transactional
    public void save(AttendanceForm form) {
        validateForm(form);
        Employee employee = employees.findById(form.employeeId())
                .orElseThrow(() -> new BusinessException("员工不存在"));
        if (employee.status() == EmployeeStatus.RESIGNED) {
            throw new BusinessException("离职员工不能登记考勤");
        }

        attendance.findByEmployeeAndDate(form.employeeId(), form.workDate())
                .ifPresentOrElse(
                        existing -> attendance.update(existing.id(), form),
                        () -> attendance.create(form)
                );
    }

    private void validateForm(AttendanceForm form) {
        if (form.employeeId() == null) {
            throw new BusinessException("请选择员工");
        }
        if (form.workDate() == null) {
            throw new BusinessException("考勤日期不能为空");
        }
        if (form.status() == null) {
            throw new BusinessException("考勤状态不能为空");
        }
        if (form.status() != AttendanceStatus.ABSENT
                && (form.checkIn() == null || form.checkOut() == null)) {
            throw new BusinessException("非缺勤记录需要填写上下班时间");
        }
        if (form.checkIn() != null && form.checkOut() != null
                && form.checkOut().isBefore(form.checkIn())) {
            throw new BusinessException("下班时间不能早于上班时间");
        }
    }
}
