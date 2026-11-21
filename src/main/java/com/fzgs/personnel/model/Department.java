package com.fzgs.personnel.model;

public record Department(
    Long id,
    String code,
    String name,
    String manager,
    String phone,
    RecordStatus status
) {
}
