package com.fzgs.personnel.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
class EmployeeFlowTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private JdbcTemplate jdbc;

    @Test
    void hrCanCreateEmployee() throws Exception {
        mvc.perform(post("/employees")
                        .with(user("hr").roles("HR"))
                        .with(csrf())
                        .param("employeeNo", "E2026001")
                        .param("name", "陈晨")
                        .param("gender", "女")
                        .param("birthDate", "1995-11-08")
                        .param("phone", "13800000005")
                        .param("email", "chenchen2@example.com")
                        .param("hireDate", "2026-11-21")
                        .param("baseSalary", "8000")
                        .param("departmentId", "1")
                        .param("positionId", "1")
                        .param("status", "ACTIVE"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/employees"));

        assertThat(jdbc.queryForObject(
                "select count(*) from employees where employee_no = 'E2026001'",
                Long.class)).isEqualTo(1);
    }

    @Test
    void employeeCannotOpenEmployeeManagement() throws Exception {
        mvc.perform(get("/employees").with(user("employee").roles("EMPLOYEE")))
                .andExpect(status().isForbidden());
    }

    @Test
    void duplicateEmployeeNumberShowsReadableError() throws Exception {
        mvc.perform(post("/employees")
                        .with(user("hr").roles("HR"))
                        .with(csrf())
                        .param("employeeNo", "E1001")
                        .param("name", "重复工号")
                        .param("gender", "男")
                        .param("hireDate", "2026-11-21")
                        .param("baseSalary", "7000")
                        .param("departmentId", "1")
                        .param("positionId", "1")
                        .param("status", "ACTIVE"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/employees/new"))
                .andExpect(flash().attribute("error", "工号已存在"));
    }
}
