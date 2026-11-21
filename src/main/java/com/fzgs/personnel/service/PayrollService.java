package com.fzgs.personnel.service;

import com.fzgs.personnel.common.BusinessException;
import com.fzgs.personnel.dto.PayrollForm;
import com.fzgs.personnel.model.Employee;
import com.fzgs.personnel.model.PayrollRecord;
import com.fzgs.personnel.model.PayrollStatus;
import com.fzgs.personnel.repository.EmployeeRepository;
import com.fzgs.personnel.repository.PayrollRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;

@Service
public class PayrollService {

    private static final Pattern PAY_MONTH = Pattern.compile("\\d{4}-(0[1-9]|1[0-2])");

    private final PayrollRepository payrollRepository;
    private final EmployeeRepository employeeRepository;

    public PayrollService(PayrollRepository payrollRepository,
                          EmployeeRepository employeeRepository) {
        this.payrollRepository = payrollRepository;
        this.employeeRepository = employeeRepository;
    }

    public List<PayrollRecord> findByMonth(String payMonth) {
        return payrollRepository.findByMonth(payMonth);
    }

    public Optional<PayrollRecord> findById(long id) {
        return payrollRepository.findById(id);
    }

    @Transactional
    public int generate(String payMonth, long createdBy) {
        validatePayMonth(payMonth);
        List<Employee> activeEmployees = employeeRepository.findActiveEmployees();
        Set<Long> existing = payrollRepository.findEmployeeIdsForMonth(payMonth);
        int created = 0;
        for (Employee employee : activeEmployees) {
            if (existing.contains(employee.id())) {
                continue;
            }
            PayrollForm form = new PayrollForm(
                    employee.baseSalary(), BigDecimal.ZERO, BigDecimal.ZERO,
                    BigDecimal.ZERO, BigDecimal.ZERO);
            payrollRepository.createDraft(employee.id(), payMonth, form,
                    calculateNet(form), createdBy);
            created++;
        }
        if (created == 0) {
            throw new BusinessException("该月份工资单已生成");
        }
        return created;
    }

    @Transactional
    public void updateDraft(long id, PayrollForm form) {
        validateAmounts(form);
        PayrollRecord record = payrollRepository.findById(id)
                .orElseThrow(() -> new BusinessException("工资单不存在"));
        if (record.status() != PayrollStatus.DRAFT) {
            throw new BusinessException("已发放工资不可修改");
        }
        int updated = payrollRepository.updateDraft(id, form, calculateNet(form));
        if (updated == 0) {
            throw new BusinessException("工资单不存在或已发放");
        }
    }

    @Transactional
    public void markPaid(long id) {
        PayrollRecord record = payrollRepository.findById(id)
                .orElseThrow(() -> new BusinessException("工资单不存在"));
        if (record.status() == PayrollStatus.PAID) {
            throw new BusinessException("工资单已发放");
        }
        int updated = payrollRepository.markPaid(id, LocalDateTime.now());
        if (updated == 0) {
            throw new BusinessException("工资单不存在或已发放");
        }
    }

    public BigDecimal calculateNet(PayrollForm form) {
        return form.baseSalary()
                .add(form.bonus())
                .add(form.allowance())
                .subtract(form.deduction())
                .subtract(form.socialSecurity());
    }

    private void validatePayMonth(String payMonth) {
        if (payMonth == null || !PAY_MONTH.matcher(payMonth.trim()).matches()) {
            throw new BusinessException("工资月份格式应为YYYY-MM");
        }
    }

    private void validateAmounts(PayrollForm form) {
        if (form == null || form.baseSalary() == null || form.bonus() == null
                || form.allowance() == null || form.deduction() == null
                || form.socialSecurity() == null) {
            throw new BusinessException("工资项目不能为空");
        }
        if (form.baseSalary().compareTo(BigDecimal.ZERO) < 0
                || form.bonus().compareTo(BigDecimal.ZERO) < 0
                || form.allowance().compareTo(BigDecimal.ZERO) < 0
                || form.deduction().compareTo(BigDecimal.ZERO) < 0
                || form.socialSecurity().compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException("工资项目不能为负数");
        }
    }
}
