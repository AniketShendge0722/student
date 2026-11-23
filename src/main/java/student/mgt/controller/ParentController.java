package student.mgt.controller;

import jakarta.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import student.mgt.dto.ParentDTO;
import student.mgt.service.ParentService;

@Controller
@RequestMapping("/parents")
public class ParentController {

    private final ParentService parentService;

    public ParentController(ParentService parentService) {
        this.parentService = parentService;
    }

    @GetMapping
    public String listParents(
            @RequestParam(required = false) String keyword,
            Model model) {

        model.addAttribute(
                "parents",
                parentService.searchParents(keyword)
        );

        model.addAttribute("keyword", keyword);
        model.addAttribute("totalParents", parentService.count());

        return "parents";
    }

    @GetMapping("/add")
    public String addParent(Model model) {

        model.addAttribute("parentDTO", new ParentDTO());

        return "parents";
    }

    @PostMapping("/save")
    public String saveParent(
            @Valid @ModelAttribute("parentDTO") ParentDTO parentDTO,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {

            model.addAttribute(
                    "parents",
                    parentService.findAll()
            );

            return "parents";
        }

        try {
            parentService.save(parentDTO);

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Parent registered successfully!"
            );

            return "redirect:/parents";

        } catch (RuntimeException ex) {

            model.addAttribute("errorMessage", ex.getMessage());
            model.addAttribute("parents", parentService.findAll());

            return "parents";
        }
    }

    @GetMapping("/edit/{id}")
    public String editParent(
            @PathVariable Long id,
            Model model) {

        model.addAttribute(
                "parentDTO",
                parentService.findById(id)
        );

        model.addAttribute("parents", parentService.findAll());

        return "parents";
    }

    @PostMapping("/update/{id}")
    public String updateParent(
            @PathVariable Long id,
            @Valid @ModelAttribute("parentDTO") ParentDTO parentDTO,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {

            model.addAttribute("parents", parentService.findAll());

            return "parents";
        }

        try {
            parentService.update(id, parentDTO);

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Parent updated successfully!"
            );

            return "redirect:/parents";

        } catch (RuntimeException ex) {

            model.addAttribute("errorMessage", ex.getMessage());
            model.addAttribute("parents", parentService.findAll());

            return "parents";
        }
    }

    @PostMapping("/delete/{id}")
    public String deleteParent(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {

        try {
            parentService.deleteById(id);

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Parent deleted successfully!"
            );

        } catch (RuntimeException ex) {

            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    ex.getMessage()
            );
        }

        return "redirect:/parents";
    }
}