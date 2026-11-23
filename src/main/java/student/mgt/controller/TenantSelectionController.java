
package student.mgt.controller;

import jakarta.servlet.http.HttpSession;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import student.mgt.config.TenantContextFilter;
import student.mgt.entity.Tenant;
import student.mgt.entity.UserTenantRole;
import student.mgt.serviceimpl.TenantMembershipServiceImpl;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Controller
public class TenantSelectionController {

    private final TenantMembershipServiceImpl membershipService;

    public TenantSelectionController(
            TenantMembershipServiceImpl membershipService) {
        this.membershipService = membershipService;
    }

    @GetMapping("/tenant/select")
    public String showTenantSelection(
            Authentication authentication,
            Model model) {

        List<UserTenantRole> memberships =
                membershipService.findActiveMemberships(
                        authentication.getName()
                );

        // A user may have multiple roles in the same tenant.
        // Display each tenant only once.
        Map<Long, Tenant> uniqueTenants = new LinkedHashMap<>();

        for (UserTenantRole membership : memberships) {
            Tenant tenant = membership.getTenant();
            uniqueTenants.putIfAbsent(tenant.getId(), tenant);
        }

        model.addAttribute(
                "tenants",
                uniqueTenants.values()
        );

        return "tenant/select";
    }

    @PostMapping("/tenant/select")
    public String selectTenant(
            @RequestParam("tenantId") Long tenantId,
            Authentication authentication,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        boolean validMembership =
                membershipService.findActiveMembership(
                        authentication.getName(),
                        tenantId
                ).isPresent();

        if (!validMembership) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "You do not have access to the selected school."
            );

            return "redirect:/tenant/select";
        }

        session.setAttribute(
                TenantContextFilter.SESSION_TENANT_ID,
                tenantId
        );

        redirectAttributes.addFlashAttribute(
                "successMessage",
                "School selected successfully."
        );

        return "redirect:/dashboard";
    }
}
