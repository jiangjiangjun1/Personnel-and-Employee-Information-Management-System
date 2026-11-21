package com.fzgs.personnel.integration;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = {
        "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration",
        "logging.level.org.flywaydb=ERROR"
    })
class ApplicationSmokeTest extends AbstractIntegrationTest {

    @LocalServerPort
    private int port;

    @Test
    void applicationStarts() {
        assertThat(port).isPositive();
    }
}
