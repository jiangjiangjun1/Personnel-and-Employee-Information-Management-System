package com.fzgs.personnel.controller;

import com.fzgs.personnel.common.BusinessException;
import com.fzgs.personnel.dto.UserForm;
import com.fzgs.personnel.model.AppUser;
import com.fzgs.personnel.model.Role;
import com.fzgs.personnel.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

@Controller
@RequestMapping("/users")
public class UserController {

    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("users", service.findAll());
        model.addAttribute("employeeNames", service.findEmployeeNames());
        model.addAttribute("roles", Role.values());
        return "user/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        addReferenceData(model);
        model.addAttribute("form", new UserForm("", "", Role.HR, true, null, ""));
        return "user/form";
    }

    @PostMapping
    public String create(
            @RequestParam String username,
            @RequestParam String displayName,
            @RequestParam Role role,
            @RequestParam boolean enabled,
            @RequestParam(required = false) Long employeeId,
            @RequestParam String password,
            RedirectAttributes attrs) {
        UserForm form = new UserForm(username, displayName, role, enabled, employeeId, password);
        try {
            service.create(form);
        } catch (BusinessException e) {
            return fail("users/new", form, e, attrs);
        }
        return "redirect:/users";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable long id, Model model) {
        return service.findById(id)
                .map(user -> {
                    addReferenceData(model);
                    model.addAttribute("id", id);
                    model.addAttribute("form", UserForm.from(user));
                    return "user/form";
                })
                .orElse("redirect:/users");
    }

    @PostMapping("/{id}")
    public String update(
            @PathVariable long id,
            @RequestParam String username,
            @RequestParam String displayName,
            @RequestParam Role role,
            @RequestParam boolean enabled,
            @RequestParam(required = false) Long employeeId,
            @RequestParam(required = false) String password,
            RedirectAttributes attrs) {
        UserForm form = new UserForm(username, displayName, role, enabled, employeeId,
                password == null ? "" : password);
        try {
            service.update(id, form);
        } catch (BusinessException e) {
            return fail("users/" + id + "/edit", form, e, attrs);
        }
        return "redirect:/users";
    }

    @PostMapping("/{id}/status")
    public String updateStatus(@PathVariable long id, @RequestParam boolean enabled,
                               Principal principal, RedirectAttributes attrs) {
        if (!enabled) {
            AppUser current = service.findByUsername(principal.getName()).orElse(null);
            if (current != null && current.id() == id) {
                attrs.addFlashAttribute("error", "不能停用当前登录账号");
                return "redirect:/users";
            }
        }
        try {
            service.setEnabled(id, enabled);
        } catch (BusinessException e) {
            attrs.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/users";
    }

    private void addReferenceData(Model model) {
        model.addAttribute("employees", service.findActiveEmployees());
        model.addAttribute("roles", Role.values());
    }

    private String fail(String path, UserForm form, BusinessException e,
                        RedirectAttributes attrs) {
        attrs.addFlashAttribute("error", e.getMessage());
        attrs.addFlashAttribute("form", form);
        return "redirect:/" + path;
    }
}
