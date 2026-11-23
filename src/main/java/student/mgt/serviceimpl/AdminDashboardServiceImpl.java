
package student.mgt.serviceimpl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import student.mgt.service.AdminDashboardService;

import java.util.LinkedHashMap;
import java.util.Map;

@Service
@Transactional(readOnly = true)
public class AdminDashboardServiceImpl implements AdminDashboardService {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Map<String, Long> getOverviewCounts() {

        Map<String, Long> counts = new LinkedHashMap<>();

        counts.put(
                "totalTenants",
                entityManager.createQuery(
                        "SELECT COUNT(t) FROM Tenant t",
                        Long.class
                ).getSingleResult()
        );

        counts.put(
                "activeTenants",
                entityManager.createQuery(
                        "SELECT COUNT(t) FROM Tenant t WHERE t.active = true",
                        Long.class
                ).getSingleResult()
        );

        counts.put(
                "totalUsers",
                entityManager.createQuery(
                        "SELECT COUNT(u) FROM AppUser u",
                        Long.class
                ).getSingleResult()
        );

        counts.put(
                "activeUsers",
                entityManager.createQuery(
                        "SELECT COUNT(u) FROM AppUser u WHERE u.enabled = true",
                        Long.class
                ).getSingleResult()
        );

        counts.put(
                "totalRoles",
                entityManager.createQuery(
                        "SELECT COUNT(r) FROM Role r",
                        Long.class
                ).getSingleResult()
        );

        counts.put(
                "activeRoles",
                entityManager.createQuery(
                        "SELECT COUNT(r) FROM Role r WHERE r.active = true",
                        Long.class
                ).getSingleResult()
        );

        counts.put(
                "totalPermissions",
                entityManager.createQuery(
                        "SELECT COUNT(p) FROM Permission p",
                        Long.class
                ).getSingleResult()
        );

        counts.put(
                "activeMemberships",
                entityManager.createQuery(
                        "SELECT COUNT(m) FROM UserTenantRole m WHERE m.active = true",
                        Long.class
                ).getSingleResult()
        );

        return counts;
    }
}
