package student.mgt.controller;

import jakarta.validation.Valid;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import student.mgt.dto.AttendanceDTO;
import student.mgt.service.AttendanceService;
import student.mgt.service.StudentService;
import student.mgt.entity.AttendanceStatus;

import java.time.LocalDate;
import student.mgt.dto.AttendanceBulkDTO;
import student.mgt.dto.AttendanceBulkItemDTO;
import student.mgt.dto.StudentDTO;

import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/attendance")
public class AttendanceController {

    private final AttendanceService attendanceService;
    private final StudentService studentService;

    public AttendanceController(
            AttendanceService attendanceService,
            StudentService studentService) {

        this.attendanceService = attendanceService;
        this.studentService = studentService;
    }

    @GetMapping
    public String attendanceList(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate date,

            @RequestParam(required = false)
            Long studentId,

            Model model) {

        if (date == null) {
            date = LocalDate.now();
        }

        if (studentId != null) {

            model.addAttribute(
                    "attendanceList",
                    attendanceService.findByStudentIdAndDate(
                            studentId, date
                    )
            );

        } else {

            model.addAttribute(
                    "attendanceList",
                    attendanceService.findByDate(date)
            );
        }

        model.addAttribute("selectedDate", date);
        model.addAttribute("selectedStudentId", studentId);

        loadDropdowns(model);

        addAttendanceSummary(model, date);

        return "attendance";
    }

    @GetMapping("/add")
    public String addAttendance(Model model) {

        AttendanceDTO attendanceDTO = new AttendanceDTO();
        attendanceDTO.setDate(LocalDate.now());

        model.addAttribute("attendanceDTO", attendanceDTO);

        loadDropdowns(model);

        return "attendance";
    }

    @PostMapping("/save")
    public String saveAttendance(
            @Valid @ModelAttribute("attendanceDTO")
            AttendanceDTO attendanceDTO,

            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {

            loadDropdowns(model);
            addAttendanceSummary(model, LocalDate.now());

            return "attendance";
        }

        try {
            attendanceService.save(attendanceDTO);

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Attendance marked successfully!"
            );

            return "redirect:/attendance?date=" + attendanceDTO.getDate();

        } catch (RuntimeException ex) {

            model.addAttribute("errorMessage", ex.getMessage());

            loadDropdowns(model);
            addAttendanceSummary(model, attendanceDTO.getDate());

            return "attendance";
        }
    }

    @GetMapping("/edit/{id}")
    public String editAttendance(
            @PathVariable Long id,
            Model model) {

        model.addAttribute(
                "attendanceDTO",
                attendanceService.findById(id)
        );

        loadDropdowns(model);

        return "attendance";
    }

    @PostMapping("/update/{id}")
    public String updateAttendance(
            @PathVariable Long id,

            @Valid @ModelAttribute("attendanceDTO")
            AttendanceDTO attendanceDTO,

            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {

            loadDropdowns(model);
            addAttendanceSummary(model, attendanceDTO.getDate());

            return "attendance";
        }

        try {
            attendanceService.update(id, attendanceDTO);

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Attendance updated successfully!"
            );

            return "redirect:/attendance?date=" + attendanceDTO.getDate();

        } catch (RuntimeException ex) {

            model.addAttribute("errorMessage", ex.getMessage());

            loadDropdowns(model);
            addAttendanceSummary(model, attendanceDTO.getDate());

            return "attendance";
        }
    }

    @PostMapping("/delete/{id}")
    public String deleteAttendance(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {

        try {
            attendanceService.deleteById(id);

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Attendance deleted successfully!"
            );

        } catch (RuntimeException ex) {

            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    ex.getMessage()
            );
        }

        return "redirect:/attendance";
    }

    private void loadDropdowns(Model model) {

        model.addAttribute(
                "students",
                studentService.findAll()
        );

        model.addAttribute(
                "attendanceStatuses",
                AttendanceStatus.values()
        );
    }

    private void addAttendanceSummary(
            Model model,
            LocalDate date) {

        model.addAttribute(
                "totalAttendance",
                attendanceService.countByDate(date)
        );

        model.addAttribute(
                "presentCount",
                attendanceService.countByDateAndStatus(
                        date, AttendanceStatus.PRESENT
                )
        );

        model.addAttribute(
                "absentCount",
                attendanceService.countByDateAndStatus(
                        date, AttendanceStatus.ABSENT
                )
        );

        model.addAttribute(
                "lateCount",
                attendanceService.countByDateAndStatus(
                        date, AttendanceStatus.LATE
                )
        );
    }
    @GetMapping("/bulk")
    public String bulkAttendance(
            @RequestParam(required = false) LocalDate date,
            @RequestParam(required = false) String className,
            Model model) {

        if (date == null) {
            date = LocalDate.now();
        }

        List<StudentDTO> allStudents = studentService.findAll();

        List<String> classNames = allStudents.stream()
                .map(StudentDTO::getClassName)
                .filter(name -> name != null && !name.isBlank())
                .distinct()
                .sorted()
                .collect(Collectors.toList());

        if (className == null || className.isBlank()) {
            className = classNames.isEmpty() ? null : classNames.get(0);
        }

        AttendanceBulkDTO bulkDTO = new AttendanceBulkDTO();
        bulkDTO.setDate(date);
        bulkDTO.setClassName(className);

        Map<Long, AttendanceDTO> existingRecords = new HashMap<>();

        attendanceService.findByDate(date).forEach(record ->
                existingRecords.put(record.getStudentId(), record)
        );

        if (className != null) {

            List<StudentDTO> students =
                    studentService.findByClassName(className);

            for (StudentDTO student : students) {

                if (!Boolean.TRUE.equals(student.getActive())) {
                    continue;
                }

                AttendanceBulkItemDTO row =
                        new AttendanceBulkItemDTO();

                row.setStudentId(student.getId());
                row.setStudentName(student.getName());
                row.setRollNo(student.getRollNo());

                AttendanceDTO existing =
                        existingRecords.get(student.getId());

                if (existing != null) {
                    row.setStatus(existing.getStatus());
                    row.setRemarks(existing.getRemarks());
                    row.setSelected(true);
                }

                bulkDTO.getRows().add(row);
            }
        }

        model.addAttribute("bulkDTO", bulkDTO);
        model.addAttribute("classNames", classNames);
        model.addAttribute("attendanceStatuses", AttendanceStatus.values());

        model.addAttribute("totalStudents", bulkDTO.getRows().size());

        model.addAttribute("totalAttendance",
                attendanceService.countByDate(date));

        model.addAttribute("presentCount",
                attendanceService.countByDateAndStatus(
                        date, AttendanceStatus.PRESENT));

        model.addAttribute("absentCount",
                attendanceService.countByDateAndStatus(
                        date, AttendanceStatus.ABSENT));

        model.addAttribute("lateCount",
                attendanceService.countByDateAndStatus(
                        date, AttendanceStatus.LATE));

        return "attendance-bulk";
    }


    @PostMapping("/bulk/save")
    public String saveBulkAttendance(
            @ModelAttribute("bulkDTO") AttendanceBulkDTO bulkDTO,
            RedirectAttributes redirectAttributes) {

        try {

            int changed = attendanceService.saveBulk(
                    bulkDTO.getDate(),
                    bulkDTO.getClassName(),
                    bulkDTO.getRows()
            );

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    changed + " attendance record(s) saved successfully."
            );

        } catch (RuntimeException ex) {

            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    ex.getMessage()
            );
        }

        return "redirect:/attendance/bulk?date="
                + bulkDTO.getDate()
                + "&className="
                + java.net.URLEncoder.encode(
                bulkDTO.getClassName(),
                java.nio.charset.StandardCharsets.UTF_8
        );
    }

}