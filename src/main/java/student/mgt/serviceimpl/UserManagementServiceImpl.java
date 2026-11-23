
package student.mgt.serviceimpl;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import student.mgt.dao.AppUserDao;
import student.mgt.dao.RoleDao;
import student.mgt.dao.TenantDao;
import student.mgt.dao.UserTenantRoleDao;
import student.mgt.dto.UserForm;
import student.mgt.dto.UserTenantRoleForm;
import student.mgt.entity.AppUser;
import student.mgt.entity.Role;
import student.mgt.entity.Tenant;
import student.mgt.entity.UserTenantRole;
import student.mgt.service.UserManagementService;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Service
@Transactional
public class UserManagementServiceImpl implements UserManagementService {

    private final AppUserDao appUserDao;
    private final UserTenantRoleDao userTenantRoleDao;
    private final TenantDao tenantDao;
    private final RoleDao roleDao;
    private final PasswordEncoder passwordEncoder;

    public UserManagementServiceImpl(
            AppUserDao appUserDao,
            UserTenantRoleDao userTenantRoleDao,
            TenantDao tenantDao,
            RoleDao roleDao,
            PasswordEncoder passwordEncoder) {

        this.appUserDao = appUserDao;
        this.userTenantRoleDao = userTenantRoleDao;
        this.tenantDao = tenantDao;
        this.roleDao = roleDao;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional(readOnly = true)
    public List<AppUser> findAllUsers() {
        return appUserDao.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<AppUser> findUserById(Long id) {
        return appUserDao.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserTenantRole> findMemberships(Long userId) {
        return userTenantRoleDao.findByUserId(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Tenant> findActiveTenants() {
        return tenantDao.findAllActive();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Role> findActiveRoles() {
        return roleDao.findAll()
                .stream()
                .filter(Role::isActive)
                .toList();
    }

    @Override
    public void createUser(UserForm form) {

        validateForm(form);

        String username = normalizeUsername(form.getUsername());

        if (appUserDao.existsByUsername(username)) {
            throw new IllegalArgumentException(
                    "Username already exists"
            );
        }

        validatePassword(form.getPassword());

        AppUser user = new AppUser();

        user.setUsername(username);
        user.setPassword(
                passwordEncoder.encode(form.getPassword())
        );

        // Keep the existing legacy role column populated.
        // Actual school permissions come from UserTenantRole.
        user.setRole("ROLE_USER");
        user.setEnabled(true);

        appUserDao.save(user);

        synchronizeMemberships(user, form.getAssignments());
    }

    @Override
    public void updateUser(Long userId, UserForm form) {

        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("Invalid user ID");
        }

        validateForm(form);

        AppUser user = appUserDao.findById(userId)
                .orElseThrow(() ->
                        new IllegalArgumentException("User not found")
                );

        String username = normalizeUsername(form.getUsername());

        if (appUserDao.existsByUsernameAndIdNot(username, userId)) {
            throw new IllegalArgumentException(
                    "Username already exists"
            );
        }

        user.setUsername(username);

        // Do not modify password or legacy role during normal editing.
        appUserDao.update(user);

        synchronizeMemberships(user, form.getAssignments());
    }


    @Override
    public void updateUserStatus(
            Long userId,
            boolean enabled,
            String currentUsername) {

        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("Invalid user ID");
        }

        AppUser user = appUserDao.findById(userId)
                .orElseThrow(() ->
                        new IllegalArgumentException("User not found")
                );

        if (!enabled
                && currentUsername != null
                && user.getUsername().equalsIgnoreCase(
                currentUsername.trim())) {

            throw new IllegalArgumentException(
                    "You cannot deactivate your own account"
            );
        }

        // Never deactivate the last enabled Super Admin.
        if (!enabled
                && user.isEnabled()
                && hasActiveSuperAdminMembership(userId)
                && countOtherEnabledSuperAdmins(userId) == 0) {

            throw new IllegalArgumentException(
                    "Cannot deactivate the last active Super Admin"
            );
        }

        user.setEnabled(enabled);
        appUserDao.update(user);
    }

    @Override
    public void resetPassword(Long userId, String newPassword) {

        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("Invalid user ID");
        }

        validatePassword(newPassword);

        AppUser user = appUserDao.findById(userId)
                .orElseThrow(() ->
                        new IllegalArgumentException("User not found")
                );

        user.setPassword(passwordEncoder.encode(newPassword));

        appUserDao.update(user);
    }


    private void synchronizeMemberships(
            AppUser user,
            List<UserTenantRoleForm> assignments) {

        if (assignments == null || assignments.isEmpty()) {
            throw new IllegalArgumentException(
                    "Assign the user to at least one school and role"
            );
        }

        List<UserTenantRole> existingMemberships =
                userTenantRoleDao.findByUserId(user.getId());

        Map<String, UserTenantRole> existingMap = new HashMap<>();

        Set<String> protectedSuperAdminKeys = new HashSet<>();

        for (UserTenantRole membership : existingMemberships) {

            String key = membershipKey(
                    membership.getTenant().getId(),
                    membership.getRole().getId()
            );

            existingMap.put(key, membership);

            // Preserve every existing active Super Admin membership.
            if (membership.isActive()
                    && isSuperAdminRole(membership.getRole())) {

                protectedSuperAdminKeys.add(key);
            }
        }

        Set<String> submittedKeys = new HashSet<>();

        for (UserTenantRoleForm assignment : assignments) {

            if (assignment == null
                    || assignment.getTenantId() == null
                    || assignment.getRoleId() == null
                    || assignment.getTenantId() <= 0
                    || assignment.getRoleId() <= 0) {

                throw new IllegalArgumentException(
                        "Invalid school or role assignment"
                );
            }

            Long tenantId = assignment.getTenantId();
            Long roleId = assignment.getRoleId();

            String key = membershipKey(tenantId, roleId);

            if (!submittedKeys.add(key)) {
                throw new IllegalArgumentException(
                        "Duplicate school and role assignment"
                );
            }

            Tenant tenant = tenantDao.findById(tenantId)
                    .orElseThrow(() ->
                            new IllegalArgumentException(
                                    "Selected school does not exist"
                            )
                    );

            if (!tenant.isActive()) {
                throw new IllegalArgumentException(
                        "Cannot assign a user to an inactive school"
                );
            }

            Role role = roleDao.findById(roleId)
                    .orElseThrow(() ->
                            new IllegalArgumentException(
                                    "Selected role does not exist"
                            )
                    );

            if (!role.isActive()) {
                throw new IllegalArgumentException(
                        "Cannot assign an inactive role"
                );
            }

            UserTenantRole existing = existingMap.get(key);

            // Do not allow creation or reactivation of a Super Admin
            // membership through ordinary user management.
            if (isSuperAdminRole(role)
                    && (existing == null || !existing.isActive())) {

                throw new IllegalArgumentException(
                        "Super Admin role cannot be assigned through user management"
                );
            }

            if (existing != null) {

                existing.setActive(true);
                userTenantRoleDao.update(existing);

            } else {

                UserTenantRole membership =
                        new UserTenantRole(user, tenant, role);

                membership.setActive(true);

                userTenantRoleDao.save(membership);
            }
        }

        // Keep protected Super Admin memberships even if the edit form
        // does not submit them.
        Set<String> retainedKeys = new HashSet<>(submittedKeys);
        retainedKeys.addAll(protectedSuperAdminKeys);

        // Deactivate assignments removed from the submitted form,
        // except protected Super Admin memberships.
        for (UserTenantRole membership : existingMemberships) {

            String key = membershipKey(
                    membership.getTenant().getId(),
                    membership.getRole().getId()
            );

            if (!retainedKeys.contains(key)
                    && membership.isActive()) {

                membership.setActive(false);
                userTenantRoleDao.update(membership);
            }
        }
    }

    private void validateForm(UserForm form) {

        if (form == null) {
            throw new IllegalArgumentException(
                    "User information is required"
            );
        }

        if (form.getUsername() == null
                || form.getUsername().isBlank()) {

            throw new IllegalArgumentException(
                    "Username is required"
            );
        }

        if (form.getUsername().trim().length() < 3
                || form.getUsername().trim().length() > 60) {

            throw new IllegalArgumentException(
                    "Username must be between 3 and 60 characters"
            );
        }

        if (!form.getUsername().trim()
                .matches("^[A-Za-z0-9._-]+$")) {

            throw new IllegalArgumentException(
                    "Username can contain letters, numbers, dots, underscores and hyphens only"
            );
        }
    }

    private void validatePassword(String password) {

        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException(
                    "Password is required"
            );
        }

        if (password.length() < 8 || password.length() > 72) {
            throw new IllegalArgumentException(
                    "Password must be between 8 and 72 characters"
            );
        }
    }

    private String normalizeUsername(String username) {
        return username.trim();
    }

    private String membershipKey(Long tenantId, Long roleId) {
        return tenantId + ":" + roleId;
    }

    private boolean isSuperAdminRole(Role role) {

        return role != null
                && "ROLE_SUPER_ADMIN".equalsIgnoreCase(
                role.getRoleName().trim()
        );
    }

    private boolean hasActiveSuperAdminMembership(Long userId) {

        return userTenantRoleDao.findByUserId(userId)
                .stream()
                .anyMatch(membership ->
                        membership.isActive()
                                && isSuperAdminRole(membership.getRole())
                );
    }

    private long countOtherEnabledSuperAdmins(Long excludedUserId) {

        return appUserDao.findAll()
                .stream()
                .filter(AppUser::isEnabled)
                .filter(user -> !user.getId().equals(excludedUserId))
                .filter(user ->
                        hasActiveSuperAdminMembership(user.getId())
                )
                .count();
    }

}
