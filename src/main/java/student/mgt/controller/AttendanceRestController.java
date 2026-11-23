package student.mgt.controller;

import jakarta.validation.Valid;
import student.mgt.dto.AttendanceDTO;
import student.mgt.entity.AttendanceStatus;
import student.mgt.service.AttendanceService;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/attendance")
public class AttendanceRestController {

    private final AttendanceService attendanceService;

    public AttendanceRestController(AttendanceService attendanceService) {
        this.attendanceService = attendanceService;
    }

    @PostMapping
    public ResponseEntity<AttendanceDTO> createAttendance(
            @Valid @RequestBody AttendanceDTO attendanceDTO) {

        AttendanceDTO saved = attendanceService.save(attendanceDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @GetMapping
    public ResponseEntity<List<AttendanceDTO>> getAllAttendance(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate date,

            @RequestParam(required = false) Long studentId) {

        if (studentId != null && date != null) {
            return ResponseEntity.ok(
                    attendanceService.findByStudentIdAndDate(studentId, date)
            );
        }

        if (studentId != null) {
            return ResponseEntity.ok(
                    attendanceService.findByStudentId(studentId)
            );
        }

        if (date != null) {
            return ResponseEntity.ok(
                    attendanceService.findByDate(date)
            );
        }

        return ResponseEntity.ok(attendanceService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AttendanceDTO> getAttendanceById(
            @PathVariable Long id) {

        return ResponseEntity.ok(attendanceService.findById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AttendanceDTO> updateAttendance(
            @PathVariable Long id,
            @Valid @RequestBody AttendanceDTO attendanceDTO) {

        return ResponseEntity.ok(
                attendanceService.update(id, attendanceDTO)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAttendance(
            @PathVariable Long id) {

        attendanceService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/count")
    public ResponseEntity<Long> getAttendanceCount(
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate date) {

        return ResponseEntity.ok(attendanceService.countByDate(date));
    }

    @GetMapping("/count/status")
    public ResponseEntity<Long> getAttendanceCountByStatus(
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate date,

            @RequestParam AttendanceStatus status) {

        return ResponseEntity.ok(
                attendanceService.countByDateAndStatus(date, status)
        );
    }
}