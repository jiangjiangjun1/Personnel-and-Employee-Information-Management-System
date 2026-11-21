package com.fzgs.personnel.repository;

import com.fzgs.personnel.model.DashboardSummary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class DashboardRepository {

    private final JdbcTemplate jdbc;

    public DashboardRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public DashboardSummary summary() {
        return jdbc.queryForObject("""
                select
                    (select count(*) from employees),
                    (select count(*) from employees where status = 'ACTIVE'),
                    (select count(*) from departments),
                    (select count(*) from leave_requests where status = 'PENDING')
                """,
                (rs, rowNum) -> new DashboardSummary(
                        rs.getLong(1),
                        rs.getLong(2),
                        rs.getLong(3),
                        rs.getLong(4)
                ));
    }
}
