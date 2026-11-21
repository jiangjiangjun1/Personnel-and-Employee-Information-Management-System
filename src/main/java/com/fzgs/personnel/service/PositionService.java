package com.fzgs.personnel.service;

import com.fzgs.personnel.common.BusinessException;
import com.fzgs.personnel.dto.PositionForm;
import com.fzgs.personnel.model.Position;
import com.fzgs.personnel.model.RecordStatus;
import com.fzgs.personnel.repository.PositionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class PositionService {

    private final PositionRepository repository;

    public PositionService(PositionRepository repository) {
        this.repository = repository;
    }

    public List<Position> findAll() {
        return repository.findAll();
    }

    public Optional<Position> findById(long id) {
        return repository.findById(id);
    }

    @Transactional
    public void create(PositionForm form) {
        PositionForm normalized = normalize(form);
        validateRequired(normalized);
        if (repository.existsByCode(normalized.code(), 0L)) {
            throw new BusinessException("岗位编码已存在");
        }
        repository.create(normalized);
    }

    @Transactional
    public void update(long id, PositionForm form) {
        if (repository.findById(id).isEmpty()) {
            throw new BusinessException("岗位不存在");
        }
        PositionForm normalized = normalize(form);
        validateRequired(normalized);
        if (repository.existsByCode(normalized.code(), id)) {
            throw new BusinessException("岗位编码已存在");
        }
        repository.update(id, normalized);
    }

    @Transactional
    public void updateStatus(long id, RecordStatus status) {
        if (repository.findById(id).isEmpty()) {
            throw new BusinessException("岗位不存在");
        }
        repository.updateStatus(id, status);
    }

    private PositionForm normalize(PositionForm form) {
        return new PositionForm(
                trim(form.code()).toUpperCase(),
                trim(form.name()),
                trim(form.level()),
                trim(form.description()),
                form.status()
        );
    }

    private String trim(String value) {
        return value == null ? "" : value.trim();
    }

    private void validateRequired(PositionForm form) {
        if (form.code().isBlank()) {
            throw new BusinessException("岗位编码不能为空");
        }
        if (form.name().isBlank()) {
            throw new BusinessException("岗位名称不能为空");
        }
    }
}
