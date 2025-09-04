package kg.infosystems.statefin.controller.reference;

import jakarta.validation.Valid;
import kg.infosystems.statefin.dto.request.reference.DecisionTypeCreateRequest;
import kg.infosystems.statefin.dto.update.reference.DecisionTypeUpdateRequest;
import kg.infosystems.statefin.dto.response.reference.DecisionTypeResponse;
import kg.infosystems.statefin.service.DecisionTypeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/decision-types")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class DecisionTypeController {

    private final DecisionTypeService decisionTypeService;

    @PostMapping
    @PreAuthorize("hasAuthority('DECISION_TYPE_WRITE')")
    public ResponseEntity<DecisionTypeResponse> createDecisionType(@Valid @RequestBody DecisionTypeCreateRequest createDecisionTypeDto) {
        DecisionTypeResponse decisionType = decisionTypeService.createDecisionType(createDecisionTypeDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(decisionType);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('DECISION_TYPE_READ')")
    public ResponseEntity<DecisionTypeResponse> getDecisionTypeById(@PathVariable Long id) {
        DecisionTypeResponse decisionType = decisionTypeService.getDecisionTypeById(id);
        return ResponseEntity.ok(decisionType);
    }

    @GetMapping
    @PreAuthorize("hasAuthority('DECISION_TYPE_READ')")
    public ResponseEntity<Page<DecisionTypeResponse>> getAllDecisionTypes(@PageableDefault(size = 20) Pageable pageable) {
        Page<DecisionTypeResponse> decisionTypes = decisionTypeService.getAllDecisionTypes(pageable);
        return ResponseEntity.ok(decisionTypes);
    }

    @GetMapping("/search")
    @PreAuthorize("hasAuthority('DECISION_TYPE_READ')")
    public ResponseEntity<Page<DecisionTypeResponse>> searchDecisionTypes(
            @RequestParam String searchTerm,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<DecisionTypeResponse> decisionTypes = decisionTypeService.searchDecisionTypes(searchTerm, pageable);
        return ResponseEntity.ok(decisionTypes);
    }

    @GetMapping("/active")
    @PreAuthorize("hasAuthority('DECISION_TYPE_READ')")
    public ResponseEntity<Page<DecisionTypeResponse>> getAllActiveDecisionTypes(@PageableDefault(size = 20) Pageable pageable) {
        Page<DecisionTypeResponse> decisionTypes = decisionTypeService.getAllActiveDecisionTypes(pageable);
        return ResponseEntity.ok(decisionTypes);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('DECISION_TYPE_WRITE')")
    public ResponseEntity<DecisionTypeResponse> updateDecisionType(
            @PathVariable Long id,
            @Valid @RequestBody DecisionTypeUpdateRequest updateDecisionTypeDto) {
        DecisionTypeResponse decisionType = decisionTypeService.updateDecisionType(id, updateDecisionTypeDto);
        return ResponseEntity.ok(decisionType);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('DECISION_TYPE_DELETE')")
    public ResponseEntity<Void> deleteDecisionType(@PathVariable Long id) {
        decisionTypeService.deleteDecisionType(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/exists/name-ru/{nameRu}")
    @PreAuthorize("hasAuthority('DECISION_TYPE_READ')")
    public ResponseEntity<Boolean> existsByNameRu(@PathVariable String nameRu) {
        boolean exists = decisionTypeService.existsByNameRu(nameRu);
        return ResponseEntity.ok(exists);
    }
}