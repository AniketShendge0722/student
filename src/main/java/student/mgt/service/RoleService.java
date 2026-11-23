
package student.mgt.service;

import student.mgt.entity.Role;

import java.util.List;
import java.util.Optional;

public interface RoleService {

    List<Role> findAll();

    Optional<Role> findById(Long id);

    Role createRole(String roleName, String description);

    Role updateRole(Long id, String roleName, String description);

    Role toggleActive(Long id);

    long countActiveMemberships(Long roleId);
}
