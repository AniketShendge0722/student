
package student.mgt.controller;

import jakarta.validation.Valid;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import student.mgt.dto.UserForm;
import student.mgt.dto.UserTenantRoleForm;
import student.mgt.entity.AppUser;
import student.mgt.entity.UserTenantRole;
import student.mgt.service.UserManagementService;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin/users")
@PreAuthorize("hasRole('SUPER_ADMIN')")
public class UserManagementController {

    private final UserManagementService userManagementService;

    public UserManagementController(
            UserManagementService userManagementService) {
        this.userManagementService = userManagementService;
    }

    // -------------------------------------------------
    // USER LIST
    // -------------------------------------------------

    @GetMapping
    public String listUsers(Model model) {

        List<AppUser> users =
                userManagementService.findAllUsers();

        Map<Long, Long> membershipCounts = new HashMap<>();

        for (AppUser user : users) {
            membershipCounts.put(
                    user.getId(),
                    userManagementService
                            .findMemberships(user.getId())
                            .stream()
                            .filter(UserTenantRole::isActive)
                            .count()
            );
        }

        model.addAttribute("users", users);
        model.addAttribute("membershipCounts", membershipCounts);

        return "admin/users/list";
    }

    // -------------------------------------------------
    // CREATE USER
    // -------------------------------------------------

    @GetMapping("/create")
    public String showCreateForm(Model model) {

        UserForm form = new UserForm();

        form.getAssignments().add(new UserTenantRoleForm());

        model.addAttribute("userForm", form);
        populateAssignmentOptions(model);

        return "admin/users/form";
    }

    @PostMapping("/create")
    public String createUser(
            @Valid @ModelAttribute("userForm") UserForm form,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            populateAssignmentOptions(model);
            return "admin/users/form";
        }

        try {
            userManagementService.createUser(form);

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "User created successfully"
            );

            return "redirect:/admin/users";

        } catch (IllegalArgumentException exception) {

            model.addAttribute(
                    "errorMessage",
                    exception.getMessage()
            );

            populateAssignmentOptions(model);

            return "admin/users/form";
        }
    }

    // -------------------------------------------------
    // EDIT USER
    // -------------------------------------------------

    @GetMapping("/edit/{id}")
    public String showEditForm(
            @PathVariable Long id,
            Model model,
            RedirectAttributes redirectAttributes) {

        AppUser user = userManagementService.findUserById(id)
                .orElse(null);

        if (user == null) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "User not found"
            );

            return "redirect:/admin/users";
        }

        UserForm form = new UserForm();

        form.setId(user.getId());
        form.setUsername(user.getUsername());

        List<UserTenantRoleForm> assignments =
                new ArrayList<>();

        for (UserTenantRole membership :
                userManagementService.findMemberships(id)) {

            if (!membership.isActive()) {
                continue;
            }

            UserTenantRoleForm assignment =
                    new UserTenantRoleForm();

            assignment.setTenantId(
                    membership.getTenant().getId()
            );

            assignment.setRoleId(
                    membership.getRole().getId()
            );

            assignments.add(assignment);
        }

        if (assignments.isEmpty()) {
            assignments.add(new UserTenantRoleForm());
        }

        form.setAssignments(assignments);

        model.addAttribute("userForm", form);
        model.addAttribute("editingUser", true);
        model.addAttribute("enabled", user.isEnabled());

        populateAssignmentOptions(model);

        return "admin/users/form";
    }

    @PostMapping("/edit/{id}")
    public String updateUser(
            @PathVariable Long id,
            @Valid @ModelAttribute("userForm") UserForm form,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("editingUser", true);
            populateAssignmentOptions(model);
            return "admin/users/form";
        }

        try {
            userManagementService.updateUser(id, form);

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "User updated successfully"
            );

            return "redirect:/admin/users";

        } catch (IllegalArgumentException exception) {

            model.addAttribute(
                    "errorMessage",
                    exception.getMessage()
            );

            model.addAttribute("editingUser", true);
            populateAssignmentOptions(model);

            return "admin/users/form";
        }
    }

    // -------------------------------------------------
    // ACTIVATE / DEACTIVATE
    // -------------------------------------------------

    @PostMapping("/{id}/status")
    public String updateUserStatus(
            @PathVariable Long id,
            @RequestParam boolean enabled,
            Principal principal,
            RedirectAttributes redirectAttributes) {

        try {
            userManagementService.updateUserStatus(
                    id,
                    enabled,
                    principal != null ? principal.getName() : null
            );

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    enabled
                            ? "User activated successfully"
                            : "User deactivated successfully"
            );

        } catch (IllegalArgumentException exception) {

            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    exception.getMessage()
            );
        }

        return "redirect:/admin/users";
    }

    // -------------------------------------------------
    // RESET PASSWORD
    // -------------------------------------------------

    // -------------------------------------------------
    // RESET PASSWORD
    // -------------------------------------------------

    @GetMapping("/reset-password/{id}")
    public String showResetPasswordForm(
            @PathVariable Long id,
            Model model,
            RedirectAttributes redirectAttributes) {

        AppUser user = userManagementService.findUserById(id)
                .orElse(null);

        if (user == null) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "User not found"
            );

            return "redirect:/admin/users";
        }

        // Must match reset-password.html
        model.addAttribute("targetUser", user);

        return "admin/users/reset-password";
    }

    @PostMapping("/reset-password/{id}")
    public String resetPassword(
            @PathVariable Long id,
            @RequestParam String newPassword,
            @RequestParam String confirmPassword,
            RedirectAttributes redirectAttributes,
            Model model) {

        AppUser user = userManagementService.findUserById(id)
                .orElse(null);

        if (user == null) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "User not found"
            );

            return "redirect:/admin/users";
        }

        if (!newPassword.equals(confirmPassword)) {

            model.addAttribute(
                    "errorMessage",
                    "Passwords do not match"
            );

            model.addAttribute("targetUser", user);

            return "admin/users/reset-password";
        }

        try {
            userManagementService.resetPassword(
                    id,
                    newPassword
            );

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Password reset successfully"
            );

            return "redirect:/admin/users";

        } catch (IllegalArgumentException exception) {

            model.addAttribute(
                    "errorMessage",
                    exception.getMessage()
            );

            model.addAttribute("targetUser", user);

            return "admin/users/reset-password";
        }
    }

    // -------------------------------------------------
    // COMMON FORM OPTIONS
    // -------------------------------------------------

    private void populateAssignmentOptions(Model model) {

        model.addAttribute(
                "tenants",
                userManagementService.findActiveTenants()
        );

        model.addAttribute(
                "roles",
                userManagementService.findActiveRoles()
        );
    }
}
