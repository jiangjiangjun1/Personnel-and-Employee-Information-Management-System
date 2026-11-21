package com.fzgs.personnel.controller;

import com.fzgs.personnel.common.BusinessException;
import com.fzgs.personnel.dto.LeaveForm;
import com.fzgs.personnel.model.AppUser;
import com.fzgs.personnel.model.Role;
import com.fzgs.personnel.repository.UserRepository;
import com.fzgs.personnel.service.LeaveService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

@Controller
@RequestMapping("/leave")
public class LeaveController {

    private final LeaveService leaveService;
    private final UserRepository userRepository;

    public LeaveController(LeaveService leaveService, UserRepository userRepository) {
        this.leaveService = leaveService;
        this.userRepository = userRepository;
    }

    @GetMapping
    public String list(Model model, Principal principal) {
        AppUser current = currentUser(principal);
        boolean approver = current.role() != Role.EMPLOYEE;
        if (approver) {
            model.addAttribute("requests", leaveService.findAll());
        } else if (current.employeeId() != null) {
            model.addAttribute("requests", leaveService.findForEmployee(current.employeeId()));
        } else {
            model.addAttribute("requests", java.util.List.of());
        }
        model.addAttribute("approver", approver);
        model.addAttribute("canSubmit", current.employeeId() != null);
        return "leave/list";
    }

    @GetMapping("/new")
    public String createForm(Model model, Principal principal, RedirectAttributes attrs) {
        AppUser current = currentUser(principal);
        if (current.employeeId() == null) {
            attrs.addFlashAttribute("error", "只有绑定员工的账号才能提交请假");
            return "redirect:/leave";
        }
        model.addAttribute("form", new LeaveForm("ANNUAL", LocalDate.now(), LocalDate.now(), ""));
        return "leave/form";
    }

    @PostMapping
    public String submit(
            @RequestParam String leaveType,
            @RequestParam String startDate,
            @RequestParam String endDate,
            @RequestParam String reason,
            Principal principal,
            RedirectAttributes attrs) {
        AppUser current = currentUser(principal);
        if (current.employeeId() == null) {
            attrs.addFlashAttribute("error", "只有绑定员工的账号才能提交请假");
            return "redirect:/leave";
        }
        try {
            LocalDate start = LocalDate.parse(startDate);
            LocalDate end = LocalDate.parse(endDate);
            leaveService.submit(current.employeeId(),
                    new LeaveForm(leaveType, start, end, reason));
        } catch (DateTimeParseException e) {
            return fail(leaveType, startDate, endDate, reason, "日期格式不正确", attrs);
        } catch (BusinessException e) {
            return fail(leaveType, startDate, endDate, reason, e.getMessage(), attrs);
        }
        return "redirect:/leave";
    }

    @PostMapping("/{id}/approval")
    public String approve(
            @PathVariable long id,
            @RequestParam boolean approved,
            @RequestParam(required = false) String comment,
            Principal principal,
            RedirectAttributes attrs) {
        AppUser current = currentUser(principal);
        try {
            leaveService.approve(id, current.id(), approved, comment);
        } catch (BusinessException e) {
            attrs.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/leave";
    }

    private String fail(String leaveType, String startDate, String endDate, String reason,
                        String error, RedirectAttributes attrs) {
        attrs.addFlashAttribute("error", error);
        attrs.addFlashAttribute("form",
                new LeaveForm(leaveType, null, null, reason));
        attrs.addFlashAttribute("startDate", startDate);
        attrs.addFlashAttribute("endDate", endDate);
        return "redirect:/leave/new";
    }

    private AppUser currentUser(Principal principal) {
        return userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new IllegalStateException("当前用户不存在"));
    }
}
