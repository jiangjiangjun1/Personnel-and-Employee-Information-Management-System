package com.fzgs.personnel.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;

class DatabaseMigrationTest extends AbstractIntegrationTest {

    @Autowired
    private JdbcTemplate jdbc;

    @Test
    void createsSevenCoreTablesAndSeedAccount() {
        List<String> tables = jdbc.queryForList(
            "select table_name from information_schema.tables where table_schema = database()",
            String.class);

        assertThat(tables).contains(
            "departments", "positions", "employees", "app_users",
            "attendance_records", "leave_requests", "payroll_records");
        assertThat(jdbc.queryForObject(
            "select count(*) from app_users where username = 'admin'", Long.class)).isEqualTo(1);
    }

    @Test
    void seedsAllDemoAccountsWithRolesAndEmployeeBinding() {
        assertAccount("admin", "ADMIN", false);
        assertAccount("hr", "HR", false);
        assertAccount("employee", "EMPLOYEE", true);
    }

    @Test
    void rejectsDuplicateEmployeeNumber() {
        assertThatThrownBy(() -> jdbc.update(
            "insert into employees (employee_no, name, gender, hire_date, base_salary, status, department_id, position_id) "
                + "values (?, ?, ?, ?, ?, ?, ?, ?)",
            "E1003", "重复工号", "女", LocalDate.of(2026, 11, 1),
            0, "ACTIVE", 1, 1))
            .isInstanceOf(DataIntegrityViolationException.class);
    }

    private void assertAccount(String username, String expectedRole, boolean expectEmployee) {
        assertThat(jdbc.queryForObject(
            "select count(*) from app_users where username = ?", Long.class, username)).isEqualTo(1);
        assertThat(jdbc.queryForObject(
            "select role from app_users where username = ?", String.class, username))
            .isEqualTo(expectedRole);

        Long employeeId = jdbc.queryForObject(
            "select employee_id from app_users where username = ?", Long.class, username);
        if (expectEmployee) {
            assertThat(employeeId).isNotNull();
        } else {
            assertThat(employeeId).isNull();
        }
    }
}
