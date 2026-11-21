package com.fzgs.personnel.dto;

import com.fzgs.personnel.model.RecordStatus;

public record PositionForm(
    String code,
    String name,
    String level,
    String description,
    RecordStatus status
) {
}
