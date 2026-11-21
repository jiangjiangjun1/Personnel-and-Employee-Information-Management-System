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
class LeaveFlowTest extends AbstractIntegrationTest {

    @Autowired
    MockMvc mvc;

    @Autowired
    JdbcTemplate jdbc;

    @Test
    void employeeCanSubmitPendingLeave() throws Exception {
        mvc.perform(post("/leave")
                        .with(user("employee").roles("EMPLOYEE"))
                        .with(csrf())
                        .param("leaveType", "ANNUAL")
                        .param("startDate", "2026-12-01")
                        .param("endDate", "2026-12-03")
                        .param("reason", "年假回乡"))
                .andExpect(status().is3xxRedirection());

        Long count = jdbc.queryForObject(
                "select count(*) from leave_requests where employee_id = 3 "
                        + "and start_date = '2026-12-01' and end_date = '2026-12-03'",
                Long.class);
        assertThat(count).isEqualTo(1);
    }

    @Test
    void employeeCannotApproveLeave() throws Exception {
        mvc.perform(post("/leave/2/approval")
                        .with(user("employee").roles("EMPLOYEE"))
                        .with(csrf())
                        .param("approved", "true")
                        .param("comment", "同意"))
                .andExpect(status().isForbidden());
    }

    @Test
    void hrCanApprovePendingLeave() throws Exception {
        mvc.perform(post("/leave/2/approval")
                        .with(user("hr").roles("HR"))
                        .with(csrf())
                        .param("approved", "true")
                        .param("comment", "同意"))
                .andExpect(status().is3xxRedirection());

        String status = jdbc.queryForObject(
                "select status from leave_requests where id = 2", String.class);
        Long approverId = jdbc.queryForObject(
                "select approver_id from leave_requests where id = 2", Long.class);
        assertThat(status).isEqualTo("APPROVED");
        assertThat(approverId).isEqualTo(2L);
    }
}
