package com.fzgs.personnel.config;

import com.fzgs.personnel.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.security.Principal;

@ControllerAdvice
public class GlobalModelAttributes {

    private final UserRepository users;

    public GlobalModelAttributes(UserRepository users) {
        this.users = users;
    }

    @ModelAttribute
    public void addCurrentUser(Model model, Principal principal, HttpServletRequest request) {
        model.addAttribute("currentPath", request.getRequestURI());
        if (principal != null) {
            users.findByUsername(principal.getName())
                    .ifPresent(user -> model.addAttribute("appUser", user));
        }
    }
}
