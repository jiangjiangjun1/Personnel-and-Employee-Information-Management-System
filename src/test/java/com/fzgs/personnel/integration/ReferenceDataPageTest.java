package com.fzgs.personnel.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@AutoConfigureMockMvc
class ReferenceDataPageTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mvc;

    @Test
    void adminCanOpenDepartmentAndPositionForms() throws Exception {
        mvc.perform(get("/departments/new").with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(view().name("department/form"))
                .andExpect(model().attributeExists("form"));

        mvc.perform(get("/departments/1/edit").with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(view().name("department/form"))
                .andExpect(model().attributeExists("id", "form"));

        mvc.perform(get("/positions/new").with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(view().name("position/form"))
                .andExpect(model().attributeExists("form"));

        mvc.perform(get("/positions/1/edit").with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(view().name("position/form"))
                .andExpect(model().attributeExists("id", "form"));
    }

    @Test
    void missingDepartmentStatusShowsReadableError() throws Exception {
        mvc.perform(post("/departments/999999/status")
                        .with(user("admin").roles("ADMIN"))
                        .with(csrf())
                        .param("status", "ACTIVE"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/departments"))
                .andExpect(flash().attribute("error", "部门不存在"));
    }

    @Test
    void missingPositionStatusShowsReadableError() throws Exception {
        mvc.perform(post("/positions/999999/status")
                        .with(user("admin").roles("ADMIN"))
                        .with(csrf())
                        .param("status", "ACTIVE"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/positions"))
                .andExpect(flash().attribute("error", "岗位不存在"));
    }
}
