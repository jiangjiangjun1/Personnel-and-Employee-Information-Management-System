package com.fzgs.personnel.repository;

import com.fzgs.personnel.common.PageResult;
import com.fzgs.personnel.dto.AttendanceForm;
import com.fzgs.personnel.model.AttendanceRecord;
import com.fzgs.personnel.model.AttendanceStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Types;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class AttendanceRepository {

    private static final String SELECT_ATTENDANCE = """
            select a.id, a.employee_id, e.employee_no, e.name as employee_name,
                   a.work_date, a.check_in, a.check_out, a.status, a.remark
            from attendance_records a
            join employees e on e.id = a.employee_id
            """;

    private final JdbcTemplate jdbc;

    public AttendanceRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public PageResult<AttendanceRecord> findPage(Long employeeId, LocalDate from, LocalDate to,
                                                 int page, int size) {
        StringBuilder where = new StringBuilder(" where 1 = 1");
        List<Object> args = new ArrayList<>();
        if (employeeId != null) {
            where.append(" and a.employee_id = ?");
            args.add(employeeId);
        }
        if (from != null) {
            where.append(" and a.work_date >= ?");
            args.add(from);
        }
        if (to != null) {
            where.append(" and a.work_date <= ?");
            args.add(to);
        }

        long total = countTotal(where.toString(), args);
        int safePage = Math.max(1, page);
        int offset = (safePage - 1) * size;
        List<AttendanceRecord> items = jdbc.query(
                SELECT_ATTENDANCE + where + " order by a.work_date desc, e.employee_no limit ? offset ?",
                (rs, rowNum) -> map(rs),
                append(args, size, offset)
        );
        int totalPages = size == 0 ? 0 : (int) Math.ceil((double) total / size);
        return new PageResult<>(items, safePage, size, total, totalPages);
    }

    public Optional<AttendanceRecord> findByEmployeeAndDate(long employeeId, LocalDate workDate) {
        return jdbc.query(
                        SELECT_ATTENDANCE + " where a.employee_id = ? and a.work_date = ?",
                        (rs, rowNum) -> map(rs),
                        employeeId,
                        workDate
                )
                .stream()
                .findFirst();
    }

    public long create(AttendanceForm form) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(connection -> {
            PreparedStatement ps = connection.prepareStatement("""
                    insert into attendance_records(employee_id, work_date, check_in, check_out,
                        status, remark)
                    values (?, ?, ?, ?, ?, ?)
                    """, Statement.RETURN_GENERATED_KEYS);
            setFields(ps, form);
            return ps;
        }, keyHolder);
        Number key = keyHolder.getKey();
        if (key == null) {
            throw new IllegalStateException("新增考勤失败：未返回主键");
        }
        return key.longValue();
    }

    public void update(long id, AttendanceForm form) {
        jdbc.update("""
                update attendance_records
                set employee_id = ?, work_date = ?, check_in = ?, check_out = ?,
                    status = ?, remark = ?
                where id = ?
                """, ps -> {
            setFields(ps, form);
            ps.setLong(7, id);
        });
    }

    private void setFields(PreparedStatement ps, AttendanceForm form) throws java.sql.SQLException {
        ps.setLong(1, form.employeeId());
        ps.setObject(2, form.workDate());
        setTime(ps, 3, form.checkIn());
        setTime(ps, 4, form.checkOut());
        ps.setString(5, form.status().name());
        setNullableString(ps, 6, form.remark());
    }

    private long countTotal(String where, List<Object> args) {
        Long count = jdbc.queryForObject(
                "select count(*) from attendance_records a " + where,
                Long.class,
                args.toArray()
        );
        return count == null ? 0 : count;
    }

    private Object[] append(List<Object> args, int size, int offset) {
        Object[] result = new Object[args.size() + 2];
        for (int i = 0; i < args.size(); i++) {
            result[i] = args.get(i);
        }
        result[args.size()] = size;
        result[args.size() + 1] = offset;
        return result;
    }

    private void setTime(PreparedStatement ps, int index, LocalTime value)
            throws java.sql.SQLException {
        if (value == null) {
            ps.setNull(index, Types.TIME);
        } else {
            ps.setObject(index, value);
        }
    }

    private void setNullableString(PreparedStatement ps, int index, String value)
            throws java.sql.SQLException {
        if (value == null || value.isBlank()) {
            ps.setNull(index, Types.VARCHAR);
        } else {
            ps.setString(index, value.trim());
        }
    }

    private AttendanceRecord map(java.sql.ResultSet rs) throws java.sql.SQLException {
        return new AttendanceRecord(
                rs.getLong("id"),
                rs.getLong("employee_id"),
                rs.getString("employee_no"),
                rs.getString("employee_name"),
                rs.getObject("work_date", LocalDate.class),
                rs.getObject("check_in", LocalTime.class),
                rs.getObject("check_out", LocalTime.class),
                AttendanceStatus.valueOf(rs.getString("status")),
                rs.getString("remark")
        );
    }
}
