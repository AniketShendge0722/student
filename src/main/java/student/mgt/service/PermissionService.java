
package student.mgt.service;

import student.mgt.entity.Permission;
import student.mgt.entity.RolePermission;

import java.util.List;

public interface PermissionService {

    List<Permission> findAll();

    List<RolePermission> findByRoleId(Long roleId);

    List<Permission> findAvailableForRole(Long roleId);

    void assignPermission(Long roleId, Long permissionId);

    void removePermission(Long roleId, Long permissionId);
}
