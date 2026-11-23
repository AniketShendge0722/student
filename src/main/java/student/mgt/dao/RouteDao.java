package student.mgt.dao;

import student.mgt.entity.Route;
import java.util.List;
import java.util.Optional;

public interface RouteDao {

    Route save(Route route);

    Route update(Route route);

    Optional<Route> findById(Long id);

    List<Route> findAll();

    void deleteById(Long id);

    List<Route> searchRoutes(String keyword);

    boolean existsByRouteName(String routeName);

    boolean existsByRouteNameAndIdNot(String routeName, Long id);

    boolean hasBuses(Long routeId);

    long count();
}