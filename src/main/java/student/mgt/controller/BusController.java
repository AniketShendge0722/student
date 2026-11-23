package student.mgt.controller;

import jakarta.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import student.mgt.dto.BusDTO;
import student.mgt.service.BusService;
import student.mgt.service.RouteService;

@Controller
@RequestMapping("/buses")
public class BusController {

    private final BusService busService;
    private final RouteService routeService;

    public BusController(
            BusService busService,
            RouteService routeService) {

        this.busService = busService;
        this.routeService = routeService;
    }

    @GetMapping
    public String listBuses(
            @RequestParam(required = false) String keyword,
            Model model) {

        model.addAttribute(
                "buses",
                busService.searchBuses(keyword)
        );

        model.addAttribute("keyword", keyword);
        model.addAttribute("totalBuses", busService.count());

        return "buses";
    }

    @GetMapping("/add")
    public String addBus(Model model) {

        model.addAttribute("busDTO", new BusDTO());
        loadDropdowns(model);

        return "buses";
    }

    @PostMapping("/save")
    public String saveBus(
            @Valid @ModelAttribute("busDTO") BusDTO busDTO,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            loadDropdowns(model);
            model.addAttribute("buses", busService.findAll());
            return "buses";
        }

        try {
            busService.save(busDTO);

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Bus registered successfully!"
            );

            return "redirect:/buses";

        } catch (RuntimeException ex) {

            model.addAttribute("errorMessage", ex.getMessage());
            model.addAttribute("buses", busService.findAll());

            loadDropdowns(model);

            return "buses";
        }
    }

    @GetMapping("/edit/{id}")
    public String editBus(
            @PathVariable Long id,
            Model model) {

        model.addAttribute(
                "busDTO",
                busService.findById(id)
        );

        model.addAttribute("buses", busService.findAll());

        loadDropdowns(model);

        return "buses";
    }

    @PostMapping("/update/{id}")
    public String updateBus(
            @PathVariable Long id,
            @Valid @ModelAttribute("busDTO") BusDTO busDTO,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {

            model.addAttribute("buses", busService.findAll());
            loadDropdowns(model);

            return "buses";
        }

        try {
            busService.update(id, busDTO);

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Bus updated successfully!"
            );

            return "redirect:/buses";

        } catch (RuntimeException ex) {

            model.addAttribute("errorMessage", ex.getMessage());
            model.addAttribute("buses", busService.findAll());

            loadDropdowns(model);

            return "buses";
        }
    }

    @PostMapping("/delete/{id}")
    public String deleteBus(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {

        try {
            busService.deleteById(id);

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Bus deleted successfully!"
            );

        } catch (RuntimeException ex) {

            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    ex.getMessage()
            );
        }

        return "redirect:/buses";
    }

    private void loadDropdowns(Model model) {

        model.addAttribute(
                "routes",
                routeService.findAll()
        );
    }
}