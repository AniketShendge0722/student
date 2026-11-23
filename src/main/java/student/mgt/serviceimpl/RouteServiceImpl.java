package student.mgt.serviceimpl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import student.mgt.dao.RouteDao;
import student.mgt.dto.RouteDTO;
import student.mgt.entity.Route;
import student.mgt.mapper.StudentMapper;
import student.mgt.service.RouteService;

import student.mgt.exception.ResourceNotFoundException;
import student.mgt.exception.DuplicateResourceException;

import java.util.List;

@Service
@Transactional
public class RouteServiceImpl implements RouteService {

    private final RouteDao routeDao;
    private final StudentMapper studentMapper;

    public RouteServiceImpl(
            RouteDao routeDao,
            StudentMapper studentMapper) {

        this.routeDao = routeDao;
        this.studentMapper = studentMapper;
    }

    @Override
    public RouteDTO save(RouteDTO dto) {

        if (routeDao.existsByRouteName(dto.getRouteName())) {

            throw new DuplicateResourceException(
                    "Route name already exists: " + dto.getRouteName()
            );
        }

        validateDistance(dto.getDistanceKm());

        Route route = studentMapper.toRouteEntity(dto);

        Route savedRoute = routeDao.save(route);

        return studentMapper.toRouteDTO(savedRoute);
    }

    @Override
    public RouteDTO update(Long id, RouteDTO dto) {

        Route existingRoute = routeDao.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Route not found with ID: " + id
                        )
                );

        if (routeDao.existsByRouteNameAndIdNot(
                dto.getRouteName(), id)) {

            throw new DuplicateResourceException(
                    "Route name already exists: " + dto.getRouteName()
            );
        }

        validateDistance(dto.getDistanceKm());

        studentMapper.updateRouteEntity(existingRoute, dto);

        Route updatedRoute = routeDao.update(existingRoute);

        return studentMapper.toRouteDTO(updatedRoute);
    }

    @Override
    @Transactional(readOnly = true)
    public RouteDTO findById(Long id) {

        Route route = routeDao.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Route not found with ID: " + id
                        )
                );

        return studentMapper.toRouteDTO(route);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RouteDTO> findAll() {

        return studentMapper.toRouteDTOList(
                routeDao.findAll()
        );
    }

    @Override
    public void deleteById(Long id) {

        Route route = routeDao.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Route not found with ID: " + id
                        )
                );

        if (routeDao.hasBuses(id)) {

            throw new DuplicateResourceException(
                    "Cannot delete route '" + route.getRouteName()
                            + "' because buses are assigned to it. "
                            + "Reassign or remove the buses first."
            );
        }

        routeDao.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RouteDTO> searchRoutes(String keyword) {

        return studentMapper.toRouteDTOList(
                routeDao.searchRoutes(keyword)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public long count() {
        return routeDao.count();
    }

    private void validateDistance(Double distanceKm) {

        if (distanceKm == null || distanceKm <= 0) {

            throw new IllegalArgumentException(
                    "Route distance must be greater than zero."
            );
        }
    }
}