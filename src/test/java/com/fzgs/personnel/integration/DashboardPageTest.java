package com.fzgs.personnel.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import com.fzgs.personnel.model.AttendanceRecord;
import com.fzgs.personnel.model.PayrollRecord;

import java.util.List;
import java.util.Map;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.forwardedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.assertj.core.api.Assertions.assertThat;

@AutoConfigureMockMvc
class DashboardPageTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mvc;

    @Test
    void adminCanRenderDashboard() throws Exception {
        mvc.perform(get("/dashboard").with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(view().name("dashboard/index"))
                .andExpect(forwardedUrl("/WEB-INF/jsp/dashboard/index.jsp"));
    }

    @Test
    void employeeCanRenderProfile() throws Exception {
        mvc.perform(get("/profile").with(user("employee").roles("EMPLOYEE")))
                .andExpect(status().isOk())
                .andExpect(view().name("profile/index"))
                .andExpect(forwardedUrl("/WEB-INF/jsp/profile/index.jsp"));
    }

    @Test
    void employeeDashboardShowsOnlyOwnPayrollAndAttendance() throws Exception {
        MvcResult result = mvc.perform(get("/dashboard")
                        .with(user("employee").roles("EMPLOYEE")))
                .andExpect(status().isOk())
                .andExpect(view().name("dashboard/index"))
                .andReturn();

        Map<String, Object> model = result.getModelAndView().getModel();

        assertThat(model.get("employeeView")).isEqualTo(true);
        assertThat(model).doesNotContainKey("summary");

        @SuppressWarnings("unchecked")
        List<PayrollRecord> payroll = (List<PayrollRecord>) model.get("payrollRecords");
        assertThat(payroll).hasSize(1)
                .allSatisfy(record -> assertThat(record.employeeId()).isEqualTo(3L));

        @SuppressWarnings("unchecked")
        List<AttendanceRecord> attendance = (List<AttendanceRecord>) model.get("recentAttendance");
        assertThat(attendance).hasSize(1)
                .allSatisfy(record -> assertThat(record.employeeId()).isEqualTo(3L));
    }
}
