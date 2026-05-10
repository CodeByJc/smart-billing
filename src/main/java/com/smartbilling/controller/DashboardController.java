package com.smartbilling.controller;

import com.smartbilling.model.DashboardStats;
import com.smartbilling.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controller for the admin dashboard.
 * Displays aggregate statistics, recent invoices, and low-stock alerts.
 */
@Controller
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    /**
     * Display the dashboard page with all statistics.
     */
    @GetMapping("/dashboard")
    public String showDashboard(Model model) {
        DashboardStats stats = dashboardService.getDashboardStats();
        model.addAttribute("stats", stats);
        model.addAttribute("activePage", "dashboard");
        return "dashboard";
    }

    /**
     * Root URL redirect to dashboard.
     */
    @GetMapping("/")
    public String root() {
        return "redirect:/dashboard";
    }
}
