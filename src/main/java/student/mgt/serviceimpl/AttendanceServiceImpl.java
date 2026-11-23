package student.mgt.serviceimpl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import student.mgt.dao.AttendanceDao;
import student.mgt.dao.StudentDao;

import student.mgt.dto.AttendanceDTO;
import student.mgt.entity.Attendance;
import student.mgt.entity.AttendanceStatus;

import student.mgt.mapper.StudentMapper;
import student.mgt.service.AttendanceService;

import student.mgt.exception.ResourceNotFoundException;
import student.mgt.exception.DuplicateResourceException;

import java.time.LocalDate;
import java.util.List;
import student.mgt.dto.AttendanceBulkItemDTO;
import student.mgt.entity.Student;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Objects;

@Service
@Transactional
public class AttendanceServiceImpl implements AttendanceService {

    private final AttendanceDao attendanceDao;
    private final StudentDao studentDao;
    private final StudentMapper studentMapper;

    public AttendanceServiceImpl(
            AttendanceDao attendanceDao,
            StudentDao studentDao,
            StudentMapper studentMapper) {

        this.attendanceDao = attendanceDao;
        this.studentDao = studentDao;
        this.studentMapper = studentMapper;
    }

    @Override
    public AttendanceDTO save(AttendanceDTO dto) {

        validateAttendance(dto);

        if (!studentDao.findById(dto.getStudentId()).isPresent()) {

            throw new ResourceNotFoundException(
                    "Student not found with ID: " + dto.getStudentId()
            );
        }

        if (attendanceDao.existsByStudentIdAndDate(
                dto.getStudentId(), dto.getDate())) {

            throw new DuplicateResourceException(
                    "Attendance already marked for student ID "
                            + dto.getStudentId()
                            + " on " + dto.getDate()
            );
        }

        Attendance attendance =
                studentMapper.toAttendanceEntity(dto);

        Attendance savedAttendance =
                attendanceDao.save(attendance);

        return studentMapper.toAttendanceDTO(savedAttendance);
    }

    @Override
    public AttendanceDTO update(Long id, AttendanceDTO dto) {

        Attendance existingAttendance = attendanceDao.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Attendance not found with ID: " + id
                        )
                );

        validateAttendance(dto);

        if (!studentDao.findById(dto.getStudentId()).isPresent()) {

            throw new ResourceNotFoundException(
                    "Student not found with ID: " + dto.getStudentId()
            );
        }

        List<Attendance> sameDayRecords =
                attendanceDao.findByStudentIdAndDate(
                        dto.getStudentId(), dto.getDate()
                );

        boolean duplicate = sameDayRecords.stream()
                .anyMatch(record ->
                        !record.getId().equals(id)
                );

        if (duplicate) {

            throw new DuplicateResourceException(
                    "Attendance already exists for this student and date."
            );
        }

        studentMapper.updateAttendanceEntity(
                existingAttendance, dto
        );

        Attendance updatedAttendance =
                attendanceDao.update(existingAttendance);

        return studentMapper.toAttendanceDTO(updatedAttendance);
    }

    @Override
    @Transactional(readOnly = true)
    public AttendanceDTO findById(Long id) {

        Attendance attendance = attendanceDao.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Attendance not found with ID: " + id
                        )
                );

        return studentMapper.toAttendanceDTO(attendance);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AttendanceDTO> findAll() {

        return studentMapper.toAttendanceDTOList(
                attendanceDao.findAll()
        );
    }

    @Override
    public void deleteById(Long id) {

        Attendance attendance = attendanceDao.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Attendance not found with ID: " + id
                        )
                );

        attendanceDao.deleteById(attendance.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AttendanceDTO> findByDate(LocalDate date) {

        return studentMapper.toAttendanceDTOList(
                attendanceDao.findByDate(date)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<AttendanceDTO> findByStudentId(Long studentId) {

        if (!studentDao.findById(studentId).isPresent()) {

            throw new ResourceNotFoundException(
                    "Student not found with ID: " + studentId
            );
        }

        return studentMapper.toAttendanceDTOList(
                attendanceDao.findByStudentId(studentId)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<AttendanceDTO> findByStudentIdAndDate(
            Long studentId, LocalDate date) {

        if (!studentDao.findById(studentId).isPresent()) {

            throw new ResourceNotFoundException(
                    "Student not found with ID: " + studentId
            );
        }

        return studentMapper.toAttendanceDTOList(
                attendanceDao.findByStudentIdAndDate(
                        studentId, date
                )
        );
    }

    @Override
    @Transactional(readOnly = true)
    public long countByDate(LocalDate date) {

        return attendanceDao.countByDate(date);
    }

    @Override
    @Transactional(readOnly = true)
    public long countByDateAndStatus(
            LocalDate date, AttendanceStatus status) {

        return attendanceDao.countByDateAndStatus(date, status);
    }

    private void validateAttendance(AttendanceDTO dto) {

        if (dto.getStudentId() == null) {
            throw new IllegalArgumentException(
                    "Student ID is required."
            );
        }

        if (dto.getDate() == null) {
            throw new IllegalArgumentException(
                    "Attendance date is required."
            );
        }

        if (dto.getDate().isAfter(LocalDate.now())) {
            throw new IllegalArgumentException(
                    "Attendance date cannot be in the future."
            );
        }

        if (dto.getStatus() == null) {
            throw new IllegalArgumentException(
                    "Attendance status is required."
            );
        }
    }
    @Override
    public int saveBulk(
            LocalDate date,
            String className,
            List<AttendanceBulkItemDTO> rows) {

        if (date == null) {
            throw new IllegalArgumentException(
                    "Attendance date is required."
            );
        }

        if (date.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException(
                    "Attendance date cannot be in the future."
            );
        }

        if (className == null || className.isBlank()) {
            throw new IllegalArgumentException(
                    "Please select a class."
            );
        }

        if (rows == null || rows.isEmpty()) {
            throw new IllegalArgumentException(
                    "No students were submitted."
            );
        }

        // Load students belonging to the selected class.
        Map<Long, Student> allowedStudents = new HashMap<>();

        studentDao.findByClassName(className)
                .stream()
                .filter(student ->
                        Boolean.TRUE.equals(student.getActive())
                )
                .forEach(student ->
                        allowedStudents.put(student.getId(), student)
                );

        // Load existing attendance for this date.
        Map<Long, Attendance> existingRecords = new HashMap<>();

        attendanceDao.findByDate(date).forEach(attendance ->
                existingRecords.put(
                        attendance.getStudent().getId(),
                        attendance
                )
        );

        Set<Long> processedStudentIds = new HashSet<>();

        int changedRecords = 0;

        for (AttendanceBulkItemDTO row : rows) {

            // Unselected students are not modified.
            if (row == null || !row.isSelected()) {
                continue;
            }

            Long studentId = row.getStudentId();

            if (studentId == null) {
                throw new IllegalArgumentException(
                        "Invalid student selection."
                );
            }

            if (!processedStudentIds.add(studentId)) {
                throw new IllegalArgumentException(
                        "Duplicate student in attendance submission."
                );
            }

            Student student = allowedStudents.get(studentId);

            if (student == null) {
                throw new IllegalArgumentException(
                        "Student does not belong to the selected class."
                );
            }

            if (row.getStatus() == null) {
                throw new IllegalArgumentException(
                        "Please select attendance status for "
                                + student.getName()
                );
            }

            Attendance existing = existingRecords.get(studentId);

            if (existing != null) {

                boolean statusChanged =
                        existing.getStatus() != row.getStatus();

                boolean remarksChanged =
                        !Objects.equals(
                                existing.getRemarks(),
                                row.getRemarks()
                        );

                // Do not update unchanged records.
                if (!statusChanged && !remarksChanged) {
                    continue;
                }

                existing.setStatus(row.getStatus());
                existing.setRemarks(row.getRemarks());

                attendanceDao.update(existing);

            } else {

                Attendance attendance = new Attendance();

                attendance.setStudent(student);
                attendance.setDate(date);
                attendance.setStatus(row.getStatus());
                attendance.setRemarks(row.getRemarks());

                attendanceDao.save(attendance);
            }

            changedRecords++;
        }

        return changedRecords;
    }
}