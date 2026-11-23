package student.mgt.service;

import student.mgt.dto.BusDTO;

import java.util.List;

public interface BusService {

    BusDTO save(BusDTO busDTO);

    BusDTO update(Long id, BusDTO busDTO);

    BusDTO findById(Long id);

    List<BusDTO> findAll();

    void deleteById(Long id);

    BusDTO findByBusNumber(String busNumber);

    List<BusDTO> findByRouteId(Long routeId);

    List<BusDTO> searchBuses(String keyword);

    long count();

    long countStudentsAssignedToBus(Long busId);
}