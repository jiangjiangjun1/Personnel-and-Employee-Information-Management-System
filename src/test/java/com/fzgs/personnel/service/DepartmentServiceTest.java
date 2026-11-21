package com.fzgs.personnel.service;

import com.fzgs.personnel.common.BusinessException;
import com.fzgs.personnel.dto.DepartmentForm;
import com.fzgs.personnel.model.Department;
import com.fzgs.personnel.model.RecordStatus;
import com.fzgs.personnel.repository.DepartmentRepository;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DepartmentServiceTest {

    DepartmentForm created;

    DepartmentRepository repository = new DepartmentRepository(null) {
        @Override
        public boolean existsByCode(String code, long excludedId) {
            return "HR".equals(code);
        }

        @Override
        public long create(DepartmentForm form) {
            created = form;
            return 1L;
        }

        @Override
        public Optional<Department> findById(long id) {
            return Optional.empty();
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

    @Test
    void normalizesCodeAndCreatesDepartment() {
        service.create(new DepartmentForm("  fin  ", "财务部", "赵主管", "13900000000",
                RecordStatus.ACTIVE));

        assertThat(created.code()).isEqualTo("FIN");
        assertThat(created.name()).isEqualTo("财务部");
    }

    @Test
    void rejectsBlankDepartmentCode() {
        BusinessException error = assertThrows(
                BusinessException.class,
                () -> service.create(new DepartmentForm("  ", "财务部", null, null,
                        RecordStatus.ACTIVE)));

        assertThat(error.getMessage()).isEqualTo("部门编码不能为空");
    }

    @Test
    void rejectsBlankDepartmentName() {
        BusinessException error = assertThrows(
                BusinessException.class,
                () -> service.create(new DepartmentForm("FIN", " ", null, null,
                        RecordStatus.ACTIVE)));

        assertThat(error.getMessage()).isEqualTo("部门名称不能为空");
    }

    @Test
    void updatesExistingDepartmentWithNormalizedCode() {
        boolean[] updated = {false};
        DepartmentRepository repository = new DepartmentRepository(null) {
            @Override
            public Optional<Department> findById(long id) {
                return Optional.of(new Department(id, "HR", "人力资源部", "王经理",
                        "13800000000", RecordStatus.ACTIVE));
            }

            @Override
            public boolean existsByCode(String code, long excludedId) {
                return false;
            }

            @Override
            public void update(long id, DepartmentForm form) {
                updated[0] = true;
                assertThat(form.code()).isEqualTo("HR");
                assertThat(form.name()).isEqualTo("人力资源部");
            }
        };

        new DepartmentService(repository).update(7L, new DepartmentForm(" hr ",
                "人力资源部", "王经理", "13800000000", RecordStatus.ACTIVE));

        assertThat(updated[0]).isTrue();
    }
}
