
package student.mgt.service;

import student.mgt.dto.TenantForm;
import student.mgt.entity.Tenant;

import java.util.List;
import java.util.Optional;

public interface TenantManagementService {

    List<Tenant> findAllTenants();

    Optional<Tenant> findTenantById(Long id);

    void createTenant(TenantForm form);

    void updateTenant(Long id, TenantForm form);

    void updateTenantStatus(Long id, boolean active);
}
