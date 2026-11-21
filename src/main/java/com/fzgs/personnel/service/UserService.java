package com.fzgs.personnel.service;

import com.fzgs.personnel.common.BusinessException;
import com.fzgs.personnel.dto.UserForm;
import com.fzgs.personnel.model.AppUser;
import com.fzgs.personnel.model.Employee;
import com.fzgs.personnel.model.EmployeeStatus;
import com.fzgs.personnel.model.Role;
import com.fzgs.personnel.repository.EmployeeRepository;
import com.fzgs.personnel.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository users;
    private final EmployeeRepository employees;
    private final PasswordEncoder encoder;

    public UserService(UserRepository users, EmployeeRepository employees,
                       PasswordEncoder encoder) {
        this.users = users;
        this.employees = employees;
        this.encoder = encoder;
    }

    public List<AppUser> findAll() {
        return users.findAll();
    }

    public Optional<AppUser> findById(long id) {
        return users.findById(id);
    }

    public Optional<AppUser> findByUsername(String username) {
        return users.findByUsername(username);
    }

    public List<Employee> findActiveEmployees() {
        return employees.findPage(null, null, EmployeeStatus.ACTIVE, 1, 10000).items();
    }

    public Map<Long, String> findEmployeeNames() {
        return users.findEmployeeNames();
    }

    @Transactional
    public void create(UserForm form) {
        UserForm normalized = normalize(form);
        validate(normalized, true);
        if (users.existsByUsername(normalized.username(), 0L)) {
            throw new BusinessException("用户名已存在");
        }
        validateEmployee(normalized, 0L);
        users.create(normalized, encoder.encode(normalized.password()));
    }

    @Transactional
    public void update(long id, UserForm form) {
        AppUser existing = users.findById(id)
                .orElseThrow(() -> new BusinessException("账号不存在"));
        UserForm normalized = normalize(form);
        validate(normalized, false);
        if (users.existsByUsername(normalized.username(), id)) {
            throw new BusinessException("用户名已存在");
        }
        validateEmployee(normalized, id);
        String passwordHash = normalized.password().isBlank()
                ? existing.passwordHash()
                : encoder.encode(normalized.password());
        users.update(id, normalized, passwordHash);
    }

    @Transactional
    public void setEnabled(long id, boolean enabled) {
        if (users.findById(id).isEmpty()) {
            throw new BusinessException("账号不存在");
        }
        users.setEnabled(id, enabled);
    }

    private UserForm normalize(UserForm form) {
        String username = trim(form.username());
        String displayName = trim(form.displayName());
        Long employeeId = form.role() == Role.EMPLOYEE ? form.employeeId() : null;
        return new UserForm(
                username,
                displayName,
                form.role(),
                form.enabled(),
                employeeId,
                form.password() == null ? "" : form.password()
        );
    }

    private void validate(UserForm form, boolean requirePassword) {
        if (form.username().isBlank()) {
            throw new BusinessException("用户名不能为空");
        }
        if (form.username().length() > 60) {
            throw new BusinessException("用户名长度不能超过60");
        }
        if (form.displayName().isBlank()) {
            throw new BusinessException("显示名称不能为空");
        }
        if (form.role() == null) {
            throw new BusinessException("角色不能为空");
        }
        if (form.role() == Role.EMPLOYEE && form.employeeId() == null) {
            throw new BusinessException("员工账号必须绑定员工");
        }
        if (requirePassword && form.password().isBlank()) {
            throw new BusinessException("密码不能为空");
        }
        if (!form.password().isBlank() && form.password().length() < 8) {
            throw new BusinessException("密码长度不能少于8位");
        }
    }

    private void validateEmployee(UserForm form, long excludedUserId) {
        if (form.role() != Role.EMPLOYEE) {
            return;
        }
        Employee employee = employees.findById(form.employeeId())
                .orElseThrow(() -> new BusinessException("绑定的员工不存在"));
        if (employee.status() != EmployeeStatus.ACTIVE) {
            throw new BusinessException("只能为在职员工创建账号");
        }
        if (users.existsByEmployeeId(form.employeeId(), excludedUserId)) {
            throw new BusinessException("该员工已绑定账号");
        }
    }

    private String trim(String value) {
        return value == null ? "" : value.trim();
    }
}
