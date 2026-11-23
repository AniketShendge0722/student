
package student.mgt.controller;

import jakarta.validation.Valid;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import student.mgt.dto.TenantForm;
import student.mgt.entity.Tenant;
import student.mgt.service.TenantManagementService;

@Controller
@RequestMapping("/admin/tenants")
@PreAuthorize("hasRole('SUPER_ADMIN')")
public class TenantManagementController {

    private final TenantManagementService tenantManagementService;

    public TenantManagementController(
            TenantManagementService tenantManagementService) {
        this.tenantManagementService = tenantManagementService;
    }

    @GetMapping
    public String listTenants(Model model) {

        model.addAttribute(
                "tenants",
                tenantManagementService.findAllTenants()
        );

        return "admin/tenants/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {

        model.addAttribute("tenantForm", new TenantForm());
        model.addAttribute("pageTitle", "Create School");
        model.addAttribute("formAction", "/admin/tenants");

        return "admin/tenants/form";
    }

    @GetMapping("/{id}/edit")
    public String editForm(
            @PathVariable Long id,
            Model model,
            RedirectAttributes redirectAttributes) {

        Tenant tenant = tenantManagementService.findTenantById(id)
                .orElse(null);

        if (tenant == null) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "School not found."
            );
            return "redirect:/admin/tenants";
        }

        TenantForm form = new TenantForm();
        form.setId(tenant.getId());
        form.setTenantName(tenant.getTenantName());
        form.setTenantCode(tenant.getTenantCode());
        form.setAddress(tenant.getAddress());
        form.setContactEmail(tenant.getContactEmail());
        form.setContactPhone(tenant.getContactPhone());
        form.setActive(tenant.isActive());

        model.addAttribute("tenantForm", form);
        model.addAttribute("pageTitle", "Edit School");
        model.addAttribute("formAction", "/admin/tenants");

        return "admin/tenants/form";
    }

    @PostMapping
    public String saveTenant(
            @Valid @ModelAttribute("tenantForm") TenantForm form,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

        boolean editing = form.getId() != null;

        if (bindingResult.hasErrors()) {
            model.addAttribute(
                    "pageTitle",
                    editing ? "Edit School" : "Create School"
            );
            model.addAttribute("formAction", "/admin/tenants");
            return "admin/tenants/form";
        }

        try {

            if (editing) {
                tenantManagementService.updateTenant(
                        form.getId(),
                        form
                );

                redirectAttributes.addFlashAttribute(
                        "successMessage",
                        "School details updated successfully."
                );

            } else {
                tenantManagementService.createTenant(form);

                redirectAttributes.addFlashAttribute(
                        "successMessage",
                        "School created successfully."
                );
            }

            return "redirect:/admin/tenants";

        } catch (IllegalArgumentException ex) {

            bindingResult.reject(
                    "tenant.error",
                    ex.getMessage()
            );

            model.addAttribute(
                    "pageTitle",
                    editing ? "Edit School" : "Create School"
            );
            model.addAttribute("formAction", "/admin/tenants");

            return "admin/tenants/form";
        }
    }

    @PostMapping("/{id}/status")
    public String updateTenantStatus(
            @PathVariable Long id,
            @RequestParam boolean active,
            RedirectAttributes redirectAttributes) {

        try {

            tenantManagementService.updateTenantStatus(id, active);

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    active
                            ? "School activated successfully."
                            : "School deactivated successfully."
            );

        } catch (IllegalArgumentException ex) {

            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    ex.getMessage()
            );
        }

        return "redirect:/admin/tenants";
    }
}
