package student.mgt.dao;

import student.mgt.entity.Attendance;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AttendanceDao {

    Attendance save(Attendance attendance);

    Attendance update(Attendance attendance);

    Optional<Attendance> findById(Long id);

    List<Attendance> findAll();

    void deleteById(Long id);

    List<Attendance> findByDate(LocalDate date);

    List<Attendance> findByStudentId(Long studentId);

    List<Attendance> findByStudentIdAndDate(
            Long studentId,
            LocalDate date
    );

    boolean existsByStudentIdAndDate(
            Long studentId,
            LocalDate date
    );

    long countByDate(LocalDate date);

    long countByDateAndStatus(
            LocalDate date,
            student.mgt.entity.AttendanceStatus status
    );
}