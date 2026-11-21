package com.fzgs.personnel.service;

import com.fzgs.personnel.common.BusinessException;
import com.fzgs.personnel.dto.EmployeeForm;
import com.fzgs.personnel.model.Department;
import com.fzgs.personnel.model.Employee;
import com.fzgs.personnel.model.EmployeeStatus;
import com.fzgs.personnel.model.Position;
import com.fzgs.personnel.model.RecordStatus;
import com.fzgs.personnel.repository.DepartmentRepository;
import com.fzgs.personnel.repository.EmployeeRepository;
import com.fzgs.personnel.repository.PositionRepository;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class EmployeeServiceTest {

    EmployeeForm created;

    EmployeeRepository employees = new EmployeeRepository(null) {
        @Override
        public boolean existsByEmployeeNo(String employeeNo, long excludedId) {
            return "E1001".equals(employeeNo);
        }

        @Override
        public long create(EmployeeForm form) {
            created = form;
            return 1L;
        }
    };

    DepartmentRepository departments = new DepartmentRepository(null) {
        @Override
        public Optional<Department> findById(long id) {
            return Optional.of(new Department(id, "TECH", "技术部", "李强",
                    "13800000002", RecordStatus.ACTIVE));
        }
    };

    PositionRepository positions = new PositionRepository(null) {
        @Override
        public Optional<Position> findById(long id) {
            return Optional.of(new Position(id, "DEV", "开发工程师", "P5",
                    "负责业务系统开发", RecordStatus.ACTIVE));
        }
    };

    EmployeeService service = new EmployeeService(employees, departments, positions);

    @Test
    void rejectsDuplicateEmployeeNumber() {
        BusinessException error = assertThrows(BusinessException.class,
                () -> service.create(form("E1001", "张三")));

        assertThat(error.getMessage()).isEqualTo("工号已存在");
    }

    @Test
    void normalizesEmployeeNumberAndTrimsText() {
        service.create(form(" e2026001 ", " 陈晨 "));

        assertThat(created.employeeNo()).isEqualTo("E2026001");
        assertThat(created.name()).isEqualTo("陈晨");
    }

    @Test
    void rejectsBlankEmployeeName() {
        BusinessException error = assertThrows(BusinessException.class,
                () -> service.create(form("E2026002", " ")));

        assertThat(error.getMessage()).isEqualTo("姓名不能为空");
    }

    @Test
    void rejectsNegativeBaseSalary() {
        EmployeeForm invalid = new EmployeeForm("E2026002", "李四", "男", null,
                null, null, null, LocalDate.of(2026, 11, 21),
                new BigDecimal("-1"), 2L, 1L, EmployeeStatus.ACTIVE);

        BusinessException error = assertThrows(BusinessException.class,
                () -> service.create(invalid));

        assertThat(error.getMessage()).isEqualTo("基本工资不能为负数");
    }

    @Test
    void rejectsInvalidEmail() {
        EmployeeForm invalid = new EmployeeForm("E2026002", "李四", "男", null,
                null, "not-an-email", null, LocalDate.of(2026, 11, 21),
                new BigDecimal("7000"), 2L, 1L, EmployeeStatus.ACTIVE);

        BusinessException error = assertThrows(BusinessException.class,
                () -> service.create(invalid));

        assertThat(error.getMessage()).isEqualTo("邮箱格式不正确");
    }

    @Test
    void rejectsMissingDepartment() {
        EmployeeForm invalid = new EmployeeForm("E2026002", "李四", "男", null,
                null, null, null, LocalDate.of(2026, 11, 21),
                new BigDecimal("7000"), 999L, 1L, EmployeeStatus.ACTIVE);

        DepartmentRepository empty = new DepartmentRepository(null) {
            @Override
            public Optional<Department> findById(long id) {
                return Optional.empty();
            }
        };

        BusinessException error = assertThrows(BusinessException.class,
                () -> new EmployeeService(employees, empty, positions).create(invalid));

        assertThat(error.getMessage()).isEqualTo("部门不存在");
    }

    private EmployeeForm form(String employeeNo, String name) {
        return new EmployeeForm(employeeNo, name, "男", null, null, null, null,
                LocalDate.of(2026, 11, 21), new BigDecimal("7000"),
                2L, 1L, EmployeeStatus.ACTIVE);
    }
}
