package com.fzgs.personnel.dto;

import com.fzgs.personnel.model.AppUser;
import com.fzgs.personnel.model.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UserForm(
        @NotBlank @Size(max = 60) String username,
        @NotBlank @Size(max = 60) String displayName,
        @NotNull Role role,
        boolean enabled,
        Long employeeId,
        String password
) {
    public String getUsername() { return username; }
    public String getDisplayName() { return displayName; }
    public Role getRole() { return role; }
    public boolean isEnabled() { return enabled; }
    public Long getEmployeeId() { return employeeId; }
    public String getPassword() { return password; }

    public static UserForm from(AppUser user) {
        return new UserForm(
                user.username(),
                user.displayName(),
                user.role(),
                user.enabled(),
                user.employeeId(),
                ""
        );
    }
}
