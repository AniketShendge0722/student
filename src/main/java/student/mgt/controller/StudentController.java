package student.mgt.controller;

import jakarta.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import student.mgt.dto.StudentDTO;
import student.mgt.service.StudentService;
import student.mgt.service.ParentService;
import student.mgt.service.BusService;
import student.mgt.service.SchoolClassService;

@Controller
@RequestMapping("/students")
public class StudentController {

    private final StudentService studentService;
    private final ParentService parentService;
    private final BusService busService;
    private final SchoolClassService schoolClassService;

    public StudentController(
            StudentService studentService,
            ParentService parentService,
            BusService busService,
            SchoolClassService schoolClassService) {

        this.studentService = studentService;
        this.parentService = parentService;
        this.busService = busService;
        this.schoolClassService = schoolClassService;
    }

    @GetMapping
    public String listStudents(
            @RequestParam(required = false) String keyword,
            Model model) {

        if (keyword != null && !keyword.isBlank()) {
            model.addAttribute(
                    "students",
                    studentService.searchStudents(keyword)
            );
        } else {
            model.addAttribute(
                    "students",
                    studentService.findAll()
            );
        }

        model.addAttribute("keyword", keyword);
        model.addAttribute("totalStudents", studentService.count());

        return "students";
    }

    @GetMapping("/add")
    public String addStudent(Model model) {

        model.addAttribute("studentDTO", new StudentDTO());
        loadDropdowns(model);

        return "add-student";
    }

    @PostMapping("/save")
    public String saveStudent(
            @Valid @ModelAttribute("studentDTO") StudentDTO studentDTO,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            loadDropdowns(model);
            return "add-student";
        }

        try {
            studentService.save(studentDTO);

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Student registered successfully!"
            );

            return "redirect:/students";

        } catch (RuntimeException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            loadDropdowns(model);
            return "add-student";
        }
    }

    @GetMapping("/edit/{id}")
    public String editStudent(
            @PathVariable Long id,
            Model model) {

        model.addAttribute(
                "studentDTO",
                studentService.findById(id)
        );

        loadDropdowns(model);

        return "edit-student";
    }

    @PostMapping("/update/{id}")
    public String updateStudent(
            @PathVariable Long id,
            @Valid @ModelAttribute("studentDTO") StudentDTO studentDTO,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            loadDropdowns(model);
            return "edit-student";
        }

        try {
            studentService.update(id, studentDTO);

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Student updated successfully!"
            );

            return "redirect:/students";

        } catch (RuntimeException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            loadDropdowns(model);
            return "edit-student";
        }
    }

    @PostMapping("/delete/{id}")
    public String deleteStudent(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {

        try {
            studentService.deleteById(id);

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Student deleted successfully!"
            );

        } catch (RuntimeException ex) {

            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    ex.getMessage()
            );
        }

        return "redirect:/students";
    }

    private void loadDropdowns(Model model) {

        model.addAttribute(
                "parents",
                parentService.findAll()
        );

        model.addAttribute(
                "buses",
                busService.findAll()
        );

        model.addAttribute(
                "classes",
                schoolClassService.findAllActive()
        );
    }
}