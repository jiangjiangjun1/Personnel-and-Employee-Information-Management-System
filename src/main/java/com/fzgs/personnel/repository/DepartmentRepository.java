package com.fzgs.personnel.repository;

import com.fzgs.personnel.dto.DepartmentForm;
import com.fzgs.personnel.model.Department;
import com.fzgs.personnel.model.RecordStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

@Repository
public class DepartmentRepository {

    private final JdbcTemplate jdbc;

    public DepartmentRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public List<Department> findAll() {
        return jdbc.query(
                "select id, code, name, manager, phone, status from departments order by code",
                (rs, rowNum) -> map(rs)
        );
    }

    public Optional<Department> findById(long id) {
        return jdbc.query(
                        "select id, code, name, manager, phone, status from departments where id = ?",
                        (rs, rowNum) -> map(rs),
                        id
                )
                .stream()
                .findFirst();
    }

    public boolean existsByCode(String code, long excludedId) {
        Integer count = jdbc.queryForObject(
                "select count(*) from departments where code = ? and id <> ?",
                Integer.class,
                code,
                excludedId
        );
        return count != null && count > 0;
    }

    public long create(DepartmentForm form) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(
                    "insert into departments(code, name, manager, phone, status) values (?, ?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS
            );
            ps.setString(1, form.code());
            ps.setString(2, form.name());
            ps.setString(3, form.manager());
            ps.setString(4, form.phone());
            ps.setString(5, form.status().name());
            return ps;
        }, keyHolder);
        Number key = keyHolder.getKey();
        if (key == null) {
            throw new IllegalStateException("新增部门失败：未返回主键");
        }
        return key.longValue();
    }

    public void update(long id, DepartmentForm form) {
        jdbc.update(
                "update departments set code = ?, name = ?, manager = ?, phone = ?, status = ? where id = ?",
                form.code(),
                form.name(),
                form.manager(),
                form.phone(),
                form.status().name(),
                id
        );
    }

    public void updateStatus(long id, RecordStatus status) {
        jdbc.update("update departments set status = ? where id = ?", status.name(), id);
    }

    private Department map(java.sql.ResultSet rs) throws java.sql.SQLException {
        return new Department(
                rs.getLong("id"),
                rs.getString("code"),
                rs.getString("name"),
                rs.getString("manager"),
                rs.getString("phone"),
                RecordStatus.valueOf(rs.getString("status"))
        );
    }
}
