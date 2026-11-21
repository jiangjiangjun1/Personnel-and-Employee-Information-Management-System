package com.fzgs.personnel.repository;

import com.fzgs.personnel.dto.PositionForm;
import com.fzgs.personnel.model.Position;
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
public class PositionRepository {

    private final JdbcTemplate jdbc;

    public PositionRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public List<Position> findAll() {
        return jdbc.query(
                "select id, code, name, level, description, status from positions order by code",
                (rs, rowNum) -> map(rs)
        );
    }

    public Optional<Position> findById(long id) {
        return jdbc.query(
                        "select id, code, name, level, description, status from positions where id = ?",
                        (rs, rowNum) -> map(rs),
                        id
                )
                .stream()
                .findFirst();
    }

    public boolean existsByCode(String code, long excludedId) {
        Integer count = jdbc.queryForObject(
                "select count(*) from positions where code = ? and id <> ?",
                Integer.class,
                code,
                excludedId
        );
        return count != null && count > 0;
    }

    public long create(PositionForm form) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(
                    "insert into positions(code, name, level, description, status) values (?, ?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS
            );
            ps.setString(1, form.code());
            ps.setString(2, form.name());
            ps.setString(3, form.level());
            ps.setString(4, form.description());
            ps.setString(5, form.status().name());
            return ps;
        }, keyHolder);
        Number key = keyHolder.getKey();
        if (key == null) {
            throw new IllegalStateException("新增岗位失败：未返回主键");
        }
        return key.longValue();
    }

    public void update(long id, PositionForm form) {
        jdbc.update(
                "update positions set code = ?, name = ?, level = ?, description = ?, status = ? where id = ?",
                form.code(),
                form.name(),
                form.level(),
                form.description(),
                form.status().name(),
                id
        );
    }

    public void updateStatus(long id, RecordStatus status) {
        jdbc.update("update positions set status = ? where id = ?", status.name(), id);
    }

    private Position map(java.sql.ResultSet rs) throws java.sql.SQLException {
        return new Position(
                rs.getLong("id"),
                rs.getString("code"),
                rs.getString("name"),
                rs.getString("level"),
                rs.getString("description"),
                RecordStatus.valueOf(rs.getString("status"))
        );
    }
}
