package com.fzgs.personnel.controller;

import com.fzgs.personnel.common.BusinessException;
import com.fzgs.personnel.dto.EmployeeForm;
import com.fzgs.personnel.model.EmployeeStatus;
import com.fzgs.personnel.service.EmployeeService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

@Controller
@RequestMapping("/employees")
public class EmployeeController {

    private final EmployeeService service;

    public EmployeeController(EmployeeService service) {
        this.service = service;
    }

    @GetMapping
    public String list(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long departmentId,
            @RequestParam(required = false) EmployeeStatus status,
            @RequestParam(defaultValue = "1") int page,
            Model model) {
        model.addAttribute("result", service.findPage(keyword, departmentId, status, page, 10));
        model.addAttribute("keyword", keyword == null ? "" : keyword);
        model.addAttribute("departmentId", departmentId);
        model.addAttribute("status", status);
        model.addAttribute("departments", service.findDepartments());
        model.addAttribute("statuses", EmployeeStatus.values());
        return "employee/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        addReferenceData(model);
        model.addAttribute("form", new EmployeeForm(
                "", "", "", null, "", "", "", LocalDate.now(),
                BigDecimal.ZERO, null, null, EmployeeStatus.ACTIVE));
        return "employee/form";
    }

    @PostMapping
    public String create(
            @RequestParam String employeeNo,
            @RequestParam String name,
            @RequestParam String gender,
            @RequestParam(required = false) String birthDate,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String address,
            @RequestParam String hireDate,
            @RequestParam String baseSalary,
            @RequestParam Long departmentId,
            @RequestParam Long positionId,
            @RequestParam EmployeeStatus status,
            RedirectAttributes attrs) {
        Parsed parsed = parseForm(employeeNo, name, gender, birthDate, phone, email, address,
                hireDate, baseSalary, departmentId, positionId, status);
        if (parsed.error != null) {
            attrs.addFlashAttribute("error", parsed.error);
            attrs.addFlashAttribute("form", parsed.form);
            return "redirect:/employees/new";
        }
        try {
            service.create(parsed.form);
        } catch (BusinessException e) {
            return fail("employees/new", parsed.form, e, attrs);
        }
        return "redirect:/employees";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable long id, Model model) {
        return service.findById(id)
                .map(employee -> {
                    addReferenceData(model);
                    model.addAttribute("id", id);
                    model.addAttribute("form", EmployeeForm.from(employee));
                    return "employee/form";
                })
                .orElse("redirect:/employees");
    }

    @PostMapping("/{id}")
    public String update(
            @PathVariable long id,
            @RequestParam String employeeNo,
            @RequestParam String name,
            @RequestParam String gender,
            @RequestParam(required = false) String birthDate,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String address,
            @RequestParam String hireDate,
            @RequestParam String baseSalary,
            @RequestParam Long departmentId,
            @RequestParam Long positionId,
            @RequestParam EmployeeStatus status,
            RedirectAttributes attrs) {
        Parsed parsed = parseForm(employeeNo, name, gender, birthDate, phone, email, address,
                hireDate, baseSalary, departmentId, positionId, status);
        if (parsed.error != null) {
            attrs.addFlashAttribute("error", parsed.error);
            attrs.addFlashAttribute("form", parsed.form);
            return "redirect:/employees/" + id + "/edit";
        }
        try {
            service.update(id, parsed.form);
        } catch (BusinessException e) {
            return fail("employees/" + id + "/edit", parsed.form, e, attrs);
        }
        return "redirect:/employees";
    }

    @PostMapping("/{id}/status")
    public String updateStatus(@PathVariable long id, @RequestParam EmployeeStatus status,
                               RedirectAttributes attrs) {
        try {
            service.updateStatus(id, status);
        } catch (BusinessException e) {
            attrs.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/employees";
    }

    private Parsed parseForm(String employeeNo, String name, String gender, String birthDate,
                             String phone, String email, String address, String hireDate,
                             String baseSalary, Long departmentId, Long positionId,
                             EmployeeStatus status) {
        try {
            LocalDate parsedBirth = blank(birthDate) ? null : LocalDate.parse(birthDate);
            LocalDate parsedHire = blank(hireDate) ? null : LocalDate.parse(hireDate);
            BigDecimal parsedSalary = new BigDecimal(baseSalary);
            return new Parsed(null, new EmployeeForm(
                    employeeNo, name, gender, parsedBirth, phone, email, address,
                    parsedHire, parsedSalary, departmentId, positionId, status));
        } catch (DateTimeParseException e) {
            return new Parsed("日期格式不正确", fallbackForm(employeeNo, name, gender, phone,
                    email, address, null, null, departmentId, positionId, status));
        } catch (NumberFormatException e) {
            return new Parsed("基本工资必须是数字", fallbackForm(employeeNo, name, gender, phone,
                    email, address, null, null, departmentId, positionId, status));
        }
    }

    private EmployeeForm fallbackForm(String employeeNo, String name, String gender,
                                      String phone, String email, String address, LocalDate hireDate,
                                      BigDecimal baseSalary, Long departmentId, Long positionId,
                                      EmployeeStatus status) {
        return new EmployeeForm(
                employeeNo, name, gender, null, phone, email, address, hireDate,
                baseSalary,
                departmentId, positionId, status);
    }

    private void addReferenceData(Model model) {
        model.addAttribute("departments", service.findDepartments());
        model.addAttribute("positions", service.findPositions());
    }

    private String fail(String path, EmployeeForm form, BusinessException e,
                        RedirectAttributes attrs) {
        attrs.addFlashAttribute("error", e.getMessage());
        attrs.addFlashAttribute("form", form);
        return "redirect:/" + path;
    }

    private boolean blank(String value) {
        return value == null || value.isBlank();
    }

    private record Parsed(String error, EmployeeForm form) {
    }
}
