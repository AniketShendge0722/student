
package student.mgt.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.security.access.prepost.PreAuthorize;

import student.mgt.service.AdminDashboardService;

@Controller
@PreAuthorize("hasRole('SUPER_ADMIN')")
public class AdminDashboardController {

    private final AdminDashboardService adminDashboardService;

    public AdminDashboardController(
            AdminDashboardService adminDashboardService) {
        this.adminDashboardService = adminDashboardService;
    }

    @GetMapping({"/admin", "/admin/"})
    public String dashboard(Model model) {

        model.addAttribute(
                "overview",
                adminDashboardService.getOverviewCounts()
        );

        return "admin/dashboard";
    }
}
