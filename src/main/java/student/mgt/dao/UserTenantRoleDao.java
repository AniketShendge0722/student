
package student.mgt.dao;

import student.mgt.entity.UserTenantRole;

import java.util.List;
import java.util.Optional;

public interface UserTenantRoleDao {

    UserTenantRole save(UserTenantRole membership);

    UserTenantRole update(UserTenantRole membership);

    Optional<UserTenantRole> findById(Long id);

    List<UserTenantRole> findByUserId(Long userId);

    List<UserTenantRole> findByUserIdAndTenantId(
            Long userId,
            Long tenantId
    );

    long countActiveMembershipsByUserId(Long userId);

    long countActiveMembershipsByTenantId(Long tenantId);
}
