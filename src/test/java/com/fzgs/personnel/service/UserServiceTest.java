package com.fzgs.personnel.service;

import com.fzgs.personnel.common.BusinessException;
import com.fzgs.personnel.dto.UserForm;
import com.fzgs.personnel.integration.AbstractIntegrationTest;
import com.fzgs.personnel.model.AppUser;
import com.fzgs.personnel.model.EmployeeStatus;
import com.fzgs.personnel.model.Role;
import com.fzgs.personnel.repository.EmployeeRepository;
import com.fzgs.personnel.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Transactional
class UserServiceTest extends AbstractIntegrationTest {

    @Autowired
    private UserService service;

    @Autowired
    private UserRepository users;

    @Autowired
    private EmployeeRepository employees;

    @Autowired
    private PasswordEncoder encoder;

    @Test
    void rejectsDuplicateUsername() {
        UserForm form = new UserForm("admin", "管理员二号", Role.ADMIN, true, null, "12345678");

        assertThatThrownBy(() -> service.create(form))
                .isInstanceOf(BusinessException.class)
                .hasMessage("用户名已存在");
    }

    @Test
    void storesBcryptHashInsteadOfPlainPassword() {
        Long employeeId = employees.findPage(null, null, EmployeeStatus.ACTIVE, 1, 10)
                .items().get(0).id();
        String username = "employee_created_" + System.nanoTime();
        service.create(new UserForm(username, "绑定员工账号", Role.EMPLOYEE, true,
                employeeId, "12345678"));

        AppUser saved = users.findByUsername(username).orElseThrow();
        assertThat(saved.passwordHash()).isNotEqualTo("12345678");
        assertThat(encoder.matches("12345678", saved.passwordHash())).isTrue();
    }

    @Test
    void rejectsShortPassword() {
        assertThatThrownBy(() -> service.create(
                new UserForm("new_hr_" + System.nanoTime(), "新人事", Role.HR,
                        true, null, "123")))
                .isInstanceOf(BusinessException.class)
                .hasMessage("密码长度不能少于8位");
    }
}
