package com.fzgs.personnel.repository;

import com.fzgs.personnel.common.PageResult;
import com.fzgs.personnel.dto.EmployeeForm;
import com.fzgs.personnel.model.Employee;
import com.fzgs.personnel.model.EmployeeStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class EmployeeRepository {

    private static final String SELECT_EMPLOYEE = """
            select e.id, e.employee_no, e.name, e.gender, e.birth_date, e.phone, e.email,
                   e.address, e.hire_date, e.base_salary, e.status,
                   e.department_id, d.name as department_name,
                   e.position_id, p.name as position_name
            from employees e
            join departments d on d.id = e.department_id
            join positions p on p.id = e.position_id
            """;

    private final JdbcTemplate jdbc;

    public EmployeeRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public PageResult<Employee> findPage(String keyword, Long departmentId,
                                         EmployeeStatus status, int page, int size) {
        StringBuilder where = new StringBuilder(" where 1 = 1");
        List<Object> args = new ArrayList<>();
        appendLike(where, args, keyword);
        if (departmentId != null) {
            where.append(" and e.department_id = ?");
            args.add(departmentId);
        }
        if (status != null) {
            where.append(" and e.status = ?");
            args.add(status.name());
        }

        long total = countTotal(where.toString(), args);
        int safePage = Math.max(1, page);
        int offset = (safePage - 1) * size;
        List<Employee> items = jdbc.query(
                SELECT_EMPLOYEE + where + " order by e.employee_no limit ? offset ?",
                (rs, rowNum) -> map(rs),
                append(args, size, offset)
        );
        int totalPages = size == 0 ? 0 : (int) Math.ceil((double) total / size);
        return new PageResult<>(items, safePage, size, total, totalPages);
    }

    public Optional<Employee> findById(long id) {
        return jdbc.query(
                        SELECT_EMPLOYEE + " where e.id = ?",
                        (rs, rowNum) -> map(rs),
                        id
                )
                .stream()
                .findFirst();
    }

    public Optional<Employee> findByUserId(long userId) {
        return jdbc.query(
                        SELECT_EMPLOYEE + """
                                join app_users u on u.employee_id = e.id
                                where u.id = ?
                                """,
                        (rs, rowNum) -> map(rs),
                        userId
                )
                .stream()
                .findFirst();
    }

    public boolean existsByEmployeeNo(String employeeNo, long excludedId) {
        Integer count = jdbc.queryForObject(
                "select count(*) from employees where employee_no = ? and id <> ?",
                Integer.class,
                employeeNo,
                excludedId
        );
        return count != null && count > 0;
    }

    public long create(EmployeeForm form) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(connection -> {
            PreparedStatement ps = connection.prepareStatement("""
                    insert into employees(employee_no, name, gender, birth_date, phone, email,
                        address, hire_date, base_salary, status, department_id, position_id)
                    values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                    """, Statement.RETURN_GENERATED_KEYS);
            setEmployeeFields(ps, form);
            return ps;
        }, keyHolder);
        Number key = keyHolder.getKey();
        if (key == null) {
            throw new IllegalStateException("新增员工失败：未返回主键");
        }
        return key.longValue();
    }

    public void update(long id, EmployeeForm form) {
        jdbc.update("""
                update employees set employee_no = ?, name = ?, gender = ?, birth_date = ?,
                    phone = ?, email = ?, address = ?, hire_date = ?, base_salary = ?,
                    status = ?, department_id = ?, position_id = ?
                where id = ?
                """, ps -> {
            setEmployeeFields(ps, form);
            ps.setLong(13, id);
        });
    }

    public void updateStatus(long id, EmployeeStatus status) {
        jdbc.update("update employees set status = ? where id = ?", status.name(), id);
    }

    private void setEmployeeFields(PreparedStatement ps, EmployeeForm form) throws java.sql.SQLException {
        ps.setString(1, form.employeeNo());
        ps.setString(2, form.name());
        ps.setString(3, form.gender());
        setLocalDate(ps, 4, form.birthDate());
        setNullableString(ps, 5, form.phone());
        setNullableString(ps, 6, form.email());
        setNullableString(ps, 7, form.address());
        ps.setObject(8, form.hireDate());
        ps.setBigDecimal(9, form.baseSalary());
        ps.setString(10, form.status().name());
        ps.setLong(11, form.departmentId());
        ps.setLong(12, form.positionId());
    }

    private long countTotal(String where, List<Object> args) {
        Long count = jdbc.queryForObject(
                "select count(*) from employees e " + where,
                Long.class,
                args.toArray()
        );
        return count == null ? 0 : count;
    }

    private void appendLike(StringBuilder where, List<Object> args, String keyword) {
        if (keyword != null && !keyword.isBlank()) {
            where.append(" and (e.employee_no like ? or e.name like ?)");
            String like = "%" + keyword.trim() + "%";
            args.add(like);
            args.add(like);
        }
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

    private void setLocalDate(PreparedStatement ps, int index, java.time.LocalDate value)
            throws java.sql.SQLException {
        if (value == null) {
            ps.setNull(index, Types.DATE);
        } else {
            ps.setObject(index, value);
        }
    }

    private void setNullableString(PreparedStatement ps, int index, String value)
            throws java.sql.SQLException {
        if (value == null || value.isBlank()) {
            ps.setNull(index, Types.VARCHAR);
        } else {
            ps.setString(index, value);
        }
    }

    private Employee map(java.sql.ResultSet rs) throws java.sql.SQLException {
        return new Employee(
                rs.getLong("id"),
                rs.getString("employee_no"),
                rs.getString("name"),
                rs.getString("gender"),
                rs.getObject("birth_date", java.time.LocalDate.class),
                rs.getString("phone"),
                rs.getString("email"),
                rs.getString("address"),
                rs.getObject("hire_date", java.time.LocalDate.class),
                rs.getBigDecimal("base_salary"),
                EmployeeStatus.valueOf(rs.getString("status")),
                rs.getLong("department_id"),
                rs.getString("department_name"),
                rs.getLong("position_id"),
                rs.getString("position_name")
        );
    }
}
