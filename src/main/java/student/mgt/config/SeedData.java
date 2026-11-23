
package student.mgt.config;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.support.TransactionTemplate;

import org.springframework.security.crypto.password.PasswordEncoder;

import student.mgt.entity.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Configuration
public class SeedData {

    @PersistenceContext
    private EntityManager entityManager;

    @Value("${app.security.admin.username:admin}")
    private String adminUsername;

    @Value("${app.security.admin.password}")
    private String adminPassword;

    private final PasswordEncoder passwordEncoder;
    private final TransactionTemplate transactionTemplate;

    public SeedData(
            PasswordEncoder passwordEncoder,
            TransactionTemplate transactionTemplate) {

        this.passwordEncoder = passwordEncoder;
        this.transactionTemplate = transactionTemplate;
    }

    @Bean
    public CommandLineRunner initializeSchoolData() {
        return args -> transactionTemplate.executeWithoutResult(
                status -> initialize()
        );
    }

    public void initialize() {

        AppUser admin = seedAdminUser();

        seedClasses();
        seedSections();
        seedBloodGroups();
        seedAcademicYear();
        seedDefaultRoute();
        seedDefaultBus();

        Tenant defaultTenant = seedDefaultTenant();

        List<Role> roles = seedSystemRoles();
        List<Permission> permissions = seedPermissions();

        seedRolePermissions(roles, permissions);

        seedInitialSuperAdmin(admin, defaultTenant, roles);
    }

    // --------------------------------------------------
    // ADMIN USER
    // --------------------------------------------------

    private AppUser seedAdminUser() {

        List<AppUser> users = entityManager.createQuery(
                        "SELECT u FROM AppUser u WHERE u.username = :username",
                        AppUser.class
                )
                .setParameter("username", adminUsername)
                .getResultList();

        if (!users.isEmpty()) {
            // Never modify an existing user's password or legacy role.
            return users.get(0);
        }

        AppUser user = new AppUser();

        user.setUsername(adminUsername);
        user.setPassword(passwordEncoder.encode(adminPassword));
        user.setRole("ROLE_ADMIN");
        user.setEnabled(true);

        entityManager.persist(user);
        entityManager.flush();

        return user;
    }

    // --------------------------------------------------
    // DEFAULT TENANT
    // --------------------------------------------------

    private Tenant seedDefaultTenant() {

        List<Tenant> tenants = entityManager.createQuery(
                        "SELECT t FROM Tenant t WHERE t.tenantCode = :code",
                        Tenant.class
                )
                .setParameter("code", "SCHOOL001")
                .getResultList();

        if (!tenants.isEmpty()) {
            return tenants.get(0);
        }

        List<Tenant> sameName = entityManager.createQuery(
                        "SELECT t FROM Tenant t WHERE t.tenantName = :name",
                        Tenant.class
                )
                .setParameter("name", "SchoolTrack Main School")
                .getResultList();

        if (!sameName.isEmpty()) {
            throw new IllegalStateException(
                    "A school named SchoolTrack Main School already exists " +
                            "with a different school code. Please verify the tenant " +
                            "record before continuing."
            );
        }

        Tenant tenant = new Tenant(
                "SchoolTrack Main School",
                "SCHOOL001"
        );

        entityManager.persist(tenant);
        entityManager.flush();

        return tenant;
    }

    // --------------------------------------------------
    // SYSTEM ROLES
    // --------------------------------------------------

    private List<Role> seedSystemRoles() {

        String[][] roleData = {
                {"ROLE_SUPER_ADMIN", "Full system administration access"},
                {"ROLE_ADMIN", "School administration access"},
                {"ROLE_TEACHER", "Teacher access"},
                {"ROLE_TRANSPORT_MANAGER", "School transport management access"},
                {"ROLE_PARENT", "Parent access"}
        };

        List<Role> roles = new ArrayList<>();

        for (String[] data : roleData) {

            List<Role> existing = entityManager.createQuery(
                            "SELECT r FROM Role r WHERE r.roleName = :name",
                            Role.class
                    )
                    .setParameter("name", data[0])
                    .getResultList();

            Role role;

            if (existing.isEmpty()) {

                role = new Role(
                        data[0],
                        data[1],
                        true
                );

                entityManager.persist(role);

            } else {
                role = existing.get(0);
            }

            roles.add(role);
        }

        entityManager.flush();

        return roles;
    }

    // --------------------------------------------------
    // PERMISSION CATALOGUE
    // --------------------------------------------------

    private List<Permission> seedPermissions() {

        String[][] permissionData = {

                // Students
                {"STUDENT_VIEW", "View Students", "Students"},
                {"STUDENT_CREATE", "Create Students", "Students"},
                {"STUDENT_UPDATE", "Update Students", "Students"},
                {"STUDENT_DELETE", "Delete Students", "Students"},

                // Parents
                {"PARENT_VIEW", "View Parents", "Parents"},
                {"PARENT_CREATE", "Create Parents", "Parents"},
                {"PARENT_UPDATE", "Update Parents", "Parents"},
                {"PARENT_DELETE", "Delete Parents", "Parents"},

                // Attendance
                {"ATTENDANCE_VIEW", "View Attendance", "Attendance"},
                {"ATTENDANCE_MARK", "Mark Attendance", "Attendance"},
                {"ATTENDANCE_UPDATE", "Update Attendance", "Attendance"},

                // Buses
                {"BUS_VIEW", "View Buses", "Buses"},
                {"BUS_CREATE", "Create Buses", "Buses"},
                {"BUS_UPDATE", "Update Buses", "Buses"},
                {"BUS_DELETE", "Delete Buses", "Buses"},

                // Routes
                {"ROUTE_VIEW", "View Routes", "Routes"},
                {"ROUTE_CREATE", "Create Routes", "Routes"},
                {"ROUTE_UPDATE", "Update Routes", "Routes"},
                {"ROUTE_DELETE", "Delete Routes", "Routes"},

                // Administration
                {"USER_MANAGE", "Manage Users", "Administration"},
                {"ROLE_MANAGE", "Manage Roles", "Administration"},
                {"PERMISSION_MANAGE", "Manage Permissions", "Administration"},
                {"TENANT_MANAGE", "Manage Schools", "Administration"}
        };

        List<Permission> permissions = new ArrayList<>();

        for (String[] data : permissionData) {

            List<Permission> existing = entityManager.createQuery(
                            "SELECT p FROM Permission p " +
                                    "WHERE p.permissionCode = :code",
                            Permission.class
                    )
                    .setParameter("code", data[0])
                    .getResultList();

            Permission permission;

            if (existing.isEmpty()) {

                permission = new Permission(
                        data[0],
                        data[1],
                        data[2],
                        data[1]
                );

                entityManager.persist(permission);

            } else {
                permission = existing.get(0);
            }

            permissions.add(permission);
        }

        entityManager.flush();

        return permissions;
    }

    // --------------------------------------------------
    // ROLE-PERMISSION ASSIGNMENTS
    // --------------------------------------------------

    private void seedRolePermissions(
            List<Role> roles,
            List<Permission> permissions) {

        for (Role role : roles) {

            for (Permission permission : permissions) {

                if (!shouldAssignPermission(
                        role.getRoleName(),
                        permission.getPermissionCode())) {
                    continue;
                }

                Long count = entityManager.createQuery(
                                "SELECT COUNT(rp) FROM RolePermission rp " +
                                        "WHERE rp.role.id = :roleId " +
                                        "AND rp.permission.id = :permissionId",
                                Long.class
                        )
                        .setParameter("roleId", role.getId())
                        .setParameter("permissionId", permission.getId())
                        .getSingleResult();

                if (count == 0) {
                    entityManager.persist(
                            new RolePermission(role, permission)
                    );
                }
            }
        }

        entityManager.flush();
    }

    private boolean shouldAssignPermission(
            String roleName,
            String permissionCode) {

        // Super Admin receives the complete permission catalogue.
        if ("ROLE_SUPER_ADMIN".equals(roleName)) {
            return true;
        }

        // School Admin: operational and school-level administration.
        if ("ROLE_ADMIN".equals(roleName)) {
            return !permissionCode.equals("TENANT_MANAGE")
                    && !permissionCode.equals("PERMISSION_MANAGE");
        }

        // Teacher: student viewing and attendance.
        if ("ROLE_TEACHER".equals(roleName)) {
            return List.of(
                    "STUDENT_VIEW",
                    "PARENT_VIEW",
                    "ATTENDANCE_VIEW",
                    "ATTENDANCE_MARK",
                    "ATTENDANCE_UPDATE"
            ).contains(permissionCode);
        }

        // Transport Manager: student and transport operations.
        if ("ROLE_TRANSPORT_MANAGER".equals(roleName)) {
            return List.of(
                    "STUDENT_VIEW",
                    "BUS_VIEW",
                    "BUS_CREATE",
                    "BUS_UPDATE",
                    "BUS_DELETE",
                    "ROUTE_VIEW",
                    "ROUTE_CREATE",
                    "ROUTE_UPDATE",
                    "ROUTE_DELETE"
            ).contains(permissionCode);
        }

        // Parent: basic student and attendance viewing.
        if ("ROLE_PARENT".equals(roleName)) {
            return List.of(
                    "STUDENT_VIEW",
                    "ATTENDANCE_VIEW"
            ).contains(permissionCode);
        }

        return false;
    }

    // --------------------------------------------------
    // INITIAL SUPER ADMIN MEMBERSHIP
    // --------------------------------------------------

    private void seedInitialSuperAdmin(
            AppUser admin,
            Tenant tenant,
            List<Role> roles) {

        Role superAdminRole = roles.stream()
                .filter(role ->
                        "ROLE_SUPER_ADMIN".equals(role.getRoleName()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(
                        "ROLE_SUPER_ADMIN was not initialized."
                ));

        Long count = entityManager.createQuery(
                        "SELECT COUNT(utr) FROM UserTenantRole utr " +
                                "WHERE utr.user.id = :userId " +
                                "AND utr.tenant.id = :tenantId " +
                                "AND utr.role.id = :roleId",
                        Long.class
                )
                .setParameter("userId", admin.getId())
                .setParameter("tenantId", tenant.getId())
                .setParameter("roleId", superAdminRole.getId())
                .getSingleResult();

        if (count == 0) {

            UserTenantRole membership = new UserTenantRole(
                    admin,
                    tenant,
                    superAdminRole
            );

            membership.setActive(true);

            entityManager.persist(membership);
            entityManager.flush();
        }
    }

    // --------------------------------------------------
    // EXISTING SCHOOL DATA - PRESERVED
    // --------------------------------------------------

    private void seedClasses() {

        List<String> classes = List.of(
                "Nursery",
                "Jr. KG",
                "Sr. KG",
                "1st Standard",
                "2nd Standard",
                "3rd Standard",
                "4th Standard",
                "5th Standard",
                "6th Standard",
                "7th Standard",
                "8th Standard",
                "9th Standard",
                "10th Standard",
                "11th Standard",
                "12th Standard"
        );

        for (String className : classes) {

            Long count = entityManager.createQuery(
                            "SELECT COUNT(c) FROM SchoolClass c " +
                                    "WHERE c.className = :name",
                            Long.class
                    )
                    .setParameter("name", className)
                    .getSingleResult();

            if (count == 0) {
                entityManager.persist(new SchoolClass(className));
            }
        }
    }

    private void seedSections() {

        for (String sectionName : List.of("A", "B", "C", "D")) {

            Long count = entityManager.createQuery(
                            "SELECT COUNT(s) FROM Section s " +
                                    "WHERE s.sectionName = :name",
                            Long.class
                    )
                    .setParameter("name", sectionName)
                    .getSingleResult();

            if (count == 0) {
                entityManager.persist(new Section(sectionName));
            }
        }
    }

    private void seedBloodGroups() {

        for (String group : List.of(
                "A+", "A-", "B+", "B-",
                "AB+", "AB-", "O+", "O-"
        )) {

            Long count = entityManager.createQuery(
                            "SELECT COUNT(b) FROM BloodGroup b " +
                                    "WHERE b.bloodGroup = :group",
                            Long.class
                    )
                    .setParameter("group", group)
                    .getSingleResult();

            if (count == 0) {
                entityManager.persist(new BloodGroup(group));
            }
        }
    }

    private void seedAcademicYear() {

        Long count = entityManager.createQuery(
                        "SELECT COUNT(a) FROM AcademicYear a " +
                                "WHERE a.yearName = :year",
                        Long.class
                )
                .setParameter("year", "2026-2027")
                .getSingleResult();

        if (count == 0) {

            AcademicYear academicYear = new AcademicYear(
                    "2026-2027",
                    LocalDate.of(2026, 6, 1),
                    LocalDate.of(2027, 4, 30),
                    "ACTIVE"
            );

            entityManager.persist(academicYear);
        }
    }

    private void seedDefaultRoute() {

        Long count = entityManager.createQuery(
                        "SELECT COUNT(r) FROM Route r " +
                                "WHERE r.routeName = :name",
                        Long.class
                )
                .setParameter("name", "Route 1")
                .getSingleResult();

        if (count == 0) {

            Route route = new Route();

            route.setRouteName("Route 1");
            route.setStartPoint("School");
            route.setEndPoint("Main City Area");
            route.setStops("School, Main Road, City Area");
            route.setDistanceKm(10.0);

            entityManager.persist(route);
        }
    }

    private void seedDefaultBus() {

        Long count = entityManager.createQuery(
                        "SELECT COUNT(b) FROM Bus b " +
                                "WHERE b.busNumber = :number",
                        Long.class
                )
                .setParameter("number", "MH 12 AB 1234")
                .getSingleResult();

        if (count == 0) {

            Route route = entityManager.createQuery(
                            "SELECT r FROM Route r " +
                                    "WHERE r.routeName = :name",
                            Route.class
                    )
                    .setParameter("name", "Route 1")
                    .getSingleResult();

            Bus bus = new Bus();

            bus.setBusNumber("MH 12 AB 1234");
            bus.setDriverName("Default Driver");
            bus.setDriverPhone("9999999999");
            bus.setCapacity(40);
            bus.setRoute(route);

            entityManager.persist(bus);
        }
    }
}
