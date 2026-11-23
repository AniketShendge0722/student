
package student.mgt.serviceimpl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import student.mgt.dao.RoleDao;
import student.mgt.entity.Role;
import student.mgt.service.RoleService;

import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

@Service
@Transactional
public class RoleServiceImpl implements RoleService {

    private final RoleDao roleDao;

    private static final Pattern ROLE_NAME_PATTERN =
            Pattern.compile("^ROLE_[A-Z][A-Z0-9_]{1,54}$");

    public RoleServiceImpl(RoleDao roleDao) {
        this.roleDao = roleDao;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Role> findAll() {
        return roleDao.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Role> findById(Long id) {
        return roleDao.findById(id);
    }

    @Override
    public Role createRole(String roleName, String description) {

        String normalizedName = normalizeRoleName(roleName);
        validateRoleName(normalizedName);
        validateDescription(description);

        if (roleDao.existsByRoleName(normalizedName)) {
            throw new IllegalArgumentException(
                    "A role with this name already exists."
            );
        }

        Role role = new Role(
                normalizedName,
                cleanDescription(description),
                false
        );

        role.setActive(true);
        role.setSystemRole(false);

        return roleDao.save(role);
    }

    @Override
    public Role updateRole(
            Long id,
            String roleName,
            String description) {

        Role role = getRoleOrThrow(id);

        if (role.isSystemRole()) {
            throw new IllegalArgumentException(
                    "System roles cannot be edited."
            );
        }

        String normalizedName = normalizeRoleName(roleName);
        validateRoleName(normalizedName);
        validateDescription(description);

        if (roleDao.existsByRoleNameAndIdNot(normalizedName, id)) {
            throw new IllegalArgumentException(
                    "Another role already uses this name."
            );
        }

        role.setRoleName(normalizedName);
        role.setDescription(cleanDescription(description));

        return roleDao.update(role);
    }

    @Override
    public Role toggleActive(Long id) {

        Role role = getRoleOrThrow(id);

        if (role.isSystemRole()) {
            throw new IllegalArgumentException(
                    "System roles cannot be activated or deactivated here."
            );
        }

        if (role.isActive()) {

            long assignedUsers =
                    roleDao.countActiveMembershipsByRoleId(id);

            if (assignedUsers > 0) {
                throw new IllegalArgumentException(
                        "This role is assigned to " + assignedUsers +
                                " active user-school membership(s). " +
                                "Reassign or deactivate those memberships first."
                );
            }

            role.setActive(false);

        } else {
            role.setActive(true);
        }

        return roleDao.update(role);
    }

    @Override
    @Transactional(readOnly = true)
    public long countActiveMemberships(Long roleId) {
        return roleDao.countActiveMembershipsByRoleId(roleId);
    }

    private Role getRoleOrThrow(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Invalid role ID.");
        }

        return roleDao.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("Role not found.")
                );
    }

    private String normalizeRoleName(String roleName) {

        if (roleName == null || roleName.isBlank()) {
            throw new IllegalArgumentException("Role name is required.");
        }

        String name = roleName.trim()
                .toUpperCase()
                .replaceAll("[\\s-]+", "_");

        if (!name.startsWith("ROLE_")) {
            name = "ROLE_" + name;
        }

        return name;
    }

    private void validateRoleName(String roleName) {

        if (roleName.length() > 60
                || !ROLE_NAME_PATTERN.matcher(roleName).matches()) {

            throw new IllegalArgumentException(
                    "Use a role name containing letters, numbers and underscores. " +
                            "Example: ROLE_ACCOUNTANT."
            );
        }

        if (roleName.equals("ROLE_SUPER_ADMIN")
                || roleName.equals("ROLE_ADMIN")
                || roleName.equals("ROLE_TEACHER")
                || roleName.equals("ROLE_TRANSPORT_MANAGER")
                || roleName.equals("ROLE_PARENT")) {

            throw new IllegalArgumentException(
                    "This role name is reserved for the system."
            );
        }
    }

    private void validateDescription(String description) {

        if (description != null && description.trim().length() > 300) {
            throw new IllegalArgumentException(
                    "Description cannot exceed 300 characters."
            );
        }
    }

    private String cleanDescription(String description) {
        return description == null || description.isBlank()
                ? null
                : description.trim();
    }
}
