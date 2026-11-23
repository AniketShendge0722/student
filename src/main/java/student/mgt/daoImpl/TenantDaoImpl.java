
package student.mgt.daoImpl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import student.mgt.dao.TenantDao;
import student.mgt.entity.Tenant;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional(readOnly = true)
public class TenantDaoImpl implements TenantDao {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Optional<Tenant> findById(Long id) {
        return Optional.ofNullable(
                entityManager.find(Tenant.class, id)
        );
    }

    @Override
    public List<Tenant> findAll() {
        return entityManager.createQuery(
                        "SELECT t FROM Tenant t ORDER BY t.tenantName ASC",
                        Tenant.class
                )
                .getResultList();
    }

    @Override
    public List<Tenant> findAllActive() {
        return entityManager.createQuery(
                        "SELECT t FROM Tenant t " +
                                "WHERE t.active = true " +
                                "ORDER BY t.tenantName ASC",
                        Tenant.class
                )
                .getResultList();
    }

    @Override
    public boolean existsByTenantCode(String tenantCode) {

        Long count = entityManager.createQuery(
                        "SELECT COUNT(t) FROM Tenant t " +
                                "WHERE LOWER(t.tenantCode) = LOWER(:code)",
                        Long.class
                )
                .setParameter("code", tenantCode.trim())
                .getSingleResult();

        return count > 0;
    }

    @Override
    public boolean existsByTenantCodeAndIdNot(
            String tenantCode,
            Long id) {

        Long count = entityManager.createQuery(
                        "SELECT COUNT(t) FROM Tenant t " +
                                "WHERE LOWER(t.tenantCode) = LOWER(:code) " +
                                "AND t.id <> :id",
                        Long.class
                )
                .setParameter("code", tenantCode.trim())
                .setParameter("id", id)
                .getSingleResult();

        return count > 0;
    }

    @Override
    @Transactional
    public void save(Tenant tenant) {
        entityManager.persist(tenant);
    }

    @Override
    @Transactional
    public void update(Tenant tenant) {
        entityManager.merge(tenant);
    }
}
