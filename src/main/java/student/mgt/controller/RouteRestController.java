package student.mgt.controller;

import jakarta.validation.Valid;
import student.mgt.dto.RouteDTO;
import student.mgt.service.RouteService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/routes")
public class RouteRestController {

    private final RouteService routeService;

    public RouteRestController(RouteService routeService) {
        this.routeService = routeService;
    }

    @PostMapping
    public ResponseEntity<RouteDTO> createRoute(
            @Valid @RequestBody RouteDTO routeDTO) {

        RouteDTO saved = routeService.save(routeDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @GetMapping
    public ResponseEntity<List<RouteDTO>> getAllRoutes(
            @RequestParam(required = false) String keyword) {

        List<RouteDTO> routes =
                (keyword != null && !keyword.isBlank())
                        ? routeService.searchRoutes(keyword)
                        : routeService.findAll();

        return ResponseEntity.ok(routes);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RouteDTO> getRouteById(
            @PathVariable Long id) {

        return ResponseEntity.ok(routeService.findById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RouteDTO> updateRoute(
            @PathVariable Long id,
            @Valid @RequestBody RouteDTO routeDTO) {

        return ResponseEntity.ok(routeService.update(id, routeDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRoute(
            @PathVariable Long id) {

        routeService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/count")
    public ResponseEntity<Long> getRouteCount() {
        return ResponseEntity.ok(routeService.count());
    }
}