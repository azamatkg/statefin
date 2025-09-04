package kg.infosystems.statefin.controller.reference;

import jakarta.validation.Valid;
import kg.infosystems.statefin.dto.request.reference.DecisionMakingBodyCreateRequest;
import kg.infosystems.statefin.dto.update.reference.DecisionMakingBodyUpdateRequest;
import kg.infosystems.statefin.dto.response.reference.DecisionMakingBodyResponse;
import kg.infosystems.statefin.service.DecisionMakingBodyService;
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
@RequestMapping("/api/decision-making-bodies")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class DecisionMakingBodyController {

    private final DecisionMakingBodyService decisionMakingBodyService;

    @PostMapping
    @PreAuthorize("hasAuthority('DECISION_MAKING_BODY_WRITE')")
    public ResponseEntity<DecisionMakingBodyResponse> createDecisionMakingBody(@Valid @RequestBody DecisionMakingBodyCreateRequest createDecisionMakingBodyDto) {
        DecisionMakingBodyResponse decisionMakingBody = decisionMakingBodyService.createDecisionMakingBody(createDecisionMakingBodyDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(decisionMakingBody);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('DECISION_MAKING_BODY_READ')")
    public ResponseEntity<DecisionMakingBodyResponse> getDecisionMakingBodyById(@PathVariable Long id) {
        DecisionMakingBodyResponse decisionMakingBody = decisionMakingBodyService.getDecisionMakingBodyById(id);
        return ResponseEntity.ok(decisionMakingBody);
    }

    @GetMapping
    @PreAuthorize("hasAuthority('DECISION_MAKING_BODY_READ')")
    public ResponseEntity<Page<DecisionMakingBodyResponse>> getAllDecisionMakingBodies(@PageableDefault(size = 20) Pageable pageable) {
        Page<DecisionMakingBodyResponse> decisionMakingBodies = decisionMakingBodyService.getAllDecisionMakingBodies(pageable);
        return ResponseEntity.ok(decisionMakingBodies);
    }

    @GetMapping("/search")
    @PreAuthorize("hasAuthority('DECISION_MAKING_BODY_READ')")
    public ResponseEntity<Page<DecisionMakingBodyResponse>> searchDecisionMakingBodies(
            @RequestParam String searchTerm,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<DecisionMakingBodyResponse> decisionMakingBodies = decisionMakingBodyService.searchDecisionMakingBodies(searchTerm, pageable);
        return ResponseEntity.ok(decisionMakingBodies);
    }

    @GetMapping("/active")
    @PreAuthorize("hasAuthority('DECISION_MAKING_BODY_READ')")
    public ResponseEntity<Page<DecisionMakingBodyResponse>> getAllActiveDecisionMakingBodies(@PageableDefault(size = 20) Pageable pageable) {
        Page<DecisionMakingBodyResponse> decisionMakingBodies = decisionMakingBodyService.getAllActiveDecisionMakingBodies(pageable);
        return ResponseEntity.ok(decisionMakingBodies);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('DECISION_MAKING_BODY_WRITE')")
    public ResponseEntity<DecisionMakingBodyResponse> updateDecisionMakingBody(
            @PathVariable Long id,
            @Valid @RequestBody DecisionMakingBodyUpdateRequest updateDecisionMakingBodyDto) {
        DecisionMakingBodyResponse decisionMakingBody = decisionMakingBodyService.updateDecisionMakingBody(id, updateDecisionMakingBodyDto);
        return ResponseEntity.ok(decisionMakingBody);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('DECISION_MAKING_BODY_DELETE')")
    public ResponseEntity<Void> deleteDecisionMakingBody(@PathVariable Long id) {
        decisionMakingBodyService.deleteDecisionMakingBody(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/exists/name-ru/{nameRu}")
    @PreAuthorize("hasAuthority('DECISION_MAKING_BODY_READ')")
    public ResponseEntity<Boolean> existsByNameRu(@PathVariable String nameRu) {
        boolean exists = decisionMakingBodyService.existsByNameRu(nameRu);
        return ResponseEntity.ok(exists);
    }
}