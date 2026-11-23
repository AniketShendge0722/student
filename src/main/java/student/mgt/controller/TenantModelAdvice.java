
package student.mgt.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import student.mgt.config.TenantContextFilter;
import student.mgt.entity.Tenant;
import student.mgt.entity.UserTenantRole;
import student.mgt.serviceimpl.TenantMembershipServiceImpl;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@ControllerAdvice
public class TenantModelAdvice {

    private final TenantMembershipServiceImpl membershipService;

    public TenantModelAdvice(
            TenantMembershipServiceImpl membershipService) {
        this.membershipService = membershipService;
    }

    @ModelAttribute
    public void addTenantInformation(
            Authentication authentication,
            HttpServletRequest request,
            org.springframework.ui.Model model) {

        if (authentication == null
                || !authentication.isAuthenticated()
                || authentication instanceof AnonymousAuthenticationToken) {
            return;
        }

        List<UserTenantRole> memberships =
                membershipService.findActiveMemberships(
                        authentication.getName()
                );

        Map<Long, Tenant> uniqueTenants = new LinkedHashMap<>();

        for (UserTenantRole membership : memberships) {
            Tenant tenant = membership.getTenant();
            uniqueTenants.putIfAbsent(tenant.getId(), tenant);
        }

        List<Tenant> availableTenants =
                new ArrayList<>(uniqueTenants.values());

        HttpSession session = request.getSession(false);

        Long selectedTenantId = null;

        if (session != null) {
            Object storedTenantId =
                    session.getAttribute(
                            TenantContextFilter.SESSION_TENANT_ID
                    );

            if (storedTenantId instanceof Long) {
                selectedTenantId = (Long) storedTenantId;
            }
        }

        Tenant selectedTenant = null;

        if (selectedTenantId != null) {
            selectedTenant = uniqueTenants.get(selectedTenantId);
        }

        model.addAttribute("availableTenants", availableTenants);
        model.addAttribute("selectedTenant", selectedTenant);
        model.addAttribute("currentUsername", authentication.getName());
        model.addAttribute(
                "hasMultipleTenants",
                availableTenants.size() > 1
        );
    }
}
