
package student.mgt.dao;

import student.mgt.entity.RolePermission;

import java.util.List;
import java.util.Optional;

public interface RolePermissionDao {

    List<RolePermission> findByRoleId(Long roleId);

    boolean exists(Long roleId, Long permissionId);

    RolePermission save(RolePermission rolePermission);

    void delete(Long roleId, Long permissionId);
}
