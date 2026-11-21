package com.fzgs.personnel.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.forwardedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

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
}
