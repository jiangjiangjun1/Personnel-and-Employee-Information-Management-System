package com.fzgs.personnel.controller;

import com.fzgs.personnel.common.BusinessException;
import com.fzgs.personnel.dto.DepartmentForm;
import com.fzgs.personnel.model.RecordStatus;
import com.fzgs.personnel.service.DepartmentService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/departments")
public class DepartmentController {

    private final DepartmentService service;

    public DepartmentController(DepartmentService service) {
        this.service = service;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("departments", service.findAll());
        return "department/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("form", new DepartmentForm("", "", "", "", RecordStatus.ACTIVE));
        return "department/form";
    }

    @PostMapping
    public String create(
            @RequestParam String code,
            @RequestParam String name,
            @RequestParam(required = false) String manager,
            @RequestParam(required = false) String phone,
            @RequestParam RecordStatus status,
            RedirectAttributes attrs) {
        DepartmentForm form = new DepartmentForm(code, name, manager, phone, status);
        try {
            service.create(form);
        } catch (BusinessException e) {
            return fail("departments/new", form, e, attrs);
        }
        return "redirect:/departments";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable long id, Model model) {
        return service.findById(id)
                .map(department -> {
                    model.addAttribute("id", id);
                    model.addAttribute("form", department);
                    return "department/form";
                })
                .orElse("redirect:/departments");
    }

    @PostMapping("/{id}")
    public String update(
            @PathVariable long id,
            @RequestParam String code,
            @RequestParam String name,
            @RequestParam(required = false) String manager,
            @RequestParam(required = false) String phone,
            @RequestParam RecordStatus status,
            RedirectAttributes attrs) {
        DepartmentForm form = new DepartmentForm(code, name, manager, phone, status);
        try {
            service.update(id, form);
        } catch (BusinessException e) {
            return fail("departments/" + id + "/edit", form, e, attrs);
        }
        return "redirect:/departments";
    }

    @PostMapping("/{id}/status")
    public String updateStatus(@PathVariable long id, @RequestParam RecordStatus status,
                               RedirectAttributes attrs) {
        try {
            service.updateStatus(id, status);
        } catch (BusinessException e) {
            attrs.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/departments";
    }

    private String fail(String path, DepartmentForm form, BusinessException e, RedirectAttributes attrs) {
        attrs.addFlashAttribute("error", e.getMessage());
        attrs.addFlashAttribute("form", form);
        return "redirect:/" + path;
    }
}
