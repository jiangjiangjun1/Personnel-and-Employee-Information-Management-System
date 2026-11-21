package com.fzgs.personnel.service;

import com.fzgs.personnel.common.BusinessException;
import com.fzgs.personnel.common.PageResult;
import com.fzgs.personnel.dto.EmployeeForm;
import com.fzgs.personnel.model.Department;
import com.fzgs.personnel.model.Employee;
import com.fzgs.personnel.model.EmployeeStatus;
import com.fzgs.personnel.model.Position;
import com.fzgs.personnel.model.RecordStatus;
import com.fzgs.personnel.repository.DepartmentRepository;
import com.fzgs.personnel.repository.EmployeeRepository;
import com.fzgs.personnel.repository.PositionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class EmployeeService {

    private final EmployeeRepository repository;
    private final DepartmentRepository departments;
    private final PositionRepository positions;

    public EmployeeService(EmployeeRepository repository,
                           DepartmentRepository departments,
                           PositionRepository positions) {
        this.repository = repository;
        this.departments = departments;
        this.positions = positions;
    }

    public PageResult<Employee> findPage(String keyword, Long departmentId,
                                         EmployeeStatus status, int page, int size) {
        return repository.findPage(keyword, departmentId, status, page, size);
    }

    public Optional<Employee> findById(long id) {
        return repository.findById(id);
    }

    public Optional<Employee> findByUserId(long userId) {
        return repository.findByUserId(userId);
    }

    public List<Department> findDepartments() {
        return departments.findAll().stream()
                .filter(department -> department.status() == RecordStatus.ACTIVE)
                .toList();
    }

    public List<Position> findPositions() {
        return positions.findAll().stream()
                .filter(position -> position.status() == RecordStatus.ACTIVE)
                .toList();
    }

    @Transactional
    public void create(EmployeeForm form) {
        EmployeeForm normalized = normalize(form);
        validateRequired(normalized);
        if (repository.existsByEmployeeNo(normalized.employeeNo(), 0L)) {
            throw new BusinessException("工号已存在");
        }
        validateReferences(normalized);
        repository.create(normalized);
    }

    @Transactional
    public void update(long id, EmployeeForm form) {
        if (repository.findById(id).isEmpty()) {
            throw new BusinessException("员工不存在");
        }
        EmployeeForm normalized = normalize(form);
        validateRequired(normalized);
        if (repository.existsByEmployeeNo(normalized.employeeNo(), id)) {
            throw new BusinessException("工号已存在");
        }
        validateReferences(normalized);
        repository.update(id, normalized);
    }

    @Transactional
    public void updateStatus(long id, EmployeeStatus status) {
        if (repository.findById(id).isEmpty()) {
            throw new BusinessException("员工不存在");
        }
        repository.updateStatus(id, status);
    }

    @Transactional
    public void updateContact(long employeeId, String phone, String email, String address) {
        if (repository.findById(employeeId).isEmpty()) {
            throw new BusinessException("员工不存在");
        }
        String normalizedEmail = blankToNull(email);
        if (normalizedEmail != null && !normalizedEmail.matches(
                "^[\\w.%+-]+@[\\w.-]+\\.[A-Za-z]{2,}$")) {
            throw new BusinessException("邮箱格式不正确");
        }
        repository.updateContact(employeeId, blankToNull(phone),
                normalizedEmail, blankToNull(address));
    }

    private EmployeeForm normalize(EmployeeForm form) {
        return new EmployeeForm(
                trim(form.employeeNo()).toUpperCase(),
                trim(form.name()),
                trim(form.gender()),
                form.birthDate(),
                blankToNull(form.phone()),
                blankToNull(form.email()),
                blankToNull(form.address()),
                form.hireDate(),
                form.baseSalary(),
                form.departmentId(),
                form.positionId(),
                form.status()
        );
    }

    private void validateRequired(EmployeeForm form) {
        if (form.employeeNo().isBlank()) {
            throw new BusinessException("工号不能为空");
        }
        if (form.employeeNo().length() > 24) {
            throw new BusinessException("工号长度不能超过24");
        }
        if (form.name().isBlank()) {
            throw new BusinessException("姓名不能为空");
        }
        if (form.name().length() > 60) {
            throw new BusinessException("姓名长度不能超过60");
        }
        if (form.gender().isBlank()) {
            throw new BusinessException("性别不能为空");
        }
        if (form.email() != null && !form.email().matches(
                "^[\\w.%+-]+@[\\w.-]+\\.[A-Za-z]{2,}$")) {
            throw new BusinessException("邮箱格式不正确");
        }
        if (form.hireDate() == null) {
            throw new BusinessException("入职日期不能为空");
        }
        if (form.baseSalary() == null || form.baseSalary().compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException("基本工资不能为负数");
        }
        if (form.status() == null) {
            throw new BusinessException("员工状态不能为空");
        }
    }

    private void validateReferences(EmployeeForm form) {
        if (form.departmentId() == null) {
            throw new BusinessException("部门不存在");
        }
        Department department = departments.findById(form.departmentId())
                .orElseThrow(() -> new BusinessException("部门不存在"));
        if (department.status() != RecordStatus.ACTIVE) {
            throw new BusinessException("部门已停用，不能分配给员工");
        }
        if (form.positionId() == null) {
            throw new BusinessException("岗位不存在");
        }
        Position position = positions.findById(form.positionId())
                .orElseThrow(() -> new BusinessException("岗位不存在"));
        if (position.status() != RecordStatus.ACTIVE) {
            throw new BusinessException("岗位已停用，不能分配给员工");
        }
    }

    private String trim(String value) {
        return value == null ? "" : value.trim();
    }

    private String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
