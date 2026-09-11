package com.lankaride.dashboard;

import com.lankaride.vehicle.BranchRepository;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.time.LocalDate;
import java.util.Map;

@Controller
@RequestMapping("/reports")
@PreAuthorize("hasAnyRole('ADMIN','FINANCE_MANAGER','OPERATIONS_MANAGER')")
public class ReportsController {

    private final ReportService reportService;
    private final BranchRepository branchRepository;

    public ReportsController(ReportService reportService, BranchRepository branchRepository) {
        this.reportService = reportService;
        this.branchRepository = branchRepository;
    }

    @GetMapping
    public String reports(@RequestParam(defaultValue = "daily") String period,
                          @RequestParam(required = false) Long branchId,
                          @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
                          Model model) {
        Map<String, Object> report = reportService.buildReport(period, branchId, date);
        model.addAllAttributes(report);
        model.addAttribute("branches", branchRepository.findAll());
        return "dashboard/reports";
    }
}
