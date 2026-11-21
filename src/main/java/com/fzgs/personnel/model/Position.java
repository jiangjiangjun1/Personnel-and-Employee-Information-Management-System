package com.fzgs.personnel.model;

public record Position(
    Long id,
    String code,
    String name,
    String level,
    String description,
    RecordStatus status
) {
}
