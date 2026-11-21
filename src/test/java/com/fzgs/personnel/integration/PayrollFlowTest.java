package com.fzgs.personnel.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
class PayrollFlowTest extends AbstractIntegrationTest {

    @Autowired
    MockMvc mvc;

    @Autowired
    JdbcTemplate jdbc;

    @Test
    void employeeCannotGeneratePayroll() throws Exception {
        mvc.perform(post("/payroll/generate")
                        .with(user("employee").roles("EMPLOYEE"))
                        .with(csrf())
                        .param("payMonth", "2027-01"))
                .andExpect(status().isForbidden());
    }

    @Test
    void hrGeneratesEditsAndPaysPayroll() throws Exception {
        mvc.perform(post("/payroll/generate")
                        .with(user("hr").roles("HR"))
                        .with(csrf())
                        .param("payMonth", "2027-01"))
                .andExpect(status().is3xxRedirection());

        Long count = jdbc.queryForObject(
                "select count(*) from payroll_records where pay_month = '2027-01'",
                Long.class);
        assertThat(count).isGreaterThan(0);

        Long draftId = jdbc.queryForObject(
                "select id from payroll_records where pay_month = '2027-01' and status = 'DRAFT' "
                        + "order by id limit 1",
                Long.class);

        mvc.perform(post("/payroll/" + draftId)
                        .with(user("hr").roles("HR"))
                        .with(csrf())
                        .param("baseSalary", "8000")
                        .param("bonus", "1000")
                        .param("allowance", "500")
                        .param("deduction", "200")
                        .param("socialSecurity", "800"))
                .andExpect(status().is3xxRedirection());

        mvc.perform(post("/payroll/" + draftId + "/paid")
                        .with(user("hr").roles("HR"))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection());

        String status = jdbc.queryForObject(
                "select status from payroll_records where id = ?", String.class, draftId);
        assertThat(status).isEqualTo("PAID");
    }
}
