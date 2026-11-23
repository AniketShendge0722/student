
package student.mgt.daoImpl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import student.mgt.dao.UserTenantRoleDao;
import student.mgt.entity.UserTenantRole;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public class UserTenantRoleDaoImpl implements UserTenantRoleDao {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public UserTenantRole save(UserTenantRole membership) {
        entityManager.persist(membership);
        return membership;
    }

    @Override
    public UserTenantRole update(UserTenantRole membership) {
        return entityManager.merge(membership);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UserTenantRole> findById(Long id) {
        return Optional.ofNullable(
                entityManager.find(UserTenantRole.class, id)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserTenantRole> findByUserId(Long userId) {

        return entityManager.createQuery(
                        "SELECT utr FROM UserTenantRole utr " +
                                "JOIN FETCH utr.user u " +
                                "JOIN FETCH utr.tenant t " +
                                "JOIN FETCH utr.role r " +
                                "WHERE u.id = :userId " +
                                "ORDER BY t.tenantName, r.roleName",
                        UserTenantRole.class
                )
                .setParameter("userId", userId)
                .getResultList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserTenantRole> findByUserIdAndTenantId(
            Long userId,
            Long tenantId) {

        return entityManager.createQuery(
                        "SELECT utr FROM UserTenantRole utr " +
                                "JOIN FETCH utr.user u " +
                                "JOIN FETCH utr.tenant t " +
                                "JOIN FETCH utr.role r " +
                                "WHERE u.id = :userId " +
                                "AND t.id = :tenantId " +
                                "ORDER BY r.roleName",
                        UserTenantRole.class
                )
                .setParameter("userId", userId)
                .setParameter("tenantId", tenantId)
                .getResultList();
    }

    @Override
    @Transactional(readOnly = true)
    public long countActiveMembershipsByUserId(Long userId) {

        return entityManager.createQuery(
                        "SELECT COUNT(utr) FROM UserTenantRole utr " +
                                "WHERE utr.user.id = :userId " +
                                "AND utr.active = true",
                        Long.class
                )
                .setParameter("userId", userId)
                .getSingleResult();
    }

    @Override
    @Transactional(readOnly = true)
    public long countActiveMembershipsByTenantId(Long tenantId) {

        return entityManager.createQuery(
                        "SELECT COUNT(utr) FROM UserTenantRole utr " +
                                "WHERE utr.tenant.id = :tenantId " +
                                "AND utr.active = true",
                        Long.class
                )
                .setParameter("tenantId", tenantId)
                .getSingleResult();
    }
}
