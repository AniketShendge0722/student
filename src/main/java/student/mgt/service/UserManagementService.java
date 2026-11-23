
package student.mgt.service;

import student.mgt.dto.UserForm;
import student.mgt.entity.AppUser;
import student.mgt.entity.Role;
import student.mgt.entity.Tenant;
import student.mgt.entity.UserTenantRole;

import java.util.List;
import java.util.Optional;

public interface UserManagementService {

    List<AppUser> findAllUsers();

    Optional<AppUser> findUserById(Long id);

    List<UserTenantRole> findMemberships(Long userId);

    List<Tenant> findActiveTenants();

    List<Role> findActiveRoles();

    void createUser(UserForm form);

    void updateUser(Long userId, UserForm form);

    void updateUserStatus(
            Long userId,
            boolean enabled,
            String currentUsername
    );

    void resetPassword(Long userId, String newPassword);
}
