package com.fzgs.personnel.repository;

import com.fzgs.personnel.model.AppUser;
import com.fzgs.personnel.model.Role;
import com.fzgs.personnel.dto.UserForm;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Types;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class UserRepository {

    private static final String SELECT_USER =
            "select id, username, password_hash, display_name, role, enabled, employee_id " +
                    "from app_users ";

    private final JdbcTemplate jdbc;

    public UserRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public Optional<AppUser> findByUsername(String username) {
        return jdbc.query(
                SELECT_USER + "where username = ?",
                (rs, rowNum) -> map(rs),
                username
        ).stream().findFirst();
    }

    public Optional<AppUser> findById(long id) {
        return jdbc.query(
                        SELECT_USER + "where id = ?",
                        (rs, rowNum) -> map(rs),
                        id
                )
                .stream()
                .findFirst();
    }

    public List<AppUser> findAll() {
        return jdbc.query(SELECT_USER + "order by id", (rs, rowNum) -> map(rs));
    }

    public boolean existsByUsername(String username, long excludedId) {
        Integer count = jdbc.queryForObject(
                "select count(*) from app_users where username = ? and id <> ?",
                Integer.class,
                username,
                excludedId
        );
        return count != null && count > 0;
    }

    public boolean existsByEmployeeId(long employeeId, long excludedUserId) {
        Integer count = jdbc.queryForObject(
                "select count(*) from app_users where employee_id = ? and id <> ?",
                Integer.class,
                employeeId,
                excludedUserId
        );
        return count != null && count > 0;
    }

    public long create(UserForm form, String passwordHash) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(connection -> {
            PreparedStatement ps = connection.prepareStatement("""
                    insert into app_users(username, password_hash, display_name, role, enabled, employee_id)
                    values (?, ?, ?, ?, ?, ?)
                    """, Statement.RETURN_GENERATED_KEYS);
            setFields(ps, form, passwordHash);
            return ps;
        }, keyHolder);
        Number key = keyHolder.getKey();
        if (key == null) {
            throw new IllegalStateException("新增账号失败：未返回主键");
        }
        return key.longValue();
    }

    public void update(long id, UserForm form, String passwordHash) {
        jdbc.update("""
                update app_users set username = ?, password_hash = ?, display_name = ?,
                    role = ?, enabled = ?, employee_id = ?
                where id = ?
                """, ps -> {
            setFields(ps, form, passwordHash);
            ps.setLong(7, id);
        });
    }

    public void setEnabled(long id, boolean enabled) {
        jdbc.update("update app_users set enabled = ? where id = ?", enabled, id);
    }

    public Map<Long, String> findEmployeeNames() {
        Map<Long, String> names = new LinkedHashMap<>();
        jdbc.query("select id, name from employees order by id", rs -> {
            names.put(rs.getLong("id"), rs.getString("name"));
        });
        return names;
    }

    private void setFields(PreparedStatement ps, UserForm form,
                           String passwordHash) throws java.sql.SQLException {
        ps.setString(1, form.username());
        ps.setString(2, passwordHash);
        ps.setString(3, form.displayName());
        ps.setString(4, form.role().name());
        ps.setBoolean(5, form.enabled());
        setNullableLong(ps, 6, form.employeeId());
    }

    private void setNullableLong(PreparedStatement ps, int index, Long value)
            throws java.sql.SQLException {
        if (value == null) {
            ps.setNull(index, Types.BIGINT);
        } else {
            ps.setLong(index, value);
        }
    }

    private AppUser map(java.sql.ResultSet rs) throws java.sql.SQLException {
        return new AppUser(
                rs.getLong("id"),
                rs.getString("username"),
                rs.getString("password_hash"),
                rs.getString("display_name"),
                Role.valueOf(rs.getString("role")),
                rs.getBoolean("enabled"),
                rs.getObject("employee_id", Long.class)
        );
    }
}
