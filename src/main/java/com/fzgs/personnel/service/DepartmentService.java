package com.fzgs.personnel.service;

import com.fzgs.personnel.common.BusinessException;
import com.fzgs.personnel.dto.DepartmentForm;
import com.fzgs.personnel.model.Department;
import com.fzgs.personnel.model.RecordStatus;
import com.fzgs.personnel.repository.DepartmentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class DepartmentService {

    private final DepartmentRepository repository;

    public DepartmentService(DepartmentRepository repository) {
        this.repository = repository;
    }

    public List<Department> findAll() {
        return repository.findAll();
    }

    public Optional<Department> findById(long id) {
        return repository.findById(id);
    }

    @Transactional
    public void create(DepartmentForm form) {
        DepartmentForm normalized = normalize(form);
        if (repository.existsByCode(normalized.code(), 0L)) {
            throw new BusinessException("部门编码已存在");
        }
        repository.create(normalized);
    }

    @Transactional
    public void update(long id, DepartmentForm form) {
        if (repository.findById(id).isEmpty()) {
            throw new BusinessException("部门不存在");
        }
        DepartmentForm normalized = normalize(form);
        if (repository.existsByCode(normalized.code(), id)) {
            throw new BusinessException("部门编码已存在");
        }
        repository.update(id, normalized);
    }

    @Transactional
    public void updateStatus(long id, RecordStatus status) {
        if (repository.findById(id).isEmpty()) {
            throw new BusinessException("部门不存在");
        }
        repository.updateStatus(id, status);
    }

    private DepartmentForm normalize(DepartmentForm form) {
        return new DepartmentForm(
                trim(form.code()).toUpperCase(),
                trim(form.name()),
                trim(form.manager()),
                trim(form.phone()),
                form.status()
        );
    }

    private String trim(String value) {
        return value == null ? "" : value.trim();
    }
}
