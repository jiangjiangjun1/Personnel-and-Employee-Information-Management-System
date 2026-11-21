package com.fzgs.personnel.service;

import com.fzgs.personnel.common.BusinessException;
import com.fzgs.personnel.dto.PositionForm;
import com.fzgs.personnel.model.Position;
import com.fzgs.personnel.model.RecordStatus;
import com.fzgs.personnel.repository.PositionRepository;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PositionServiceTest {

    PositionForm created;

    PositionRepository repository = new PositionRepository(null) {
        @Override
        public boolean existsByCode(String code, long excludedId) {
            return "DEV".equals(code);
        }

        @Override
        public long create(PositionForm form) {
            created = form;
            return 1L;
        }

        @Override
        public Optional<Position> findById(long id) {
            return Optional.empty();
        }
    };
    PositionService service = new PositionService(repository);

    @Test
    void rejectsDuplicatePositionCode() {
        BusinessException error = assertThrows(
                BusinessException.class,
                () -> service.create(new PositionForm("DEV", "开发工程师", "P5",
                        "负责业务系统开发", RecordStatus.ACTIVE)));

        assertThat(error.getMessage()).isEqualTo("岗位编码已存在");
    }

    @Test
    void normalizesCodeAndCreatesPosition() {
        service.create(new PositionForm("  ops  ", "运维工程师", "P4", "维护服务器",
                RecordStatus.ACTIVE));

        assertThat(created.code()).isEqualTo("OPS");
        assertThat(created.name()).isEqualTo("运维工程师");
    }

    @Test
    void rejectsBlankPositionCode() {
        BusinessException error = assertThrows(
                BusinessException.class,
                () -> service.create(new PositionForm("  ", "运维工程师", "P4", null,
                        RecordStatus.ACTIVE)));

        assertThat(error.getMessage()).isEqualTo("岗位编码不能为空");
    }

    @Test
    void rejectsBlankPositionName() {
        BusinessException error = assertThrows(
                BusinessException.class,
                () -> service.create(new PositionForm("OPS", " ", "P4", null,
                        RecordStatus.ACTIVE)));

        assertThat(error.getMessage()).isEqualTo("岗位名称不能为空");
    }

    @Test
    void updatesExistingPositionWithNormalizedCode() {
        boolean[] updated = {false};
        PositionRepository repository = new PositionRepository(null) {
            @Override
            public Optional<Position> findById(long id) {
                return Optional.of(new Position(id, "DEV", "开发工程师", "P5",
                        "负责业务系统开发", RecordStatus.ACTIVE));
            }

            @Override
            public boolean existsByCode(String code, long excludedId) {
                return false;
            }

            @Override
            public void update(long id, PositionForm form) {
                updated[0] = true;
                assertThat(form.code()).isEqualTo("DEV");
                assertThat(form.name()).isEqualTo("开发工程师");
            }
        };

        new PositionService(repository).update(7L, new PositionForm(" dev ",
                "开发工程师", "P5", "负责业务系统开发", RecordStatus.ACTIVE));

        assertThat(updated[0]).isTrue();
    }
}
