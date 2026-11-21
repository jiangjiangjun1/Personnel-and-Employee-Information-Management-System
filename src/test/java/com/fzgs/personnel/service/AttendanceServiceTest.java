package com.fzgs.personnel.service;

import com.fzgs.personnel.common.BusinessException;
import com.fzgs.personnel.dto.AttendanceForm;
import com.fzgs.personnel.model.AttendanceRecord;
import com.fzgs.personnel.model.AttendanceStatus;
import com.fzgs.personnel.model.Employee;
import com.fzgs.personnel.model.EmployeeStatus;
import com.fzgs.personnel.repository.AttendanceRepository;
import com.fzgs.personnel.repository.EmployeeRepository;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AttendanceServiceTest {

    private Optional<AttendanceRecord> existing = Optional.empty();
    private long updatedId = -1L;
    private boolean createCalled = false;

    private final EmployeeRepository employees = new EmployeeRepository(null) {
        @Override
        public Optional<Employee> findById(long id) {
            return Optional.of(employee(EmployeeStatus.ACTIVE));
        }
    };

    private final AttendanceRepository attendance = new AttendanceRepository(null) {
        @Override
        public Optional<AttendanceRecord> findByEmployeeAndDate(long employeeId, LocalDate workDate) {
            return existing;
        }

        @Override
        public long create(AttendanceForm form) {
            createCalled = true;
            return 10L;
        }

        @Override
        public void update(long id, AttendanceForm form) {
            updatedId = id;
        }
    };

    private final AttendanceService service = new AttendanceService(attendance, employees);

    @Test
    void updatesExistingRecordForSameEmployeeAndDate() {
        existing = Optional.of(new AttendanceRecord(9L, 3L, "E1003", "王五",
                LocalDate.of(2026, 11, 21), LocalTime.of(9, 0), null,
                AttendanceStatus.LATE, null));

        service.save(new AttendanceForm(3L, LocalDate.of(2026, 11, 21),
                LocalTime.of(8, 30), LocalTime.of(18, 0), AttendanceStatus.NORMAL, "补录"));

        assertThat(updatedId).isEqualTo(9L);
        assertThat(createCalled).isFalse();
    }

    @Test
    void rejectsResignedEmployee() {
        EmployeeRepository resigned = new EmployeeRepository(null) {
            @Override
            public Optional<Employee> findById(long id) {
                return Optional.of(employee(EmployeeStatus.RESIGNED));
            }
        };
        AttendanceService resignedService = new AttendanceService(attendance, resigned);

        BusinessException error = assertThrows(BusinessException.class,
                () -> resignedService.save(new AttendanceForm(3L, LocalDate.of(2026, 11, 21),
                        LocalTime.of(8, 30), LocalTime.of(18, 0), AttendanceStatus.NORMAL, "")));

        assertThat(error.getMessage()).isEqualTo("离职员工不能登记考勤");
    }

    @Test
    void rejectsCheckOutBeforeCheckIn() {
        BusinessException error = assertThrows(BusinessException.class,
                () -> service.save(new AttendanceForm(3L, LocalDate.of(2026, 11, 21),
                        LocalTime.of(18, 0), LocalTime.of(8, 30), AttendanceStatus.NORMAL, "")));

        assertThat(error.getMessage()).isEqualTo("下班时间不能早于上班时间");
    }

    @Test
    void createsAbsentRecordWithoutTimes() {
        service.save(new AttendanceForm(3L, LocalDate.of(2026, 11, 21),
                null, null, AttendanceStatus.ABSENT, "未到岗"));

        assertThat(createCalled).isTrue();
        assertThat(updatedId).isEqualTo(-1L);
    }

    @Test
    void rejectsMissingEmployee() {
        EmployeeRepository missing = new EmployeeRepository(null) {
            @Override
            public Optional<Employee> findById(long id) {
                return Optional.empty();
            }
        };
        AttendanceService missingService = new AttendanceService(attendance, missing);

        BusinessException error = assertThrows(BusinessException.class,
                () -> missingService.save(new AttendanceForm(999L, LocalDate.of(2026, 11, 21),
                        LocalTime.of(8, 30), LocalTime.of(18, 0), AttendanceStatus.NORMAL, "")));

        assertThat(error.getMessage()).isEqualTo("员工不存在");
    }

    private Employee employee(EmployeeStatus status) {
        return new Employee(
                3L, "E1003", "王五", "男", LocalDate.of(1990, 5, 20),
                "13800000003", "wangwu@example.com", "福州", LocalDate.of(2020, 1, 6),
                new BigDecimal("7000"), status, 1L, "技术部", 1L, "Java开发"
        );
    }
}
