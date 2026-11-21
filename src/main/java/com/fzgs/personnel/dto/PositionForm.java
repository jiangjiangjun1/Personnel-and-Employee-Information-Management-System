package com.fzgs.personnel.dto;

import com.fzgs.personnel.model.RecordStatus;

public record PositionForm(
    String code,
    String name,
    String level,
    String description,
    RecordStatus status
) {
    public String getCode() { return code; }
    public String getName() { return name; }
    public String getLevel() { return level; }
    public String getDescription() { return description; }
    public RecordStatus getStatus() { return status; }
}
