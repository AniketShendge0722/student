package student.mgt.controller;

import jakarta.validation.Valid;
import student.mgt.dto.BusDTO;
import student.mgt.service.BusService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/buses")
public class BusRestController {

    private final BusService busService;

    public BusRestController(BusService busService) {
        this.busService = busService;
    }

    @PostMapping
    public ResponseEntity<BusDTO> createBus(
            @Valid @RequestBody BusDTO busDTO) {

        BusDTO saved = busService.save(busDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @GetMapping
    public ResponseEntity<List<BusDTO>> getAllBuses(
            @RequestParam(required = false) String keyword) {

        List<BusDTO> buses =
                (keyword != null && !keyword.isBlank())
                        ? busService.searchBuses(keyword)
                        : busService.findAll();

        return ResponseEntity.ok(buses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BusDTO> getBusById(
            @PathVariable Long id) {

        return ResponseEntity.ok(busService.findById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BusDTO> updateBus(
            @PathVariable Long id,
            @Valid @RequestBody BusDTO busDTO) {

        return ResponseEntity.ok(busService.update(id, busDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBus(
            @PathVariable Long id) {

        busService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/number/{busNumber}")
    public ResponseEntity<BusDTO> getBusByNumber(
            @PathVariable String busNumber) {

        return ResponseEntity.ok(busService.findByBusNumber(busNumber));
    }

    @GetMapping("/route/{routeId}")
    public ResponseEntity<List<BusDTO>> getBusesByRoute(
            @PathVariable Long routeId) {

        return ResponseEntity.ok(busService.findByRouteId(routeId));
    }

    @GetMapping("/count")
    public ResponseEntity<Long> getBusCount() {
        return ResponseEntity.ok(busService.count());
    }

    @GetMapping("/{id}/student-count")
    public ResponseEntity<Long> getStudentCountForBus(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                busService.countStudentsAssignedToBus(id)
        );
    }
}