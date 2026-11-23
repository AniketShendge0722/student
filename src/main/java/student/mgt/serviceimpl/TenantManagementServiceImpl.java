
package student.mgt.serviceimpl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import student.mgt.dao.TenantDao;
import student.mgt.dao.UserTenantRoleDao;
import student.mgt.dto.TenantForm;
import student.mgt.entity.Tenant;
import student.mgt.service.TenantManagementService;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class TenantManagementServiceImpl
        implements TenantManagementService {

    private final TenantDao tenantDao;
    private final UserTenantRoleDao userTenantRoleDao;

    public TenantManagementServiceImpl(
            TenantDao tenantDao,
            UserTenantRoleDao userTenantRoleDao) {

        this.tenantDao = tenantDao;
        this.userTenantRoleDao = userTenantRoleDao;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Tenant> findAllTenants() {
        return tenantDao.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Tenant> findTenantById(Long id) {

        if (id == null || id <= 0) {
            return Optional.empty();
        }

        return tenantDao.findById(id);
    }

    @Override
    public void createTenant(TenantForm form) {

        validateForm(form);

        String code = normalizeCode(form.getTenantCode());

        if (tenantDao.existsByTenantCode(code)) {
            throw new IllegalArgumentException(
                    "School code already exists"
            );
        }

        Tenant tenant = new Tenant();

        copyFormToTenant(form, tenant);

        tenant.setTenantCode(code);
        tenant.setActive(true);

        tenantDao.save(tenant);
    }

    @Override
    public void updateTenant(Long id, TenantForm form) {

        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Invalid school ID");
        }

        validateForm(form);

        Tenant tenant = tenantDao.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("School not found")
                );

        String code = normalizeCode(form.getTenantCode());

        if (tenantDao.existsByTenantCodeAndIdNot(code, id)) {
            throw new IllegalArgumentException(
                    "School code already exists"
            );
        }

        copyFormToTenant(form, tenant);

        tenant.setTenantCode(code);

        // Preserve the current active status.
        // Status is managed separately.
        tenantDao.update(tenant);
    }

    @Override
    public void updateTenantStatus(Long id, boolean active) {

        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Invalid school ID");
        }

        Tenant tenant = tenantDao.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("School not found")
                );

        if (!active && tenant.isActive()) {

            long activeMembershipCount =
                    userTenantRoleDao.countActiveMembershipsByTenantId(id);

            if (activeMembershipCount > 0) {
                throw new IllegalArgumentException(
                        "Cannot deactivate this school because " +
                                "active user memberships are assigned to it. " +
                                "Remove or deactivate those memberships first."
                );
            }

            long activeTenantCount = tenantDao.findAllActive().size();

            if (activeTenantCount <= 1) {
                throw new IllegalArgumentException(
                        "Cannot deactivate the last active school"
                );
            }
        }

        tenant.setActive(active);

        tenantDao.update(tenant);
    }

    private void validateForm(TenantForm form) {

        if (form == null) {
            throw new IllegalArgumentException(
                    "School information is required"
            );
        }

        if (form.getTenantName() == null
                || form.getTenantName().isBlank()) {

            throw new IllegalArgumentException(
                    "School name is required"
            );
        }

        if (form.getTenantCode() == null
                || form.getTenantCode().isBlank()) {

            throw new IllegalArgumentException(
                    "School code is required"
            );
        }

        if (form.getTenantName().trim().length() > 150) {
            throw new IllegalArgumentException(
                    "School name cannot exceed 150 characters"
            );
        }

        if (form.getTenantCode().trim().length() < 3
                || form.getTenantCode().trim().length() > 50) {

            throw new IllegalArgumentException(
                    "School code must be between 3 and 50 characters"
            );
        }

        if (!form.getTenantCode().trim()
                .matches("^[A-Za-z0-9_-]+$")) {

            throw new IllegalArgumentException(
                    "Invalid school code"
            );
        }

        if (form.getAddress() != null
                && form.getAddress().length() > 500) {

            throw new IllegalArgumentException(
                    "Address cannot exceed 500 characters"
            );
        }

        if (form.getContactEmail() != null
                && !form.getContactEmail().isBlank()
                && !form.getContactEmail().matches(
                "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {

            throw new IllegalArgumentException(
                    "Enter a valid email address"
            );
        }

        if (form.getContactPhone() != null
                && !form.getContactPhone().matches(
                "^[0-9+() -]{0,15}$")) {

            throw new IllegalArgumentException(
                    "Enter a valid contact phone number"
            );
        }
    }

    private void copyFormToTenant(
            TenantForm form,
            Tenant tenant) {

        tenant.setTenantName(form.getTenantName().trim());

        tenant.setAddress(
                normalizeOptional(form.getAddress())
        );

        tenant.setContactEmail(
                normalizeOptional(form.getContactEmail())
        );

        tenant.setContactPhone(
                normalizeOptional(form.getContactPhone())
        );
    }

    private String normalizeCode(String code) {
        return code.trim().toUpperCase();
    }

    private String normalizeOptional(String value) {

        if (value == null || value.isBlank()) {
            return null;
        }

        return value.trim();
    }
}
