
package student.mgt.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import student.mgt.entity.Tenant;
import student.mgt.entity.UserTenantRole;
import student.mgt.serviceimpl.TenantMembershipServiceImpl;
import student.mgt.util.TenantContextHolder;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class TenantContextFilter extends OncePerRequestFilter {

    public static final String SESSION_TENANT_ID = "ACTIVE_TENANT_ID";

    private final TenantMembershipServiceImpl membershipService;

    public TenantContextFilter(
            TenantMembershipServiceImpl membershipService) {
        this.membershipService = membershipService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        try {
            Authentication authentication =
                    SecurityContextHolder.getContext().getAuthentication();

            if (authentication == null
                    || !authentication.isAuthenticated()
                    || authentication instanceof AnonymousAuthenticationToken) {

                filterChain.doFilter(request, response);
                return;
            }

            String username = authentication.getName();
            String requestUri = request.getRequestURI();
            String contextPath = request.getContextPath();

            if (isPublicOrSelectionPath(requestUri, contextPath)) {
                filterChain.doFilter(request, response);
                return;
            }

            List<UserTenantRole> memberships =
                    membershipService.findActiveMemberships(username);

            Map<Long, Tenant> availableTenants = new LinkedHashMap<>();

            for (UserTenantRole membership : memberships) {
                Tenant tenant = membership.getTenant();
                availableTenants.putIfAbsent(tenant.getId(), tenant);
            }

            HttpSession session = request.getSession(false);

            Long selectedTenantId = null;

            if (session != null) {
                Object storedTenantId =
                        session.getAttribute(SESSION_TENANT_ID);

                if (storedTenantId instanceof Long) {
                    selectedTenantId = (Long) storedTenantId;
                }
            }

            // No active school membership.
            if (availableTenants.isEmpty()) {
                rejectRequest(request, response);
                return;
            }

            // Validate the currently selected school.
            if (selectedTenantId != null
                    && availableTenants.containsKey(selectedTenantId)) {

                TenantContextHolder.setTenantId(selectedTenantId);

                filterChain.doFilter(request, response);
                return;
            }

            // A stored selection is no longer valid.
            if (session != null && selectedTenantId != null) {
                session.removeAttribute(SESSION_TENANT_ID);
            }

            // Automatically select the only available school.
            if (availableTenants.size() == 1) {

                Long tenantId =
                        availableTenants.keySet().iterator().next();

                TenantContextHolder.setTenantId(tenantId);

                request.getSession(true).setAttribute(
                        SESSION_TENANT_ID,
                        tenantId
                );

                filterChain.doFilter(request, response);
                return;
            }

            // Multiple schools: user must choose one.
            redirectToSchoolSelection(request, response);

        } finally {
            TenantContextHolder.clear();
        }
    }

    private boolean isPublicOrSelectionPath(
            String requestUri,
            String contextPath) {

        String path = requestUri.substring(contextPath.length());

        return path.equals("/login")
                || path.equals("/logout")
                || path.equals("/error")
                || path.equals("/tenant/select")
                || path.startsWith("/css/")
                || path.startsWith("/js/")
                || path.startsWith("/images/")
                || path.equals("/favicon.ico");
    }

    private void redirectToSchoolSelection(
            HttpServletRequest request,
            HttpServletResponse response) throws IOException {

        if (request.getRequestURI().startsWith(
                request.getContextPath() + "/api/")) {

            response.sendError(
                    HttpServletResponse.SC_FORBIDDEN,
                    "Please select an active school before accessing this API."
            );
            return;
        }

        response.sendRedirect(
                request.getContextPath() + "/tenant/select"
        );
    }

    private void rejectRequest(
            HttpServletRequest request,
            HttpServletResponse response) throws IOException {

        if (request.getRequestURI().startsWith(
                request.getContextPath() + "/api/")) {

            response.sendError(
                    HttpServletResponse.SC_FORBIDDEN,
                    "No active school membership is available."
            );
            return;
        }

        response.sendRedirect(
                request.getContextPath() + "/tenant/select"
        );
    }
}
