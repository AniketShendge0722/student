
package student.mgt.dao;

import student.mgt.entity.Permission;

import java.util.List;
import java.util.Optional;

public interface PermissionDao {

    List<Permission> findAll();

    List<Permission> findActiveNotAssignedToRole(Long roleId);

    Optional<Permission> findById(Long id);
}
