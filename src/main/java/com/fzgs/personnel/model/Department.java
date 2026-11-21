package com.fzgs.personnel.model;

public record Department(
    Long id,
    String code,
    String name,
    String manager,
    String phone,
    RecordStatus status
) {
    public Long getId() { return id; }
    public String getCode() { return code; }
    public String getName() { return name; }
    public String getManager() { return manager; }
    public String getPhone() { return phone; }
    public RecordStatus getStatus() { return status; }
}
