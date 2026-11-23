
package student.mgt.dto;

import jakarta.validation.constraints.*;
import java.time.LocalDate;

import student.mgt.entity.AttendanceStatus;

public class AttendanceDTO {

    private Long id;

    @NotNull(message = "Please select a student")
    @Positive(message = "Invalid student selection")
    private Long studentId;

    // Student details for displaying attendance records
    private StudentDTO student;

    @NotNull(message = "Attendance date is required")
    @PastOrPresent(message = "Attendance date cannot be in the future")
    private LocalDate date;

    @NotNull(message = "Attendance status is required")
    private AttendanceStatus status;

    @Size(max = 500, message = "Remarks cannot exceed 500 characters")
    private String remarks;

    public AttendanceDTO() {
    }

    public AttendanceDTO(
            Long id,
            Long studentId,
            LocalDate date,
            AttendanceStatus status,
            String remarks) {

        this.id = id;
        this.studentId = studentId;
        this.date = date;
        this.status = status;
        this.remarks = remarks;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public StudentDTO getStudent() {
        return student;
    }

    public void setStudent(StudentDTO student) {
        this.student = student;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public AttendanceStatus getStatus() {
        return status;
    }

    public void setStatus(AttendanceStatus status) {
        this.status = status;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}
