package student.mgt.dao;

import student.mgt.entity.Bus;
import java.util.List;
import java.util.Optional;

public interface BusDao {

    Bus save(Bus bus);

    Bus update(Bus bus);

    Optional<Bus> findById(Long id);

    List<Bus> findAll();

    void deleteById(Long id);

    Optional<Bus> findByBusNumber(String busNumber);

    boolean existsByBusNumber(String busNumber);

    boolean existsByBusNumberAndIdNot(String busNumber, Long id);

    List<Bus> findByRouteId(Long routeId);

    List<Bus> searchBuses(String keyword);

    long count();

    long countStudentsAssignedToBus(Long busId);
}