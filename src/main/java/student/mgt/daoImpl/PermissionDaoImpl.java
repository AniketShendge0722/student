
package student.mgt.daoImpl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import student.mgt.dao.PermissionDao;
import student.mgt.entity.Permission;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public class PermissionDaoImpl implements PermissionDao {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional(readOnly = true)
    public List<Permission> findAll() {
        return entityManager.createQuery(
                "SELECT p FROM Permission p " +
                        "ORDER BY p.moduleName ASC, p.permissionCode ASC",
                Permission.class
        ).getResultList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Permission> findActiveNotAssignedToRole(Long roleId) {
        return entityManager.createQuery(
                        "SELECT p FROM Permission p " +
                                "WHERE p.active = true " +
                                "AND NOT EXISTS (" +
                                "   SELECT rp.id FROM RolePermission rp " +
                                "   WHERE rp.role.id = :roleId " +
                                "   AND rp.permission.id = p.id" +
                                ") " +
                                "ORDER BY p.moduleName ASC, p.permissionCode ASC",
                        Permission.class
                )
                .setParameter("roleId", roleId)
                .getResultList();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Permission> findById(Long id) {
        return Optional.ofNullable(
                entityManager.find(Permission.class, id)
        );
    }
}
