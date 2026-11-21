package com.fzgs.personnel.service;

import com.fzgs.personnel.common.BusinessException;
import com.fzgs.personnel.dto.PositionForm;
import com.fzgs.personnel.model.RecordStatus;
import com.fzgs.personnel.repository.PositionRepository;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PositionServiceTest {

    PositionRepository repository = new PositionRepository(null) {
        @Override
        public boolean existsByCode(String code, long excludedId) {
            return "DEV".equals(code);
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
}
