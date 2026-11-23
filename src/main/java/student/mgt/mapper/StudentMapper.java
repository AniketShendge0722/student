
package student.mgt.mapper;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;

import org.springframework.stereotype.Component;

import student.mgt.dto.*;
import student.mgt.entity.*;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class StudentMapper {

    private final EntityManager entityManager;

    public StudentMapper(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    // ================= STUDENT =================

    public StudentDTO toStudentDTO(Student student) {

        if (student == null) {
            return null;
        }

        return new StudentDTO(
                student.getId(),
                student.getName(),
                student.getRollNo(),
                student.getClassName(),
                student.getGender(),
                student.getDob(),
                student.getAddress(),
                student.getPhone(),
                student.getEmail(),
                student.getAdmissionDate(),
                student.getParent() != null
                        ? student.getParent().getId() : null,
                student.getBus() != null
                        ? student.getBus().getId() : null,
                student.getActive()
        );
    }

    public Student toStudentEntity(StudentDTO dto) {

        Student student = new Student();
        return updateStudentEntity(student, dto);
    }

    public Student updateStudentEntity(Student student, StudentDTO dto) {

        student.setName(dto.getName());
        student.setRollNo(dto.getRollNo());
        student.setClassName(dto.getClassName());
        student.setGender(dto.getGender());
        student.setDob(dto.getDob());
        student.setAddress(dto.getAddress());
        student.setPhone(dto.getPhone());
        student.setEmail(dto.getEmail());
        student.setAdmissionDate(dto.getAdmissionDate());
        student.setActive(dto.getActive());

        Parent parent = entityManager.find(Parent.class, dto.getParentId());

        if (parent == null) {
            throw new EntityNotFoundException(
                    "Parent not found: " + dto.getParentId()
            );
        }

        student.setParent(parent);

        if (dto.getBusId() != null) {

            Bus bus = entityManager.find(Bus.class, dto.getBusId());

            if (bus == null) {
                throw new EntityNotFoundException(
                        "Bus not found: " + dto.getBusId()
                );
            }

            student.setBus(bus);

        } else {
            student.setBus(null);
        }

        return student;
    }

    public List<StudentDTO> toStudentDTOList(List<Student> students) {

        if (students == null) {
            return Collections.emptyList();
        }

        return students.stream()
                .map(this::toStudentDTO)
                .collect(Collectors.toList());
    }

    // ================= PARENT =================

    public ParentDTO toParentDTO(Parent parent) {

        if (parent == null) {
            return null;
        }

        List<Long> studentIds = parent.getStudents() == null
                ? Collections.emptyList()
                : parent.getStudents().stream()
                .map(Student::getId)
                .collect(Collectors.toList());

        return new ParentDTO(
                parent.getId(),
                parent.getFatherName(),
                parent.getMotherName(),
                parent.getPhone(),
                parent.getEmail(),
                parent.getAddress(),
                studentIds
        );
    }

    public Parent toParentEntity(ParentDTO dto) {

        Parent parent = new Parent();

        parent.setFatherName(dto.getFatherName());
        parent.setMotherName(dto.getMotherName());
        parent.setPhone(dto.getPhone());
        parent.setEmail(dto.getEmail());
        parent.setAddress(dto.getAddress());

        return parent;
    }

    public Parent updateParentEntity(Parent parent, ParentDTO dto) {

        parent.setFatherName(dto.getFatherName());
        parent.setMotherName(dto.getMotherName());
        parent.setPhone(dto.getPhone());
        parent.setEmail(dto.getEmail());
        parent.setAddress(dto.getAddress());

        return parent;
    }

    public List<ParentDTO> toParentDTOList(List<Parent> parents) {

        if (parents == null) {
            return Collections.emptyList();
        }

        return parents.stream()
                .map(this::toParentDTO)
                .collect(Collectors.toList());
    }

    // ================= BUS =================

    public BusDTO toBusDTO(Bus bus) {

        if (bus == null) {
            return null;
        }

        List<Long> studentIds = bus.getStudents() == null
                ? Collections.emptyList()
                : bus.getStudents().stream()
                .map(Student::getId)
                .collect(Collectors.toList());

        return new BusDTO(
                bus.getId(),
                bus.getBusNumber(),
                bus.getDriverName(),
                bus.getDriverPhone(),
                bus.getCapacity(),
                bus.getRoute() != null ? bus.getRoute().getId() : null,
                studentIds
        );
    }

    public Bus toBusEntity(BusDTO dto) {

        Bus bus = new Bus();
        return updateBusEntity(bus, dto);
    }

    public Bus updateBusEntity(Bus bus, BusDTO dto) {

        bus.setBusNumber(dto.getBusNumber());
        bus.setDriverName(dto.getDriverName());
        bus.setDriverPhone(dto.getDriverPhone());
        bus.setCapacity(dto.getCapacity());

        Route route = entityManager.find(Route.class, dto.getRouteId());

        if (route == null) {
            throw new EntityNotFoundException(
                    "Route not found: " + dto.getRouteId()
            );
        }

        bus.setRoute(route);

        return bus;
    }

    public List<BusDTO> toBusDTOList(List<Bus> buses) {

        if (buses == null) {
            return Collections.emptyList();
        }

        return buses.stream()
                .map(this::toBusDTO)
                .collect(Collectors.toList());
    }

    // ================= ROUTE =================

    public RouteDTO toRouteDTO(Route route) {

        if (route == null) {
            return null;
        }

        List<Long> busIds = route.getBuses() == null
                ? Collections.emptyList()
                : route.getBuses().stream()
                .map(Bus::getId)
                .collect(Collectors.toList());

        return new RouteDTO(
                route.getId(),
                route.getRouteName(),
                route.getStartPoint(),
                route.getEndPoint(),
                route.getStops(),
                route.getDistanceKm(),
                busIds
        );
    }

    public Route toRouteEntity(RouteDTO dto) {

        Route route = new Route();
        return updateRouteEntity(route, dto);
    }

    public Route updateRouteEntity(Route route, RouteDTO dto) {

        route.setRouteName(dto.getRouteName());
        route.setStartPoint(dto.getStartPoint());
        route.setEndPoint(dto.getEndPoint());
        route.setStops(dto.getStops());
        route.setDistanceKm(dto.getDistanceKm());

        return route;
    }

    public List<RouteDTO> toRouteDTOList(List<Route> routes) {

        if (routes == null) {
            return Collections.emptyList();
        }

        return routes.stream()
                .map(this::toRouteDTO)
                .collect(Collectors.toList());
    }

    // ================= ATTENDANCE =================

    public AttendanceDTO toAttendanceDTO(Attendance attendance) {

        if (attendance == null) {
            return null;
        }

        AttendanceDTO dto = new AttendanceDTO(
                attendance.getId(),
                attendance.getStudent() != null
                        ? attendance.getStudent().getId() : null,
                attendance.getDate(),
                attendance.getStatus(),
                attendance.getRemarks()
        );

        dto.setStudent(
                attendance.getStudent() != null
                        ? toStudentDTO(attendance.getStudent())
                        : null
        );

        return dto;
    }

    public Attendance toAttendanceEntity(AttendanceDTO dto) {

        Attendance attendance = new Attendance();

        Student student = entityManager.find(
                Student.class,
                dto.getStudentId()
        );

        if (student == null) {
            throw new EntityNotFoundException(
                    "Student not found: " + dto.getStudentId()
            );
        }

        attendance.setStudent(student);
        attendance.setDate(dto.getDate());
        attendance.setStatus(dto.getStatus());
        attendance.setRemarks(dto.getRemarks());

        return attendance;
    }

    public Attendance updateAttendanceEntity(
            Attendance attendance,
            AttendanceDTO dto) {

        Student student = entityManager.find(
                Student.class,
                dto.getStudentId()
        );

        if (student == null) {
            throw new EntityNotFoundException(
                    "Student not found: " + dto.getStudentId()
            );
        }

        attendance.setStudent(student);
        attendance.setDate(dto.getDate());
        attendance.setStatus(dto.getStatus());
        attendance.setRemarks(dto.getRemarks());

        return attendance;
    }

    public List<AttendanceDTO> toAttendanceDTOList(
            List<Attendance> attendances) {

        if (attendances == null) {
            return Collections.emptyList();
        }

        return attendances.stream()
                .map(this::toAttendanceDTO)
                .collect(Collectors.toList());
    }
}
