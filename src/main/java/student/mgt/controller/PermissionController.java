
package student.mgt.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import student.mgt.entity.Role;
import student.mgt.service.PermissionService;
import student.mgt.service.RoleService;

import java.util.Collections;

@Controller
@RequestMapping("/admin/permissions")
@PreAuthorize("hasRole('SUPER_ADMIN')")
public class PermissionController {

    private final PermissionService permissionService;
    private final RoleService roleService;

    public PermissionController(
            PermissionService permissionService,
            RoleService roleService) {

        this.permissionService = permissionService;
        this.roleService = roleService;
    }

    @GetMapping
    public String listPermissions(Model model) {

        model.addAttribute(
                "permissions",
                permissionService.findAll()
        );

        model.addAttribute(
                "roles",
                roleService.findAll()
        );

        return "admin/permissions/list";
    }

    @GetMapping("/roles/{roleId}")
    public String rolePermissions(
            @PathVariable Long roleId,
            Model model,
            RedirectAttributes redirectAttributes) {

        Role role = roleService.findById(roleId).orElse(null);

        if (role == null) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "Role not found."
            );
            return "redirect:/admin/permissions";
        }

        model.addAttribute("role", role);

        model.addAttribute(
                "assignedPermissions",
                permissionService.findByRoleId(roleId)
        );

        if (role.isSystemRole()) {
            model.addAttribute(
                    "availablePermissions",
                    Collections.emptyList()
            );
            model.addAttribute(
                    "protectedRole",
                    true
            );
        } else {
            model.addAttribute(
                    "availablePermissions",
                    permissionService.findAvailableForRole(roleId)
            );
            model.addAttribute(
                    "protectedRole",
                    false
            );
        }

        return "admin/permissions/role-permissions";
    }

    @PostMapping("/roles/{roleId}/assign")
    public String assignPermission(
            @PathVariable Long roleId,
            @RequestParam Long permissionId,
            RedirectAttributes redirectAttributes) {

        try {
            permissionService.assignPermission(roleId, permissionId);

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Permission assigned successfully."
            );

        } catch (IllegalArgumentException exception) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    exception.getMessage()
            );
        }

        return "redirect:/admin/permissions/roles/" + roleId;
    }

    @PostMapping("/roles/{roleId}/remove")
    public String removePermission(
            @PathVariable Long roleId,
            @RequestParam Long permissionId,
            RedirectAttributes redirectAttributes) {

        try {
            permissionService.removePermission(roleId, permissionId);

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Permission removed successfully."
            );

        } catch (IllegalArgumentException exception) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    exception.getMessage()
            );
        }

        return "redirect:/admin/permissions/roles/" + roleId;
    }
}
