package student.mgt.serviceimpl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import student.mgt.dao.BusDao;
import student.mgt.dao.RouteDao;

import student.mgt.dto.BusDTO;
import student.mgt.entity.Bus;

import student.mgt.mapper.StudentMapper;
import student.mgt.service.BusService;

import student.mgt.exception.ResourceNotFoundException;
import student.mgt.exception.DuplicateResourceException;
import student.mgt.exception.BusCapacityExceededException;

import java.util.List;

@Service
@Transactional
public class BusServiceImpl implements BusService {

    private final BusDao busDao;
    private final RouteDao routeDao;
    private final StudentMapper studentMapper;

    public BusServiceImpl(
            BusDao busDao,
            RouteDao routeDao,
            StudentMapper studentMapper) {

        this.busDao = busDao;
        this.routeDao = routeDao;
        this.studentMapper = studentMapper;
    }

    @Override
    public BusDTO save(BusDTO dto) {

        if (busDao.existsByBusNumber(dto.getBusNumber())) {

            throw new DuplicateResourceException(
                    "Bus number already exists: " + dto.getBusNumber()
            );
        }

        if (dto.getCapacity() == null || dto.getCapacity() <= 0) {

            throw new IllegalArgumentException(
                    "Bus capacity must be greater than zero."
            );
        }

        validateRoute(dto.getRouteId());

        Bus bus = studentMapper.toBusEntity(dto);

        Bus savedBus = busDao.save(bus);

        return studentMapper.toBusDTO(savedBus);
    }

    @Override
    public BusDTO update(Long id, BusDTO dto) {

        Bus existingBus = busDao.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Bus not found with ID: " + id
                        )
                );

        if (busDao.existsByBusNumberAndIdNot(
                dto.getBusNumber(), id)) {

            throw new DuplicateResourceException(
                    "Bus number already exists: " + dto.getBusNumber()
            );
        }

        if (dto.getCapacity() == null || dto.getCapacity() <= 0) {

            throw new IllegalArgumentException(
                    "Bus capacity must be greater than zero."
            );
        }

        long assignedStudents =
                busDao.countStudentsAssignedToBus(id);

        if (dto.getCapacity() < assignedStudents) {

            throw new BusCapacityExceededException(
                    "Cannot reduce bus capacity below currently assigned "
                            + "students. Assigned students: "
                            + assignedStudents
            );
        }

        validateRoute(dto.getRouteId());

        studentMapper.updateBusEntity(existingBus, dto);

        Bus updatedBus = busDao.update(existingBus);

        return studentMapper.toBusDTO(updatedBus);
    }

    @Override
    @Transactional(readOnly = true)
    public BusDTO findById(Long id) {

        Bus bus = busDao.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Bus not found with ID: " + id
                        )
                );

        return studentMapper.toBusDTO(bus);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BusDTO> findAll() {

        return studentMapper.toBusDTOList(
                busDao.findAll()
        );
    }

    @Override
    public void deleteById(Long id) {

        Bus bus = busDao.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Bus not found with ID: " + id
                        )
                );

        long assignedStudents =
                busDao.countStudentsAssignedToBus(id);

        if (assignedStudents > 0) {

            throw new BusCapacityExceededException(
                    "Cannot delete bus. " + assignedStudents
                            + " students are currently assigned to it. "
                            + "Reassign students first."
            );
        }

        busDao.deleteById(bus.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public BusDTO findByBusNumber(String busNumber) {

        Bus bus = busDao.findByBusNumber(busNumber)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Bus not found: " + busNumber
                        )
                );

        return studentMapper.toBusDTO(bus);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BusDTO> findByRouteId(Long routeId) {

        if (!routeDao.findById(routeId).isPresent()) {
            throw new ResourceNotFoundException(
                    "Route not found with ID: " + routeId
            );
        }

        return studentMapper.toBusDTOList(
                busDao.findByRouteId(routeId)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<BusDTO> searchBuses(String keyword) {

        return studentMapper.toBusDTOList(
                busDao.searchBuses(keyword)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public long count() {
        return busDao.count();
    }

    @Override
    @Transactional(readOnly = true)
    public long countStudentsAssignedToBus(Long busId) {

        if (!busDao.findById(busId).isPresent()) {
            throw new ResourceNotFoundException(
                    "Bus not found with ID: " + busId
            );
        }

        return busDao.countStudentsAssignedToBus(busId);
    }

    private void validateRoute(Long routeId) {

        if (routeId == null || !routeDao.findById(routeId).isPresent()) {

            throw new ResourceNotFoundException(
                    "Valid route is required for bus registration."
            );
        }
    }
}