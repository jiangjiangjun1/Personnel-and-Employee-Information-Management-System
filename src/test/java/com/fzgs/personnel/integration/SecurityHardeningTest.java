package com.fzgs.personnel.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;

import java.net.CookieManager;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class SecurityHardeningTest extends AbstractIntegrationTest {

    private static final String XSS_PAYLOAD = "<script>alert('xss')</script>";

    @Autowired
    private MockMvc mvc;

    @Autowired
    private JdbcTemplate jdbc;

    @LocalServerPort
    private int port;

    @Test
    void unknownPageReturnsFriendly404() throws Exception {
        mvc.perform(get("/missing-page").with(user("admin").roles("ADMIN")))
            .andExpect(status().isNotFound())
            .andExpect(view().name("error/404"));
    }

    @Test
    void invalidRequestParameterReturns400() throws Exception {
        mvc.perform(get("/employees")
                .param("departmentId", "not-a-number")
                .with(user("admin").roles("ADMIN")))
            .andExpect(status().isBadRequest())
            .andExpect(view().name("error/400"));
    }

    @Test
    void storedMarkupIsEscapedInRenderedJsp() throws Exception {
        String original = jdbc.queryForObject(
            "select manager from departments where id = 1", String.class);
        jdbc.update("update departments set manager = ? where id = 1", XSS_PAYLOAD);

        try {
            HttpClient client = loginAsAdmin();
            HttpResponse<String> response = client.send(
                httpGet("/departments"), HttpResponse.BodyHandlers.ofString());

            assertThat(response.statusCode()).isEqualTo(200);
            assertThat(response.body()).contains("&lt;script&gt;");
            assertThat(response.body()).doesNotContain(XSS_PAYLOAD);
        } finally {
            jdbc.update("update departments set manager = ? where id = 1", original);
        }
    }

    private HttpClient loginAsAdmin() throws Exception {
        HttpClient client = HttpClient.newBuilder()
            .cookieHandler(new CookieManager())
            .followRedirects(HttpClient.Redirect.NEVER)
            .build();
        HttpResponse<String> loginPage = client.send(
            httpGet("/login"), HttpResponse.BodyHandlers.ofString());
        String csrf = extractCsrf(loginPage.body());
        String form = "username=admin&password=123456&_csrf="
            + URLEncoder.encode(csrf, StandardCharsets.UTF_8);

        HttpRequest request = HttpRequest.newBuilder(uri("/login"))
            .header("Content-Type", "application/x-www-form-urlencoded")
            .POST(HttpRequest.BodyPublishers.ofString(form))
            .build();
        HttpResponse<String> login = client.send(
            request, HttpResponse.BodyHandlers.ofString());
        assertThat(login.statusCode()).isEqualTo(302);
        return client;
    }

    private HttpRequest httpGet(String path) {
        return HttpRequest.newBuilder(uri(path)).GET().build();
    }

    private URI uri(String path) {
        return URI.create("http://127.0.0.1:" + port + path);
    }

    private String extractCsrf(String body) {
        Matcher matcher = Pattern.compile("name=\"_csrf\" value=\"([^\"]+)\"")
            .matcher(body);
        assertThat(matcher.find()).isTrue();
        return matcher.group(1);
    }
}
