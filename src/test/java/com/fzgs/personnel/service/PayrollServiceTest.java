package com.fzgs.personnel.service;

import com.fzgs.personnel.common.BusinessException;
import com.fzgs.personnel.dto.PayrollForm;
import com.fzgs.personnel.model.Employee;
import com.fzgs.personnel.model.EmployeeStatus;
import com.fzgs.personnel.model.PayrollRecord;
import com.fzgs.personnel.model.PayrollStatus;
import com.fzgs.personnel.repository.EmployeeRepository;
import com.fzgs.personnel.repository.PayrollRepository;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PayrollServiceTest {

    private static final PayrollForm SAMPLE = new PayrollForm(
            new BigDecimal("8000"), new BigDecimal("1000"), new BigDecimal("500"),
            new BigDecimal("200"), new BigDecimal("800"));

    private final List<Employee> activeEmployees = new ArrayList<>();
    private final Set<Long> existingForMonth = new HashSet<>();
    private final List<Long> created = new ArrayList<>();
    private Optional<PayrollRecord> found = Optional.empty();
    private long updatedId = -1L;
    private PayrollForm updatedForm = null;
    private BigDecimal updatedNet = null;
    private long paidId = -1L;
    private LocalDateTime paidAt = null;

    private final EmployeeRepository employees = new EmployeeRepository(null) {
        @Override
        public List<Employee> findActiveEmployees() {
            return activeEmployees;
        }
    };

    private final PayrollRepository repository = new PayrollRepository(null) {
        @Override
        public Set<Long> findEmployeeIdsForMonth(String payMonth) {
            return existingForMonth;
        }

        @Override
        public Optional<PayrollRecord> findById(long id) {
            return found;
        }

        @Override
        public long createDraft(long employeeId, String payMonth, PayrollForm form,
                                BigDecimal netSalary, long createdBy) {
            created.add(employeeId);
            return 100L + employeeId;
        }

        @Override
        public int updateDraft(long id, PayrollForm form, BigDecimal netSalary) {
            updatedId = id;
            updatedForm = form;
            updatedNet = netSalary;
            return 1;
        }

        @Override
        public int markPaid(long id, LocalDateTime paidAt) {
            paidId = id;
            PayrollServiceTest.this.paidAt = paidAt;
            return 1;
        }
    };

    private final PayrollService service = new PayrollService(repository, employees);

    @Test
    void calculatesNetSalary() {
        assertThat(service.calculateNet(SAMPLE)).isEqualByComparingTo("8500.00");
    }

    @Test
    void rejectsEditingPaidPayroll() {
        found = Optional.of(payroll(7L, PayrollStatus.PAID));

        BusinessException error = assertThrows(BusinessException.class,
                () -> service.updateDraft(7L, SAMPLE));

        assertThat(error.getMessage()).isEqualTo("已发放工资不可修改");
    }

    @Test
    void generatesDraftsOnlyForUnpaidActiveEmployees() {
        activeEmployees.add(employee(1L, "8000"));
        activeEmployees.add(employee(2L, "9000"));
        existingForMonth.add(2L);

        int count = service.generate("2027-01", 2L);

        assertThat(count).isEqualTo(1);
        assertThat(created).containsExactly(1L);
    }

    @Test
    void rejectsGenerateWhenEveryActiveEmployeeAlreadyHasPayroll() {
        activeEmployees.add(employee(1L, "8000"));
        existingForMonth.add(1L);

        BusinessException error = assertThrows(BusinessException.class,
                () -> service.generate("2027-01", 2L));

        assertThat(error.getMessage()).isEqualTo("该月份工资单已生成");
    }

    @Test
    void rejectsInvalidPayMonth() {
        BusinessException error = assertThrows(BusinessException.class,
                () -> service.generate("2027/01", 2L));

        assertThat(error.getMessage()).isEqualTo("工资月份格式应为YYYY-MM");
    }

    @Test
    void updatesDraftWithRecalculatedNet() {
        found = Optional.of(payroll(9L, PayrollStatus.DRAFT));

        service.updateDraft(9L, SAMPLE);

        assertThat(updatedId).isEqualTo(9L);
        assertThat(updatedNet).isEqualByComparingTo("8500.00");
    }

    @Test
    void marksDraftAsPaid() {
        found = Optional.of(payroll(11L, PayrollStatus.DRAFT));

        service.markPaid(11L);

        assertThat(paidId).isEqualTo(11L);
        assertThat(paidAt).isNotNull();
    }

    private Employee employee(long id, String baseSalary) {
        return new Employee(
                id, "E" + (1000 + id), "员工" + id, "男", LocalDate.of(1990, 1, 1),
                "1380000000" + id, "e" + id + "@example.com", "福州",
                LocalDate.of(2020, 1, 1), new BigDecimal(baseSalary),
                EmployeeStatus.ACTIVE, 1L, "技术部", 1L, "Java开发"
        );
    }

    private PayrollRecord payroll(long id, PayrollStatus status) {
        return new PayrollRecord(
                id, 1L, "E1001", "员工1", "2027-01",
                new BigDecimal("8000"), BigDecimal.ZERO, BigDecimal.ZERO,
                BigDecimal.ZERO, BigDecimal.ZERO, new BigDecimal("8000"),
                status, null, 2L, "人事专员"
        );
    }
}
