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
    public long getId() { return id; }
    public String getUsername() { return username; }
    public String getPasswordHash() { return passwordHash; }
    public String getDisplayName() { return displayName; }
    public Role getRole() { return role; }
    public boolean isEnabled() { return enabled; }
    public Long getEmployeeId() { return employeeId; }
}
