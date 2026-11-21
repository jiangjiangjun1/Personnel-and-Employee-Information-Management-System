package com.fzgs.personnel.controller;

import com.fzgs.personnel.common.BusinessException;
import com.fzgs.personnel.dto.PositionForm;
import com.fzgs.personnel.model.RecordStatus;
import com.fzgs.personnel.service.PositionService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/positions")
public class PositionController {

    private final PositionService service;

    public PositionController(PositionService service) {
        this.service = service;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("positions", service.findAll());
        return "position/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("form", new PositionForm("", "", "", "", RecordStatus.ACTIVE));
        return "position/form";
    }

    @PostMapping
    public String create(
            @RequestParam String code,
            @RequestParam String name,
            @RequestParam(required = false) String level,
            @RequestParam(required = false) String description,
            @RequestParam RecordStatus status,
            RedirectAttributes attrs) {
        PositionForm form = new PositionForm(code, name, level, description, status);
        try {
            service.create(form);
        } catch (BusinessException e) {
            return fail("position/new", form, e, attrs);
        }
        return "redirect:/positions";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable long id, Model model) {
        return service.findById(id)
                .map(position -> {
                    model.addAttribute("id", id);
                    model.addAttribute("form", position);
                    return "position/form";
                })
                .orElse("redirect:/positions");
    }

    @PostMapping("/{id}")
    public String update(
            @PathVariable long id,
            @RequestParam String code,
            @RequestParam String name,
            @RequestParam(required = false) String level,
            @RequestParam(required = false) String description,
            @RequestParam RecordStatus status,
            RedirectAttributes attrs) {
        PositionForm form = new PositionForm(code, name, level, description, status);
        try {
            service.update(id, form);
        } catch (BusinessException e) {
            return fail("position/" + id + "/edit", form, e, attrs);
        }
        return "redirect:/positions";
    }

    @PostMapping("/{id}/status")
    public String updateStatus(@PathVariable long id, @RequestParam RecordStatus status) {
        try {
            service.updateStatus(id, status);
        } catch (BusinessException ignored) {
            return "redirect:/positions";
        }
        return "redirect:/positions";
    }

    private String fail(String path, PositionForm form, BusinessException e, RedirectAttributes attrs) {
        attrs.addFlashAttribute("error", e.getMessage());
        attrs.addFlashAttribute("form", form);
        return "redirect:/" + path;
    }
}
