
package student.mgt.serviceimpl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import student.mgt.dao.PermissionDao;
import student.mgt.dao.RolePermissionDao;
import student.mgt.dao.RoleDao;

import student.mgt.entity.Permission;
import student.mgt.entity.Role;
import student.mgt.entity.RolePermission;

import student.mgt.service.PermissionService;

import java.util.List;

@Service
@Transactional
public class PermissionServiceImpl implements PermissionService {

    private final PermissionDao permissionDao;
    private final RolePermissionDao rolePermissionDao;
    private final RoleDao roleDao;

    public PermissionServiceImpl(
            PermissionDao permissionDao,
            RolePermissionDao rolePermissionDao,
            RoleDao roleDao) {

        this.permissionDao = permissionDao;
        this.rolePermissionDao = rolePermissionDao;
        this.roleDao = roleDao;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Permission> findAll() {
        return permissionDao.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<RolePermission> findByRoleId(Long roleId) {
        getRoleOrThrow(roleId);
        return rolePermissionDao.findByRoleId(roleId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Permission> findAvailableForRole(Long roleId) {
        Role role = getRoleOrThrow(roleId);
        validateCustomRole(role);

        return permissionDao.findActiveNotAssignedToRole(roleId);
    }

    @Override
    public void assignPermission(Long roleId, Long permissionId) {

        Role role = getRoleOrThrow(roleId);
        validateCustomRole(role);

        Permission permission = permissionDao.findById(permissionId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Permission not found.")
                );

        if (!role.isActive()) {
            throw new IllegalArgumentException(
                    "Permissions cannot be changed for an inactive role."
            );
        }

        if (!permission.isActive()) {
            throw new IllegalArgumentException(
                    "Inactive permissions cannot be assigned."
            );
        }

        if (rolePermissionDao.exists(roleId, permissionId)) {
            throw new IllegalArgumentException(
                    "This permission is already assigned to the role."
            );
        }

        rolePermissionDao.save(
                new RolePermission(role, permission)
        );
    }

    @Override
    public void removePermission(Long roleId, Long permissionId) {

        Role role = getRoleOrThrow(roleId);
        validateCustomRole(role);

        if (!rolePermissionDao.exists(roleId, permissionId)) {
            throw new IllegalArgumentException(
                    "This permission is not assigned to the role."
            );
        }

        rolePermissionDao.delete(roleId, permissionId);
    }

    private Role getRoleOrThrow(Long roleId) {

        if (roleId == null || roleId <= 0) {
            throw new IllegalArgumentException("Invalid role ID.");
        }

        return roleDao.findById(roleId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Role not found.")
                );
    }

    private void validateCustomRole(Role role) {

        if (role.isSystemRole()) {
            throw new IllegalArgumentException(
                    "System role permissions are protected and cannot be changed here."
            );
        }
    }
}
