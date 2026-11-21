package com.fzgs.personnel.repository;

import com.fzgs.personnel.dto.LeaveForm;
import com.fzgs.personnel.model.LeaveRequest;
import com.fzgs.personnel.model.LeaveStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public class LeaveRepository {

    private static final String SELECT_LEAVE = """
            select lr.id, lr.employee_id, e.employee_no, e.name,
                   lr.leave_type, lr.start_date, lr.end_date, lr.reason, lr.status,
                   lr.approver_id, u.display_name as approver_name,
                   lr.approved_at, lr.approver_comment, lr.created_at
            from leave_requests lr
            join employees e on e.id = lr.employee_id
            left join app_users u on u.id = lr.approver_id
            """;

    private final JdbcTemplate jdbc;

    public LeaveRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public List<LeaveRequest> findAll() {
        return jdbc.query(SELECT_LEAVE + " order by lr.created_at desc, lr.id desc",
                (rs, rowNum) -> map(rs));
    }

    public List<LeaveRequest> findPending() {
        return jdbc.query(SELECT_LEAVE + " where lr.status = 'PENDING' "
                        + "order by lr.created_at desc, lr.id desc",
                (rs, rowNum) -> map(rs));
    }

    public List<LeaveRequest> findByEmployee(long employeeId) {
        return jdbc.query(SELECT_LEAVE + " where lr.employee_id = ? "
                        + "order by lr.created_at desc, lr.id desc",
                (rs, rowNum) -> map(rs),
                employeeId);
    }

    public Optional<LeaveRequest> findById(long id) {
        return jdbc.query(SELECT_LEAVE + " where lr.id = ?",
                        (rs, rowNum) -> map(rs),
                        id)
                .stream()
                .findFirst();
    }

    public long create(LeaveForm form, long employeeId) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(connection -> {
            PreparedStatement ps = connection.prepareStatement("""
                    insert into leave_requests(employee_id, leave_type, start_date, end_date,
                        reason, status)
                    values (?, ?, ?, ?, ?, 'PENDING')
                    """, Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, employeeId);
            ps.setString(2, form.leaveType());
            ps.setObject(3, form.startDate());
            ps.setObject(4, form.endDate());
            ps.setString(5, form.reason());
            return ps;
        }, keyHolder);
        Number key = keyHolder.getKey();
        if (key == null) {
            throw new IllegalStateException("新增请假申请失败：未返回主键");
        }
        return key.longValue();
    }

    public void approve(long id, boolean approved, long approverId,
                        LocalDateTime approvedAt, String comment) {
        jdbc.update("""
                update leave_requests set status = ?, approver_id = ?,
                    approved_at = ?, approver_comment = ?
                where id = ?
                """,
                approved ? LeaveStatus.APPROVED.name() : LeaveStatus.REJECTED.name(),
                approverId,
                approvedAt,
                blank(comment) ? null : comment.trim(),
                id);
    }

    private boolean blank(String value) {
        return value == null || value.isBlank();
    }

    private LeaveRequest map(java.sql.ResultSet rs) throws java.sql.SQLException {
        return new LeaveRequest(
                rs.getLong("id"),
                rs.getLong("employee_id"),
                rs.getString("employee_no"),
                rs.getString("name"),
                rs.getString("leave_type"),
                rs.getObject("start_date", java.time.LocalDate.class),
                rs.getObject("end_date", java.time.LocalDate.class),
                rs.getString("reason"),
                LeaveStatus.valueOf(rs.getString("status")),
                rs.getObject("approver_id", Long.class),
                rs.getString("approver_name"),
                rs.getObject("approved_at", LocalDateTime.class),
                rs.getString("approver_comment"),
                rs.getObject("created_at", LocalDateTime.class)
        );
    }
}
