package com.fzgs.personnel.service;

import com.fzgs.personnel.common.BusinessException;
import com.fzgs.personnel.dto.LeaveForm;
import com.fzgs.personnel.model.Employee;
import com.fzgs.personnel.model.EmployeeStatus;
import com.fzgs.personnel.model.LeaveRequest;
import com.fzgs.personnel.model.LeaveStatus;
import com.fzgs.personnel.repository.EmployeeRepository;
import com.fzgs.personnel.repository.LeaveRepository;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class LeaveServiceTest {

    private final FakeLeaveRepository leaves = new FakeLeaveRepository();
    private final EmployeeRepository employees = new EmployeeRepository(null) {
        @Override
        public Optional<Employee> findById(long id) {
            return Optional.of(employee(id, EmployeeStatus.ACTIVE));
        }
    };
    private final LeaveService service = new LeaveService(leaves, employees);

    @Test
    void rejectsEndDateBeforeStartDate() {
        BusinessException error = assertThrows(BusinessException.class,
                () -> service.submit(3L, new LeaveForm("ANNUAL",
                        LocalDate.of(2026, 11, 22),
                        LocalDate.of(2026, 11, 21),
                        "家中有事")));
        assertThat(error.getMessage()).isEqualTo("结束日期不能早于开始日期");
    }

    @Test
    void rejectsApprovalOfProcessedRequest() {
        leaves.found = request(LeaveStatus.APPROVED);

        BusinessException error = assertThrows(BusinessException.class,
                () -> service.approve(11L, 2L, true, "同意"));
        assertThat(error.getMessage()).isEqualTo("该请假已处理");
    }

    @Test
    void rejectsSubmissionByResignedEmployee() {
        EmployeeRepository resigned = new EmployeeRepository(null) {
            @Override
            public Optional<Employee> findById(long id) {
                return Optional.of(employee(id, EmployeeStatus.RESIGNED));
            }
        };
        LeaveService serviceForResigned = new LeaveService(leaves, resigned);

        BusinessException error = assertThrows(BusinessException.class,
                () -> serviceForResigned.submit(3L, validForm()));
        assertThat(error.getMessage()).isEqualTo("离职员工不能提交请假");
    }

    @Test
    void createsPendingRequestForActiveEmployee() {
        service.submit(3L, validForm());

        assertThat(leaves.created).isTrue();
        assertThat(leaves.createdEmployeeId).isEqualTo(3L);
        assertThat(leaves.createdForm.leaveType()).isEqualTo("ANNUAL");
    }

    private LeaveForm validForm() {
        return new LeaveForm("ANNUAL",
                LocalDate.of(2026, 12, 1),
                LocalDate.of(2026, 12, 3),
                "年假回乡");
    }

    private Employee employee(long id, EmployeeStatus status) {
        return new Employee(id, "E1003", "陈晨", "女", LocalDate.of(1995, 11, 8),
                "13800000005", "chenchen@example.com", "福州市仓山区",
                LocalDate.of(2022, 3, 1), new BigDecimal("8000"), status,
                2L, "技术部", 1L, "开发工程师");
    }

    private LeaveRequest request(LeaveStatus status) {
        return new LeaveRequest(11L, 3L, "E1003", "陈晨", "ANNUAL",
                LocalDate.of(2026, 12, 1), LocalDate.of(2026, 12, 3),
                "年假回乡", status, null, null, null, null,
                LocalDateTime.now());
    }

    private static class FakeLeaveRepository extends LeaveRepository {
        private LeaveRequest found;
        private boolean created;
        private long createdEmployeeId;
        private LeaveForm createdForm;

        FakeLeaveRepository() {
            super(null);
        }

        @Override
        public Optional<LeaveRequest> findById(long id) {
            return Optional.ofNullable(found);
        }

        @Override
        public long create(LeaveForm form, long employeeId) {
            this.created = true;
            this.createdEmployeeId = employeeId;
            this.createdForm = form;
            return 99L;
        }
    }
}
