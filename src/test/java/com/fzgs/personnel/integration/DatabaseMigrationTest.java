package com.fzgs.personnel.integration;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
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
}
