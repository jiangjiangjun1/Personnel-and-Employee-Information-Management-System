package com.fzgs.personnel.service;

import com.fzgs.personnel.common.BusinessException;
import com.fzgs.personnel.dto.LeaveForm;
import com.fzgs.personnel.model.Employee;
import com.fzgs.personnel.model.EmployeeStatus;
import com.fzgs.personnel.model.LeaveRequest;
import com.fzgs.personnel.model.LeaveStatus;
import com.fzgs.personnel.repository.EmployeeRepository;
import com.fzgs.personnel.repository.LeaveRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Service
public class LeaveService {

    private static final Set<String> LEAVE_TYPES =
            Set.of("ANNUAL", "SICK", "PERSONAL", "OTHER");

    private final LeaveRepository leaveRepository;
    private final EmployeeRepository employeeRepository;

    public LeaveService(LeaveRepository leaveRepository,
                        EmployeeRepository employeeRepository) {
        this.leaveRepository = leaveRepository;
        this.employeeRepository = employeeRepository;
    }

    public List<LeaveRequest> findAll() {
        return leaveRepository.findAll();
    }

    public List<LeaveRequest> findPending() {
        return leaveRepository.findPending();
    }

    public List<LeaveRequest> findForEmployee(long employeeId) {
        return leaveRepository.findByEmployee(employeeId);
    }

    @Transactional
    public long submit(long employeeId, LeaveForm form) {
        validate(form);
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new BusinessException("员工不存在"));
        if (employee.status() == EmployeeStatus.RESIGNED) {
            throw new BusinessException("离职员工不能提交请假");
        }
        if (employee.status() != EmployeeStatus.ACTIVE) {
            throw new BusinessException("员工当前状态不能提交请假");
        }
        return leaveRepository.create(form, employeeId);
    }

    @Transactional
    public void approve(long leaveId, long approverUserId, boolean approved, String comment) {
        LeaveRequest request = leaveRepository.findById(leaveId)
                .orElseThrow(() -> new BusinessException("请假申请不存在"));
        if (request.status() != LeaveStatus.PENDING) {
            throw new BusinessException("该请假已处理");
        }
        leaveRepository.approve(leaveId, approved, approverUserId,
                LocalDateTime.now(), comment);
    }

    private void validate(LeaveForm form) {
        if (form.leaveType() == null || !LEAVE_TYPES.contains(form.leaveType())) {
            throw new BusinessException("请假类型不正确");
        }
        if (form.startDate() == null || form.endDate() == null) {
            throw new BusinessException("请假日期不能为空");
        }
        if (form.endDate().isBefore(form.startDate())) {
            throw new BusinessException("结束日期不能早于开始日期");
        }
        if (form.reason() == null || form.reason().isBlank()) {
            throw new BusinessException("请假原因不能为空");
        }
        if (form.reason().length() > 500) {
            throw new BusinessException("请假原因不能超过500字");
        }
    }
}
