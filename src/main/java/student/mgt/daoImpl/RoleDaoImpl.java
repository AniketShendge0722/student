
package student.mgt.daoImpl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import student.mgt.dao.RoleDao;
import student.mgt.entity.Role;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public class RoleDaoImpl implements RoleDao {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Role save(Role role) {
        entityManager.persist(role);
        return role;
    }

    @Override
    public Role update(Role role) {
        return entityManager.merge(role);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Role> findById(Long id) {
        return Optional.ofNullable(entityManager.find(Role.class, id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Role> findAll() {
        return entityManager.createQuery(
                        "SELECT r FROM Role r ORDER BY r.systemRole DESC, r.roleName ASC",
                        Role.class
                )
                .getResultList();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByRoleName(String roleName) {
        Long count = entityManager.createQuery(
                        "SELECT COUNT(r) FROM Role r WHERE LOWER(r.roleName) = LOWER(:name)",
                        Long.class
                )
                .setParameter("name", roleName)
                .getSingleResult();

        return count > 0;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByRoleNameAndIdNot(String roleName, Long id) {
        Long count = entityManager.createQuery(
                        "SELECT COUNT(r) FROM Role r " +
                                "WHERE LOWER(r.roleName) = LOWER(:name) " +
                                "AND r.id <> :id",
                        Long.class
                )
                .setParameter("name", roleName)
                .setParameter("id", id)
                .getSingleResult();

        return count > 0;
    }

    @Override
    @Transactional(readOnly = true)
    public long countActiveMembershipsByRoleId(Long roleId) {
        return entityManager.createQuery(
                        "SELECT COUNT(m) FROM UserTenantRole m " +
                                "WHERE m.role.id = :roleId AND m.active = true",
                        Long.class
                )
                .setParameter("roleId", roleId)
                .getSingleResult();
    }
}
