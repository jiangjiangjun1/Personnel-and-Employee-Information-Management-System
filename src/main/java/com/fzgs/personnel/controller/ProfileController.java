package com.fzgs.personnel.controller;

import com.fzgs.personnel.common.BusinessException;
import com.fzgs.personnel.model.AppUser;
import com.fzgs.personnel.model.Employee;
import com.fzgs.personnel.model.PayrollRecord;
import com.fzgs.personnel.repository.UserRepository;
import com.fzgs.personnel.service.AttendanceService;
import com.fzgs.personnel.service.EmployeeService;
import com.fzgs.personnel.service.LeaveService;
import com.fzgs.personnel.service.PayrollService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.time.YearMonth;
import java.util.List;

@Controller
@RequestMapping("/profile")
public class ProfileController {

    private final UserRepository userRepository;
    private final EmployeeService employeeService;
    private final AttendanceService attendanceService;
    private final LeaveService leaveService;
    private final PayrollService payrollService;

    public ProfileController(UserRepository userRepository,
                             EmployeeService employeeService,
                             AttendanceService attendanceService,
                             LeaveService leaveService,
                             PayrollService payrollService) {
        this.userRepository = userRepository;
        this.employeeService = employeeService;
        this.attendanceService = attendanceService;
        this.leaveService = leaveService;
        this.payrollService = payrollService;
    }

    @GetMapping
    public String view(Model model, Principal principal) {
        AppUser user = currentUser(principal);
        model.addAttribute("appUser", user);

        Employee employee = user.employeeId() == null
                ? null
                : employeeService.findByUserId(user.id()).orElse(null);
        model.addAttribute("employee", employee);

        if (employee != null) {
            String month = YearMonth.now().toString();
            List<PayrollRecord> payroll = payrollService.findByMonth(month).stream()
                    .filter(record -> record.employeeId().equals(employee.id()))
                    .toList();
            model.addAttribute("recentAttendance",
                    attendanceService.findPage(employee.id(), null, null, 1, 5).items());
            model.addAttribute("leaveRequests", leaveService.findForEmployee(employee.id()));
            model.addAttribute("currentMonth", month);
            model.addAttribute("payrollRecords", payroll);
        }
        return "profile/index";
    }

    @PostMapping
    public String updateContact(@RequestParam String phone,
                                @RequestParam String email,
                                @RequestParam String address,
                                Principal principal,
                                RedirectAttributes attrs) {
        AppUser user = currentUser(principal);
        if (user.employeeId() == null) {
            attrs.addFlashAttribute("error", "当前账号未关联员工");
            return "redirect:/profile";
        }
        try {
            employeeService.updateContact(user.employeeId(), phone, email, address);
            attrs.addFlashAttribute("message", "联系方式已更新");
        } catch (BusinessException e) {
            attrs.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/profile";
    }

    private AppUser currentUser(Principal principal) {
        return userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new IllegalStateException("当前用户不存在"));
    }
}
