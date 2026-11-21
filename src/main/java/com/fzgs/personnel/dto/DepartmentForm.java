package com.fzgs.personnel.dto;

import com.fzgs.personnel.model.RecordStatus;

public record DepartmentForm(
    String code,
    String name,
    String manager,
    String phone,
    RecordStatus status
) {
}
