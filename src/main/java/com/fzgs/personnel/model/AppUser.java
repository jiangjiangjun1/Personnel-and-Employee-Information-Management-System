package com.fzgs.personnel.model;

public record AppUser(
    long id,
    String username,
    String passwordHash,
    String displayName,
    Role role,
    boolean enabled,
    Long employeeId
) {
}
