package com.fzgs.personnel.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
class SecurityFlowTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mvc;

    @Test
    void anonymousUserIsRedirectedToLogin() throws Exception {
        mvc.perform(get("/dashboard"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    void employeeCannotOpenDepartmentManagement() throws Exception {
        mvc.perform(formLogin().user("employee").password("123456"))
            .andExpect(authenticated().withUsername("employee"));

        mvc.perform(get("/departments").with(user("employee").roles("EMPLOYEE")))
            .andExpect(status().isForbidden());
    }

    @Test
    void seededAdminCanLogIn() throws Exception {
        mvc.perform(formLogin().user("admin").password("123456"))
            .andExpect(authenticated().withUsername("admin"));
    }
}
