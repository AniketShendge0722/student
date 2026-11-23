
package student.mgt.dao;

import student.mgt.entity.Role;

import java.util.List;
import java.util.Optional;

public interface RoleDao {

    Role save(Role role);

    Role update(Role role);

    Optional<Role> findById(Long id);

    List<Role> findAll();

    boolean existsByRoleName(String roleName);

    boolean existsByRoleNameAndIdNot(String roleName, Long id);

    long countActiveMembershipsByRoleId(Long roleId);
}
