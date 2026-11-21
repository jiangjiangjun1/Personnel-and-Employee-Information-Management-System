package com.fzgs.personnel.service;

import com.fzgs.personnel.common.BusinessException;
import com.fzgs.personnel.dto.DepartmentForm;
import com.fzgs.personnel.model.RecordStatus;
import com.fzgs.personnel.repository.DepartmentRepository;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DepartmentServiceTest {

    DepartmentRepository repository = new DepartmentRepository(null) {
        @Override
        public boolean existsByCode(String code, long excludedId) {
            return "HR".equals(code);
        }
    };
    DepartmentService service = new DepartmentService(repository);

    @Test
    void rejectsDuplicateDepartmentCode() {
        BusinessException error = assertThrows(
                BusinessException.class,
                () -> service.create(new DepartmentForm("HR", "人力资源部", "王经理",
                        "13800000000", RecordStatus.ACTIVE)));

        assertThat(error.getMessage()).isEqualTo("部门编码已存在");
    }
}
