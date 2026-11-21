package com.fzgs.personnel.repository;

import com.fzgs.personnel.model.AppUser;
import com.fzgs.personnel.model.Role;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class UserRepository {

    private final JdbcTemplate jdbc;

    public UserRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public Optional<AppUser> findByUsername(String username) {
        return jdbc.query(
                "select id, username, password_hash, display_name, role, enabled, employee_id " +
                        "from app_users where username = ?",
                (rs, rowNum) -> new AppUser(
                        rs.getLong("id"),
                        rs.getString("username"),
                        rs.getString("password_hash"),
                        rs.getString("display_name"),
                        Role.valueOf(rs.getString("role")),
                        rs.getBoolean("enabled"),
                        rs.getObject("employee_id", Long.class)
                ),
                username
        ).stream().findFirst();
    }
}
