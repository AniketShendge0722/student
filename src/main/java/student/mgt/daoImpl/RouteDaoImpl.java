package student.mgt.daoImpl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import student.mgt.dao.RouteDao;
import student.mgt.entity.Route;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public class RouteDaoImpl implements RouteDao {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Route save(Route route) {

        entityManager.persist(route);
        entityManager.flush();

        return route;
    }

    @Override
    public Route update(Route route) {

        Route updated = entityManager.merge(route);
        entityManager.flush();

        return updated;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Route> findById(Long id) {

        return Optional.ofNullable(
                entityManager.find(Route.class, id)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<Route> findAll() {

        return entityManager.createQuery(
                "SELECT DISTINCT r FROM Route r " +
                        "LEFT JOIN FETCH r.buses " +
                        "ORDER BY r.id DESC",
                Route.class
        ).getResultList();
    }

    @Override
    public void deleteById(Long id) {

        Route route = entityManager.find(Route.class, id);

        if (route != null) {
            entityManager.remove(route);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Route> searchRoutes(String keyword) {

        if (keyword == null || keyword.isBlank()) {
            return findAll();
        }

        return entityManager.createQuery(
                "SELECT DISTINCT r FROM Route r " +
                        "LEFT JOIN FETCH r.buses " +
                        "WHERE LOWER(r.routeName) LIKE :keyword " +
                        "OR LOWER(r.startPoint) LIKE :keyword " +
                        "OR LOWER(r.endPoint) LIKE :keyword " +
                        "OR LOWER(r.stops) LIKE :keyword " +
                        "ORDER BY r.id DESC",
                Route.class
        ).setParameter(
                "keyword",
                "%" + keyword.trim().toLowerCase() + "%"
        ).getResultList();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByRouteName(String routeName) {

        Long count = entityManager.createQuery(
                        "SELECT COUNT(r) FROM Route r " +
                                "WHERE LOWER(r.routeName) = LOWER(:routeName)",
                        Long.class
                ).setParameter("routeName", routeName)
                .getSingleResult();

        return count > 0;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByRouteNameAndIdNot(
            String routeName, Long id) {

        Long count = entityManager.createQuery(
                        "SELECT COUNT(r) FROM Route r " +
                                "WHERE LOWER(r.routeName) = LOWER(:routeName) " +
                                "AND r.id <> :id",
                        Long.class
                ).setParameter("routeName", routeName)
                .setParameter("id", id)
                .getSingleResult();

        return count > 0;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasBuses(Long routeId) {

        Long count = entityManager.createQuery(
                        "SELECT COUNT(b) FROM Bus b " +
                                "WHERE b.route.id = :routeId",
                        Long.class
                ).setParameter("routeId", routeId)
                .getSingleResult();

        return count > 0;
    }

    @Override
    @Transactional(readOnly = true)
    public long count() {

        return entityManager.createQuery(
                "SELECT COUNT(r) FROM Route r",
                Long.class
        ).getSingleResult();
    }
}