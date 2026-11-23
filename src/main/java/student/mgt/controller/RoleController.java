
package student.mgt.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import student.mgt.entity.Role;
import student.mgt.service.RoleService;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/admin/roles")
@PreAuthorize("hasRole('SUPER_ADMIN')")
public class RoleController {

    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @GetMapping
    public String listRoles(Model model) {

        var roles = roleService.findAll();

        Map<Long, Long> membershipCounts = new HashMap<>();

        for (Role role : roles) {
            membershipCounts.put(
                    role.getId(),
                    roleService.countActiveMemberships(role.getId())
            );
        }

        model.addAttribute("roles", roles);
        model.addAttribute("membershipCounts", membershipCounts);

        return "admin/roles/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {

        model.addAttribute("role", new Role());
        model.addAttribute("pageHeading", "Create Role");
        model.addAttribute("formAction", "/admin/roles/save");

        return "admin/roles/form";
    }

    @GetMapping("/{id}/edit")
    public String editForm(
            @PathVariable Long id,
            Model model,
            RedirectAttributes redirectAttributes) {

        Role role = roleService.findById(id).orElse(null);

        if (role == null) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "Role not found."
            );

            return "redirect:/admin/roles";
        }

        if (role.isSystemRole()) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "System roles cannot be edited."
            );

            return "redirect:/admin/roles";
        }

        model.addAttribute("role", role);
        model.addAttribute("pageHeading", "Edit Role");
        model.addAttribute("formAction", "/admin/roles/update/" + id);

        return "admin/roles/form";
    }

    @PostMapping("/save")
    public String saveRole(
            @RequestParam String roleName,
            @RequestParam(required = false) String description,
            RedirectAttributes redirectAttributes) {

        try {
            roleService.createRole(roleName, description);

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Role created successfully."
            );

        } catch (IllegalArgumentException exception) {

            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    exception.getMessage()
            );

            return "redirect:/admin/roles/new";
        }

        return "redirect:/admin/roles";
    }

    @PostMapping("/update/{id}")
    public String updateRole(
            @PathVariable Long id,
            @RequestParam String roleName,
            @RequestParam(required = false) String description,
            RedirectAttributes redirectAttributes) {

        try {
            roleService.updateRole(id, roleName, description);

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Role updated successfully."
            );

        } catch (IllegalArgumentException exception) {

            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    exception.getMessage()
            );

            return "redirect:/admin/roles/" + id + "/edit";
        }

        return "redirect:/admin/roles";
    }

    @PostMapping("/{id}/toggle")
    public String toggleRole(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {

        try {
            Role role = roleService.toggleActive(id);

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    role.isActive()
                            ? "Role activated successfully."
                            : "Role deactivated successfully."
            );

        } catch (IllegalArgumentException exception) {

            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    exception.getMessage()
            );
        }

        return "redirect:/admin/roles";
    }
}
