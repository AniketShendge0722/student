
package student.mgt.dao;

import student.mgt.entity.Tenant;

import java.util.List;
import java.util.Optional;

public interface TenantDao {

    Optional<Tenant> findById(Long id);

    List<Tenant> findAll();

    List<Tenant> findAllActive();

    boolean existsByTenantCode(String tenantCode);

    boolean existsByTenantCodeAndIdNot(
            String tenantCode,
            Long id
    );

    void save(Tenant tenant);

    void update(Tenant tenant);
}
