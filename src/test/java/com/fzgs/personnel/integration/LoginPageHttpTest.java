package com.fzgs.personnel.integration;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.net.URI;
import java.net.CookieManager;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class LoginPageHttpTest extends AbstractIntegrationTest {

    @LocalServerPort
    private int port;

    @Test
    void anonymousLoginPageRendersWithoutRedirectLoop() throws Exception {
        HttpClient client = newClient();
        HttpResponse<String> response = client.send(
                get("/login"), HttpResponse.BodyHandlers.ofString());

        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.body()).contains("人事员工信息管理系统", "用户名", "密码");
    }

    @Test
    void adminCanLogInAndRenderDashboard() throws Exception {
        HttpClient client = loginAsAdmin();
        HttpResponse<String> dashboard = client.send(
                get("/dashboard"), HttpResponse.BodyHandlers.ofString());

        assertThat(dashboard.statusCode()).isEqualTo(200);
        assertThat(dashboard.body()).contains("工作台", "待审批请假", "系统管理员");
    }

    @Test
    void adminPagesRenderWithPersistentSidebar() throws Exception {
        HttpClient client = loginAsAdmin();
        for (String path : new String[]{
                "/dashboard", "/departments", "/departments/new",
                "/positions", "/positions/new", "/users", "/users/new",
                "/employees", "/employees/new", "/attendance", "/attendance/new",
                "/leave", "/payroll", "/profile"
        }) {
            HttpResponse<String> response = client.send(
                    get(path), HttpResponse.BodyHandlers.ofString());
            assertThat(response.statusCode()).as(path).isEqualTo(200);
            assertThat(response.body()).as(path)
                    .contains("人事员工信息管理系统", "class=\"sidebar\"", "个人中心");
        }
    }

    @Test
    void employeePagesRenderWithPersistentSidebar() throws Exception {
        HttpClient client = loginAsEmployee();
        for (String path : new String[]{"/dashboard", "/leave", "/leave/new", "/profile"}) {
            HttpResponse<String> response = client.send(
                    get(path), HttpResponse.BodyHandlers.ofString());
            assertThat(response.statusCode()).as(path).isEqualTo(200);
            assertThat(response.body()).as(path)
                    .contains("class=\"sidebar\"", "我的请假", "个人中心");
        }
    }

    private HttpClient loginAsAdmin() throws Exception {
        return loginAs("admin");
    }

    private HttpClient loginAsEmployee() throws Exception {
        return loginAs("employee");
    }

    private HttpClient loginAs(String username) throws Exception {
        HttpClient client = newClient();
        HttpResponse<String> loginPage = client.send(
                get("/login"), HttpResponse.BodyHandlers.ofString());
        String csrf = extractCsrf(loginPage.body());
        String form = "username=" + username + "&password=123456&_csrf="
                + URLEncoder.encode(csrf, StandardCharsets.UTF_8);

        HttpRequest login = HttpRequest.newBuilder(uri("/login"))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(form))
                .build();
        HttpResponse<String> loginResponse = client.send(
                login, HttpResponse.BodyHandlers.ofString());
        assertThat(loginResponse.statusCode()).isEqualTo(302);
        assertThat(loginResponse.headers().firstValue("Location").orElse(""))
                .endsWith("/dashboard");
        return client;
    }

    private HttpClient newClient() {
        return HttpClient.newBuilder()
                .cookieHandler(new CookieManager())
                .followRedirects(HttpClient.Redirect.NEVER)
                .build();
    }

    private HttpRequest get(String path) {
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
