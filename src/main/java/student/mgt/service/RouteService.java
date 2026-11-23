package student.mgt.service;

import student.mgt.dto.RouteDTO;

import java.util.List;

public interface RouteService {

    RouteDTO save(RouteDTO routeDTO);

    RouteDTO update(Long id, RouteDTO routeDTO);

    RouteDTO findById(Long id);

    List<RouteDTO> findAll();

    void deleteById(Long id);

    List<RouteDTO> searchRoutes(String keyword);

    long count();
}