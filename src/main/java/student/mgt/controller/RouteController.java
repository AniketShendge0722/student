package student.mgt.controller;

import jakarta.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import student.mgt.dto.RouteDTO;
import student.mgt.service.RouteService;

@Controller
@RequestMapping("/routes")
public class RouteController {

    private final RouteService routeService;

    public RouteController(RouteService routeService) {
        this.routeService = routeService;
    }

    @GetMapping
    public String listRoutes(
            @RequestParam(required = false) String keyword,
            Model model) {

        model.addAttribute(
                "routes",
                routeService.searchRoutes(keyword)
        );

        model.addAttribute("keyword", keyword);
        model.addAttribute("totalRoutes", routeService.count());

        return "routes";
    }

    @GetMapping("/add")
    public String addRoute(Model model) {

        model.addAttribute("routeDTO", new RouteDTO());

        return "routes";
    }

    @PostMapping("/save")
    public String saveRoute(
            @Valid @ModelAttribute("routeDTO") RouteDTO routeDTO,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("routes", routeService.findAll());
            return "routes";
        }

        try {
            routeService.save(routeDTO);

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Route created successfully!"
            );

            return "redirect:/routes";

        } catch (RuntimeException ex) {

            model.addAttribute("errorMessage", ex.getMessage());
            model.addAttribute("routes", routeService.findAll());

            return "routes";
        }
    }

    @GetMapping("/edit/{id}")
    public String editRoute(
            @PathVariable Long id,
            Model model) {

        model.addAttribute(
                "routeDTO",
                routeService.findById(id)
        );

        model.addAttribute("routes", routeService.findAll());

        return "routes";
    }

    @PostMapping("/update/{id}")
    public String updateRoute(
            @PathVariable Long id,
            @Valid @ModelAttribute("routeDTO") RouteDTO routeDTO,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("routes", routeService.findAll());
            return "routes";
        }

        try {
            routeService.update(id, routeDTO);

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Route updated successfully!"
            );

            return "redirect:/routes";

        } catch (RuntimeException ex) {

            model.addAttribute("errorMessage", ex.getMessage());
            model.addAttribute("routes", routeService.findAll());

            return "routes";
        }
    }

    @PostMapping("/delete/{id}")
    public String deleteRoute(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {

        try {
            routeService.deleteById(id);

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Route deleted successfully!"
            );

        } catch (RuntimeException ex) {

            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    ex.getMessage()
            );
        }

        return "redirect:/routes";
    }
}