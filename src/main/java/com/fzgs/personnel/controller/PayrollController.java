package com.fzgs.personnel.controller;

import com.fzgs.personnel.common.BusinessException;
import com.fzgs.personnel.dto.PayrollForm;
import com.fzgs.personnel.model.AppUser;
import com.fzgs.personnel.model.PayrollRecord;
import com.fzgs.personnel.repository.UserRepository;
import com.fzgs.personnel.service.PayrollService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.security.Principal;
import java.time.YearMonth;

@Controller
@RequestMapping("/payroll")
public class PayrollController {

    private final PayrollService payrollService;
    private final UserRepository userRepository;

    public PayrollController(PayrollService payrollService, UserRepository userRepository) {
        this.payrollService = payrollService;
        this.userRepository = userRepository;
    }

    @GetMapping
    public String list(@RequestParam(required = false) String payMonth, Model model) {
        String month = payMonth == null || payMonth.isBlank()
                ? YearMonth.now().toString() : payMonth;
        model.addAttribute("payMonth", month);
        model.addAttribute("records", payrollService.findByMonth(month));
        return "payroll/list";
    }

    @PostMapping("/generate")
    public String generate(@RequestParam String payMonth, Principal principal,
                           RedirectAttributes attrs) {
        AppUser current = currentUser(principal);
        try {
            int count = payrollService.generate(payMonth, current.id());
            attrs.addFlashAttribute("message", "已生成" + count + "张工资单");
        } catch (BusinessException e) {
            attrs.addFlashAttribute("error", e.getMessage());
        }
        attrs.addAttribute("payMonth", payMonth);
        return "redirect:/payroll";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable long id, Model model, RedirectAttributes attrs) {
        PayrollRecord record = payrollService.findById(id)
                .orElse(null);
        if (record == null) {
            attrs.addFlashAttribute("error", "工资单不存在");
            return "redirect:/payroll";
        }
        model.addAttribute("record", record);
        model.addAttribute("form", new PayrollForm(
                record.baseSalary(), record.bonus(), record.allowance(),
                record.deduction(), record.socialSecurity()));
        return "payroll/form";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable long id,
                         @RequestParam String baseSalary,
                         @RequestParam String bonus,
                         @RequestParam String allowance,
                         @RequestParam String deduction,
                         @RequestParam String socialSecurity,
                         RedirectAttributes attrs) {
        try {
            PayrollForm form = new PayrollForm(
                    decimal(baseSalary), decimal(bonus), decimal(allowance),
                    decimal(deduction), decimal(socialSecurity));
            payrollService.updateDraft(id, form);
            attrs.addFlashAttribute("message", "工资单已更新");
            attrs.addAttribute("payMonth", currentMonthOf(id));
            return "redirect:/payroll";
        } catch (NumberFormatException e) {
            attrs.addFlashAttribute("error", "金额格式不正确");
        } catch (BusinessException e) {
            attrs.addFlashAttribute("error", e.getMessage());
        }
        flashAmounts(attrs, baseSalary, bonus, allowance, deduction, socialSecurity);
        return "redirect:/payroll/" + id + "/edit";
    }

    @PostMapping("/{id}/paid")
    public String markPaid(@PathVariable long id, RedirectAttributes attrs) {
        try {
            payrollService.markPaid(id);
            attrs.addFlashAttribute("message", "工资单已发放");
        } catch (BusinessException e) {
            attrs.addFlashAttribute("error", e.getMessage());
        }
        attrs.addAttribute("payMonth", currentMonthOf(id));
        return "redirect:/payroll";
    }

    private String currentMonthOf(long id) {
        return payrollService.findById(id)
                .map(PayrollRecord::payMonth)
                .orElse(YearMonth.now().toString());
    }

    private BigDecimal decimal(String value) {
        if (value == null || value.isBlank()) {
            throw new NumberFormatException();
        }
        return new BigDecimal(value.trim());
    }

    private void flashAmounts(RedirectAttributes attrs, String baseSalary, String bonus,
                              String allowance, String deduction, String socialSecurity) {
        attrs.addFlashAttribute("rawBase", baseSalary);
        attrs.addFlashAttribute("rawBonus", bonus);
        attrs.addFlashAttribute("rawAllowance", allowance);
        attrs.addFlashAttribute("rawDeduction", deduction);
        attrs.addFlashAttribute("rawSocial", socialSecurity);
    }

    private AppUser currentUser(Principal principal) {
        return userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new IllegalStateException("当前用户不存在"));
    }
}
