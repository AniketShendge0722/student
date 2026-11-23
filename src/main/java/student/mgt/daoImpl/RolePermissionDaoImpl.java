
package student.mgt.daoImpl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import student.mgt.dao.RolePermissionDao;
import student.mgt.entity.RolePermission;

import java.util.List;

@Repository
@Transactional
public class RolePermissionDaoImpl implements RolePermissionDao {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional(readOnly = true)
    public List<RolePermission> findByRoleId(Long roleId) {
        return entityManager.createQuery(
                        "SELECT rp FROM RolePermission rp " +
                                "JOIN FETCH rp.permission " +
                                "WHERE rp.role.id = :roleId " +
                                "ORDER BY rp.permission.moduleName, " +
                                "rp.permission.permissionCode",
                        RolePermission.class
                )
                .setParameter("roleId", roleId)
                .getResultList();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean exists(Long roleId, Long permissionId) {
        Long count = entityManager.createQuery(
                        "SELECT COUNT(rp) FROM RolePermission rp " +
                                "WHERE rp.role.id = :roleId " +
                                "AND rp.permission.id = :permissionId",
                        Long.class
                )
                .setParameter("roleId", roleId)
                .setParameter("permissionId", permissionId)
                .getSingleResult();

        return count > 0;
    }

    @Override
    public RolePermission save(RolePermission rolePermission) {
        entityManager.persist(rolePermission);
        return rolePermission;
    }

    @Override
    public void delete(Long roleId, Long permissionId) {
        List<RolePermission> mappings = entityManager.createQuery(
                        "SELECT rp FROM RolePermission rp " +
                                "WHERE rp.role.id = :roleId " +
                                "AND rp.permission.id = :permissionId",
                        RolePermission.class
                )
                .setParameter("roleId", roleId)
                .setParameter("permissionId", permissionId)
                .getResultList();

        for (RolePermission mapping : mappings) {
            entityManager.remove(mapping);
        }
    }
}
