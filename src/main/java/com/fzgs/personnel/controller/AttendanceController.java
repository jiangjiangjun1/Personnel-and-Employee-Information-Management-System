package com.fzgs.personnel.controller;

import com.fzgs.personnel.common.BusinessException;
import com.fzgs.personnel.dto.AttendanceForm;
import com.fzgs.personnel.model.AttendanceStatus;
import com.fzgs.personnel.model.Employee;
import com.fzgs.personnel.model.EmployeeStatus;
import com.fzgs.personnel.service.AttendanceService;
import com.fzgs.personnel.service.EmployeeService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.List;

@Controller
@RequestMapping("/attendance")
public class AttendanceController {

    private final AttendanceService service;
    private final EmployeeService employeeService;

    public AttendanceController(AttendanceService service, EmployeeService employeeService) {
        this.service = service;
        this.employeeService = employeeService;
    }

    @GetMapping
    public String list(
            @RequestParam(required = false) Long employeeId,
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to,
            @RequestParam(defaultValue = "1") int page,
            Model model) {
        LocalDate fromDate = parseDate(from);
        LocalDate toDate = parseDate(to);
        if ((from != null && !from.isBlank() && fromDate == null)
                || (to != null && !to.isBlank() && toDate == null)) {
            model.addAttribute("error", "日期格式不正确");
        }
        model.addAttribute("result", service.findPage(employeeId, fromDate, toDate, page, 10));
        model.addAttribute("employees", activeEmployees());
        model.addAttribute("employeeId", employeeId);
        model.addAttribute("from", from == null ? "" : from);
        model.addAttribute("to", to == null ? "" : to);
        return "attendance/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("employees", activeEmployees());
        model.addAttribute("form", new AttendanceForm(null, LocalDate.now(),
                LocalTime.of(9, 0), LocalTime.of(18, 0), AttendanceStatus.NORMAL, ""));
        return "attendance/form";
    }

    @PostMapping
    public String save(
            @RequestParam Long employeeId,
            @RequestParam String workDate,
            @RequestParam(required = false) String checkIn,
            @RequestParam(required = false) String checkOut,
            @RequestParam AttendanceStatus status,
            @RequestParam(required = false) String remark,
            RedirectAttributes attrs) {
        Parsed parsed = parse(employeeId, workDate, checkIn, checkOut, status, remark);
        if (parsed.error != null) {
            attrs.addFlashAttribute("error", parsed.error);
            attrs.addFlashAttribute("form", new AttendanceForm(employeeId, null, null, null,
                    status, remark));
            attrs.addFlashAttribute("workDate", workDate);
            attrs.addFlashAttribute("checkIn", checkIn);
            attrs.addFlashAttribute("checkOut", checkOut);
            return "redirect:/attendance/new";
        }
        try {
            service.save(parsed.form);
        } catch (BusinessException e) {
            attrs.addFlashAttribute("error", e.getMessage());
            attrs.addFlashAttribute("form", parsed.form);
            return "redirect:/attendance/new";
        }
        return "redirect:/attendance?employeeId=" + employeeId;
    }

    private List<Employee> activeEmployees() {
        return employeeService.findPage(null, null, EmployeeStatus.ACTIVE, 1, 10000).items();
    }

    private Parsed parse(Long employeeId, String workDate, String checkIn, String checkOut,
                         AttendanceStatus status, String remark) {
        try {
            LocalDate parsedDate = LocalDate.parse(workDate);
            LocalTime parsedCheckIn = blank(checkIn) ? null : LocalTime.parse(checkIn);
            LocalTime parsedCheckOut = blank(checkOut) ? null : LocalTime.parse(checkOut);
            return new Parsed(null, new AttendanceForm(employeeId, parsedDate, parsedCheckIn,
                    parsedCheckOut, status, remark));
        } catch (DateTimeParseException e) {
            return new Parsed("日期或时间格式不正确", null);
        }
    }

    private LocalDate parseDate(String value) {
        if (blank(value)) {
            return null;
        }
        try {
            return LocalDate.parse(value);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    private boolean blank(String value) {
        return value == null || value.isBlank();
    }

    private record Parsed(String error, AttendanceForm form) {
    }
}
