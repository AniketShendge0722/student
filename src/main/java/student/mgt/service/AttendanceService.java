package student.mgt.service;

import student.mgt.dto.AttendanceDTO;
import student.mgt.dto.AttendanceBulkItemDTO;

import java.time.LocalDate;
import java.util.List;

public interface AttendanceService {

    AttendanceDTO save(AttendanceDTO attendanceDTO);

    AttendanceDTO update(Long id, AttendanceDTO attendanceDTO);

    AttendanceDTO findById(Long id);

    List<AttendanceDTO> findAll();

    int saveBulk(LocalDate date, String className, List<AttendanceBulkItemDTO> rows);

    void deleteById(Long id);

    List<AttendanceDTO> findByDate(LocalDate date);

    List<AttendanceDTO> findByStudentId(Long studentId);

    List<AttendanceDTO> findByStudentIdAndDate(
            Long studentId,
            LocalDate date
    );

    long countByDate(LocalDate date);

    long countByDateAndStatus(
            LocalDate date,
            student.mgt.entity.AttendanceStatus status
    );
}