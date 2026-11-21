package com.fzgs.personnel.repository;

import com.fzgs.personnel.dto.PayrollForm;
import com.fzgs.personnel.model.PayrollRecord;
import com.fzgs.personnel.model.PayrollStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public class PayrollRepository {

    private static final String SELECT_PAYROLL = """
            select p.id, p.employee_id, e.employee_no, e.name,
                   p.pay_month, p.base_salary, p.bonus, p.allowance,
                   p.deduction, p.social_security, p.net_salary, p.status,
                   p.paid_at, p.created_by, u.display_name as creator_name
            from payroll_records p
            join employees e on e.id = p.employee_id
            left join app_users u on u.id = p.created_by
            """;

    private final JdbcTemplate jdbc;

    public PayrollRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public List<PayrollRecord> findByMonth(String payMonth) {
        return jdbc.query(
                SELECT_PAYROLL + " where p.pay_month = ? order by e.employee_no",
                (rs, rowNum) -> map(rs),
                payMonth
        );
    }

    public List<PayrollRecord> findByEmployeeAndMonth(long employeeId, String payMonth) {
        return jdbc.query(
                SELECT_PAYROLL + " where p.employee_id = ? and p.pay_month = ? order by e.employee_no",
                (rs, rowNum) -> map(rs),
                employeeId,
                payMonth
        );
    }

    public Optional<PayrollRecord> findById(long id) {
        return jdbc.query(
                        SELECT_PAYROLL + " where p.id = ?",
                        (rs, rowNum) -> map(rs),
                        id
                )
                .stream()
                .findFirst();
    }

    public Set<Long> findEmployeeIdsForMonth(String payMonth) {
        Set<Long> ids = new HashSet<>();
        ids.addAll(jdbc.query(
                "select employee_id from payroll_records where pay_month = ?",
                (rs, rowNum) -> rs.getLong("employee_id"),
                payMonth));
        return ids;
    }

    public long createDraft(long employeeId, String payMonth, PayrollForm form,
                            BigDecimal netSalary, long createdBy) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(connection -> {
            PreparedStatement ps = connection.prepareStatement("""
                    insert into payroll_records(employee_id, pay_month, base_salary, bonus,
                        allowance, deduction, social_security, net_salary, status, created_by)
                    values (?, ?, ?, ?, ?, ?, ?, ?, 'DRAFT', ?)
                    """, Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, employeeId);
            ps.setString(2, payMonth);
            ps.setBigDecimal(3, form.baseSalary());
            ps.setBigDecimal(4, form.bonus());
            ps.setBigDecimal(5, form.allowance());
            ps.setBigDecimal(6, form.deduction());
            ps.setBigDecimal(7, form.socialSecurity());
            ps.setBigDecimal(8, netSalary);
            ps.setLong(9, createdBy);
            return ps;
        }, keyHolder);
        Number key = keyHolder.getKey();
        if (key == null) {
            throw new IllegalStateException("新增工资单失败：未返回主键");
        }
        return key.longValue();
    }

    public int updateDraft(long id, PayrollForm form, BigDecimal netSalary) {
        return jdbc.update("""
                update payroll_records set base_salary = ?, bonus = ?, allowance = ?,
                    deduction = ?, social_security = ?, net_salary = ?
                where id = ? and status = 'DRAFT'
                """,
                form.baseSalary(),
                form.bonus(),
                form.allowance(),
                form.deduction(),
                form.socialSecurity(),
                netSalary,
                id);
    }

    public int markPaid(long id, LocalDateTime paidAt) {
        return jdbc.update("""
                update payroll_records set status = 'PAID', paid_at = ?
                where id = ? and status = 'DRAFT'
                """, paidAt, id);
    }

    private PayrollRecord map(java.sql.ResultSet rs) throws java.sql.SQLException {
        return new PayrollRecord(
                rs.getLong("id"),
                rs.getLong("employee_id"),
                rs.getString("employee_no"),
                rs.getString("name"),
                rs.getString("pay_month"),
                rs.getBigDecimal("base_salary"),
                rs.getBigDecimal("bonus"),
                rs.getBigDecimal("allowance"),
                rs.getBigDecimal("deduction"),
                rs.getBigDecimal("social_security"),
                rs.getBigDecimal("net_salary"),
                PayrollStatus.valueOf(rs.getString("status")),
                rs.getObject("paid_at", LocalDateTime.class),
                rs.getLong("created_by"),
                rs.getString("creator_name")
        );
    }
}
