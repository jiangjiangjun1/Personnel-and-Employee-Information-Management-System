package com.fzgs.personnel.dto;

import com.fzgs.personnel.model.RecordStatus;

public record DepartmentForm(
    String code,
    String name,
    String manager,
    String phone,
    RecordStatus status
) {
    public String getCode() { return code; }
    public String getName() { return name; }
    public String getManager() { return manager; }
    public String getPhone() { return phone; }
    public RecordStatus getStatus() { return status; }
}
