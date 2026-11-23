package student.mgt.controller;

import jakarta.validation.Valid;
import student.mgt.dto.ParentDTO;
import student.mgt.service.ParentService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/parents")
public class ParentRestController {

    private final ParentService parentService;

    public ParentRestController(ParentService parentService) {
        this.parentService = parentService;
    }

    @PostMapping
    public ResponseEntity<ParentDTO> createParent(
            @Valid @RequestBody ParentDTO parentDTO) {

        ParentDTO saved = parentService.save(parentDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @GetMapping
    public ResponseEntity<List<ParentDTO>> getAllParents(
            @RequestParam(required = false) String keyword) {

        List<ParentDTO> parents =
                (keyword != null && !keyword.isBlank())
                        ? parentService.searchParents(keyword)
                        : parentService.findAll();

        return ResponseEntity.ok(parents);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ParentDTO> getParentById(
            @PathVariable Long id) {

        return ResponseEntity.ok(parentService.findById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ParentDTO> updateParent(
            @PathVariable Long id,
            @Valid @RequestBody ParentDTO parentDTO) {

        return ResponseEntity.ok(parentService.update(id, parentDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteParent(
            @PathVariable Long id) {

        parentService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<ParentDTO> getParentByEmail(
            @PathVariable String email) {

        return ResponseEntity.ok(parentService.findByEmail(email));
    }

    @GetMapping("/count")
    public ResponseEntity<Long> getParentCount() {
        return ResponseEntity.ok(parentService.count());
    }
}