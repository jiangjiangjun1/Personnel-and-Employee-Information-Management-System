package com.fzgs.personnel.controller;

import com.fzgs.personnel.model.AppUser;
import com.fzgs.personnel.model.PayrollRecord;
import com.fzgs.personnel.repository.UserRepository;
import com.fzgs.personnel.service.AttendanceService;
import com.fzgs.personnel.service.DashboardService;
import com.fzgs.personnel.service.PayrollService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.math.BigDecimal;
import java.security.Principal;
import java.time.YearMonth;
import java.util.List;

@Controller
public class DashboardController {

    private final DashboardService dashboardService;
    private final AttendanceService attendanceService;
    private final PayrollService payrollService;
    private final UserRepository userRepository;

    public DashboardController(DashboardService dashboardService,
                               AttendanceService attendanceService,
                               PayrollService payrollService,
                               UserRepository userRepository) {
        this.dashboardService = dashboardService;
        this.attendanceService = attendanceService;
        this.payrollService = payrollService;
        this.userRepository = userRepository;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model, Principal principal) {
        AppUser user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new IllegalStateException("当前用户不存在"));

        String month = YearMonth.now().toString();
        List<PayrollRecord> payroll = payrollService.findByMonth(month);
        BigDecimal payrollTotal = payroll.stream()
                .map(PayrollRecord::netSalary)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        model.addAttribute("appUser", user);
        model.addAttribute("summary", dashboardService.summary());
        model.addAttribute("recentAttendance",
                attendanceService.findPage(null, null, null, 1, 5).items());
        model.addAttribute("currentMonth", month);
        model.addAttribute("payrollRecords", payroll);
        model.addAttribute("payrollTotal", payrollTotal);
        return "dashboard/index";
    }
}
