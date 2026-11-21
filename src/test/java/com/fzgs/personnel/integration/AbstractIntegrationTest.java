package com.fzgs.personnel.integration;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;

@SpringBootTest
public abstract class AbstractIntegrationTest {

    private static final boolean CI_DB =
        Boolean.parseBoolean(System.getenv().getOrDefault("CI_DB", "false"));

    private static final String MYSQL_IMAGE =
        System.getenv().getOrDefault("TEST_MYSQL_IMAGE", "mysql:8.0");

    private static final MySQLContainer<?> MYSQL = CI_DB ? null :
        new MySQLContainer<>(MYSQL_IMAGE)
            .withDatabaseName("personnel_management")
            .withUsername("personnel")
            .withPassword("personnel_test");

    static {
        if (MYSQL != null) {
            MYSQL.start();
        }
    }

    @DynamicPropertySource
    static void database(DynamicPropertyRegistry registry) {
        if (MYSQL != null) {
            registry.add("spring.datasource.url", MYSQL::getJdbcUrl);
            registry.add("spring.datasource.username", MYSQL::getUsername);
            registry.add("spring.datasource.password", MYSQL::getPassword);
        }
    }
}
