package student.mgt.controller;

import jakarta.validation.Valid;
import student.mgt.dto.StudentDTO;
import student.mgt.service.StudentService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/students")
public class StudentRestController {

    private final StudentService studentService;

    public StudentRestController(StudentService studentService) {
        this.studentService = studentService;
    }

    @PostMapping
    public ResponseEntity<StudentDTO> createStudent(
            @Valid @RequestBody StudentDTO studentDTO) {

        StudentDTO saved = studentService.save(studentDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @GetMapping
    public ResponseEntity<List<StudentDTO>> getAllStudents(
            @RequestParam(required = false) String keyword) {

        List<StudentDTO> students =
                (keyword != null && !keyword.isBlank())
                        ? studentService.searchStudents(keyword)
                        : studentService.findAll();

        return ResponseEntity.ok(students);
    }

    @GetMapping("/{id}")
    public ResponseEntity<StudentDTO> getStudentById(
            @PathVariable Long id) {

        return ResponseEntity.ok(studentService.findById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<StudentDTO> updateStudent(
            @PathVariable Long id,
            @Valid @RequestBody StudentDTO studentDTO) {

        return ResponseEntity.ok(studentService.update(id, studentDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStudent(
            @PathVariable Long id) {

        studentService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/roll-no/{rollNo}")
    public ResponseEntity<StudentDTO> getStudentByRollNo(
            @PathVariable String rollNo) {

        return ResponseEntity.ok(studentService.findByRollNo(rollNo));
    }

    @GetMapping("/class/{className}")
    public ResponseEntity<List<StudentDTO>> getStudentsByClass(
            @PathVariable String className) {

        return ResponseEntity.ok(studentService.findByClassName(className));
    }

    @GetMapping("/bus/{busId}")
    public ResponseEntity<List<StudentDTO>> getStudentsByBus(
            @PathVariable Long busId) {

        return ResponseEntity.ok(studentService.findByBusId(busId));
    }

    @GetMapping("/count")
    public ResponseEntity<Long> getStudentCount() {
        return ResponseEntity.ok(studentService.count());
    }

    @GetMapping("/count/active")
    public ResponseEntity<Long> getActiveStudentCount() {
        return ResponseEntity.ok(studentService.countActiveStudents());
    }
}