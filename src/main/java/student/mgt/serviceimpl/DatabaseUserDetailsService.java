
package student.mgt.serviceimpl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import student.mgt.entity.AppUser;
import student.mgt.entity.UserTenantRole;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Service
public class DatabaseUserDetailsService implements UserDetailsService {

    @PersistenceContext
    private EntityManager entityManager;

    private final TenantMembershipServiceImpl membershipService;

    public DatabaseUserDetailsService(
            TenantMembershipServiceImpl membershipService) {
        this.membershipService = membershipService;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) {

        try {
            AppUser appUser = entityManager.createQuery(
                            "SELECT u FROM AppUser u " +
                                    "WHERE u.username = :username",
                            AppUser.class
                    )
                    .setParameter("username", username.trim())
                    .getSingleResult();

            Set<GrantedAuthority> authorities = new LinkedHashSet<>();

            // Preserve the existing legacy role.
            if (appUser.getRole() != null
                    && !appUser.getRole().isBlank()) {

                authorities.add(
                        new SimpleGrantedAuthority(appUser.getRole())
                );
            }

            // Load active tenant memberships.
            List<UserTenantRole> memberships =
                    membershipService.findActiveMemberships(
                            appUser.getUsername()
                    );

            for (UserTenantRole membership : memberships) {

                if (membership.getRole() == null
                        || !membership.getRole().isActive()) {
                    continue;
                }

                Long tenantId = membership.getTenant().getId();

                String roleName =
                        membership.getRole().getRoleName();

                if (roleName == null || roleName.isBlank()) {
                    continue;
                }

                String authorityName =
                        roleName.startsWith("ROLE_")
                                ? roleName
                                : "ROLE_" + roleName;

                // Grant global Super Admin authority only when
                // an active Super Admin membership exists.
                if ("ROLE_SUPER_ADMIN".equals(authorityName)) {

                    authorities.add(
                            new SimpleGrantedAuthority("ROLE_SUPER_ADMIN")
                    );
                }

                // Preserve tenant-specific role authority.
                authorities.add(
                        new SimpleGrantedAuthority(
                                "TENANT_" + tenantId + "_" + authorityName
                        )
                );

                // Load active permissions for this role.
                List<String> permissionCodes =
                        entityManager.createQuery(
                                        "SELECT DISTINCT p.permissionCode " +
                                                "FROM RolePermission rp " +
                                                "JOIN rp.permission p " +
                                                "WHERE rp.role.id = :roleId " +
                                                "AND p.active = true",
                                        String.class
                                )
                                .setParameter(
                                        "roleId",
                                        membership.getRole().getId()
                                )
                                .getResultList();

                for (String permissionCode : permissionCodes) {

                    if (permissionCode != null
                            && !permissionCode.isBlank()) {

                        authorities.add(
                                new SimpleGrantedAuthority(
                                        "TENANT_" + tenantId + "_" +
                                                permissionCode.trim()
                                )
                        );
                    }
                }
            }

            return User.builder()
                    .username(appUser.getUsername())
                    .password(appUser.getPassword())
                    .authorities(authorities)
                    .disabled(!appUser.isEnabled())
                    .build();

        } catch (NoResultException exception) {

            throw new UsernameNotFoundException(
                    "User not found: " + username
            );
        }
    }
}
