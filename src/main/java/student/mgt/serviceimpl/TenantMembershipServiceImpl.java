
package student.mgt.serviceimpl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import student.mgt.entity.UserTenantRole;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class TenantMembershipServiceImpl {

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Returns all active tenant memberships for an enabled user.
     * Only memberships with active tenants and active roles are included.
     */
    public List<UserTenantRole> findActiveMemberships(String username) {

        if (username == null || username.isBlank()) {
            return List.of();
        }

        return entityManager.createQuery(
                        "SELECT utr " +
                                "FROM UserTenantRole utr " +
                                "JOIN FETCH utr.user u " +
                                "JOIN FETCH utr.tenant t " +
                                "JOIN FETCH utr.role r " +
                                "WHERE u.username = :username " +
                                "AND u.enabled = true " +
                                "AND utr.active = true " +
                                "AND t.active = true " +
                                "AND r.active = true " +
                                "ORDER BY t.tenantName, r.roleName",
                        UserTenantRole.class
                )
                .setParameter("username", username.trim())
                .getResultList();
    }

    /**
     * Checks whether the user has an active membership in the requested tenant.
     * The submitted tenant ID must never be trusted without this check.
     */
    public Optional<UserTenantRole> findActiveMembership(
            String username,
            Long tenantId) {

        if (username == null || username.isBlank()
                || tenantId == null || tenantId <= 0) {
            return Optional.empty();
        }

        List<UserTenantRole> results = entityManager.createQuery(
                        "SELECT utr " +
                                "FROM UserTenantRole utr " +
                                "JOIN FETCH utr.user u " +
                                "JOIN FETCH utr.tenant t " +
                                "JOIN FETCH utr.role r " +
                                "WHERE u.username = :username " +
                                "AND u.enabled = true " +
                                "AND t.id = :tenantId " +
                                "AND utr.active = true " +
                                "AND t.active = true " +
                                "AND r.active = true",
                        UserTenantRole.class
                )
                .setParameter("username", username.trim())
                .setParameter("tenantId", tenantId)
                .setMaxResults(1)
                .getResultList();

        return results.stream().findFirst();
    }

    /**
     * Returns the distinct active tenant count for the user.
     */
    public long countActiveTenants(String username) {

        if (username == null || username.isBlank()) {
            return 0;
        }

        return entityManager.createQuery(
                        "SELECT COUNT(DISTINCT t.id) " +
                                "FROM UserTenantRole utr " +
                                "JOIN utr.user u " +
                                "JOIN utr.tenant t " +
                                "JOIN utr.role r " +
                                "WHERE u.username = :username " +
                                "AND u.enabled = true " +
                                "AND utr.active = true " +
                                "AND t.active = true " +
                                "AND r.active = true",
                        Long.class
                )
                .setParameter("username", username.trim())
                .getSingleResult();
    }
}
