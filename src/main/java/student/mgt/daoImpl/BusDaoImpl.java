package student.mgt.daoImpl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import student.mgt.dao.BusDao;
import student.mgt.entity.Bus;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public class BusDaoImpl implements BusDao {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Bus save(Bus bus) {

        entityManager.persist(bus);
        entityManager.flush();

        return bus;
    }

    @Override
    public Bus update(Bus bus) {

        Bus updated = entityManager.merge(bus);
        entityManager.flush();

        return updated;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Bus> findById(Long id) {

        return Optional.ofNullable(
                entityManager.find(Bus.class, id)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<Bus> findAll() {

        return entityManager.createQuery(
                "SELECT DISTINCT b FROM Bus b " +
                        "JOIN FETCH b.route " +
                        "LEFT JOIN FETCH b.students " +
                        "ORDER BY b.id DESC",
                Bus.class
        ).getResultList();
    }

    @Override
    public void deleteById(Long id) {

        Bus bus = entityManager.find(Bus.class, id);

        if (bus != null) {
            entityManager.remove(bus);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Bus> findByBusNumber(String busNumber) {

        List<Bus> result = entityManager.createQuery(
                        "SELECT b FROM Bus b " +
                                "WHERE UPPER(b.busNumber) = UPPER(:busNumber)",
                        Bus.class
                ).setParameter("busNumber", busNumber)
                .getResultList();

        return result.stream().findFirst();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByBusNumber(String busNumber) {

        Long count = entityManager.createQuery(
                        "SELECT COUNT(b) FROM Bus b " +
                                "WHERE UPPER(b.busNumber) = UPPER(:busNumber)",
                        Long.class
                ).setParameter("busNumber", busNumber)
                .getSingleResult();

        return count > 0;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByBusNumberAndIdNot(
            String busNumber, Long id) {

        Long count = entityManager.createQuery(
                        "SELECT COUNT(b) FROM Bus b " +
                                "WHERE UPPER(b.busNumber) = UPPER(:busNumber) " +
                                "AND b.id <> :id",
                        Long.class
                ).setParameter("busNumber", busNumber)
                .setParameter("id", id)
                .getSingleResult();

        return count > 0;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Bus> findByRouteId(Long routeId) {

        return entityManager.createQuery(
                        "SELECT DISTINCT b FROM Bus b " +
                                "JOIN FETCH b.route " +
                                "LEFT JOIN FETCH b.students " +
                                "WHERE b.route.id = :routeId " +
                                "ORDER BY b.busNumber",
                        Bus.class
                ).setParameter("routeId", routeId)
                .getResultList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Bus> searchBuses(String keyword) {

        if (keyword == null || keyword.isBlank()) {
            return findAll();
        }

        return entityManager.createQuery(
                "SELECT DISTINCT b FROM Bus b " +
                        "JOIN FETCH b.route " +
                        "LEFT JOIN FETCH b.students " +
                        "WHERE LOWER(b.busNumber) LIKE :keyword " +
                        "OR LOWER(b.driverName) LIKE :keyword " +
                        "OR b.driverPhone LIKE :keyword " +
                        "OR LOWER(b.route.routeName) LIKE :keyword " +
                        "ORDER BY b.id DESC",
                Bus.class
        ).setParameter(
                "keyword",
                "%" + keyword.trim().toLowerCase() + "%"
        ).getResultList();
    }

    @Override
    @Transactional(readOnly = true)
    public long count() {

        return entityManager.createQuery(
                "SELECT COUNT(b) FROM Bus b",
                Long.class
        ).getSingleResult();
    }

    @Override
    @Transactional(readOnly = true)
    public long countStudentsAssignedToBus(Long busId) {

        return entityManager.createQuery(
                        "SELECT COUNT(s) FROM Student s " +
                                "WHERE s.bus.id = :busId",
                        Long.class
                ).setParameter("busId", busId)
                .getSingleResult();
    }
}