package com.fzgs.personnel.model;

public record Position(
    Long id,
    String code,
    String name,
    String level,
    String description,
    RecordStatus status
) {
    public Long getId() { return id; }
    public String getCode() { return code; }
    public String getName() { return name; }
    public String getLevel() { return level; }
    public String getDescription() { return description; }
    public RecordStatus getStatus() { return status; }
}
