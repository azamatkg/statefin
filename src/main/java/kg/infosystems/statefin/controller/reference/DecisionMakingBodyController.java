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
@PreAuthorize("hasRole('ADMIN')")
public class DecisionMakingBodyController {

    private final DecisionMakingBodyService decisionMakingBodyService;

    @PostMapping
    public ResponseEntity<DecisionMakingBodyResponse> createDecisionMakingBody(@Valid @RequestBody DecisionMakingBodyCreateRequest createDecisionMakingBodyDto) {
        DecisionMakingBodyResponse decisionMakingBody = decisionMakingBodyService.createDecisionMakingBody(createDecisionMakingBodyDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(decisionMakingBody);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DecisionMakingBodyResponse> getDecisionMakingBodyById(@PathVariable Long id) {
        DecisionMakingBodyResponse decisionMakingBody = decisionMakingBodyService.getDecisionMakingBodyById(id);
        return ResponseEntity.ok(decisionMakingBody);
    }

    @GetMapping
    public ResponseEntity<Page<DecisionMakingBodyResponse>> getAllDecisionMakingBodies(@PageableDefault(size = 20) Pageable pageable) {
        Page<DecisionMakingBodyResponse> decisionMakingBodies = decisionMakingBodyService.getAllDecisionMakingBodies(pageable);
        return ResponseEntity.ok(decisionMakingBodies);
    }

    @GetMapping("/search")
    public ResponseEntity<Page<DecisionMakingBodyResponse>> searchDecisionMakingBodies(
            @RequestParam String searchTerm,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<DecisionMakingBodyResponse> decisionMakingBodies = decisionMakingBodyService.searchDecisionMakingBodies(searchTerm, pageable);
        return ResponseEntity.ok(decisionMakingBodies);
    }

    @GetMapping("/active")
    public ResponseEntity<Page<DecisionMakingBodyResponse>> getAllActiveDecisionMakingBodies(@PageableDefault(size = 20) Pageable pageable) {
        Page<DecisionMakingBodyResponse> decisionMakingBodies = decisionMakingBodyService.getAllActiveDecisionMakingBodies(pageable);
        return ResponseEntity.ok(decisionMakingBodies);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DecisionMakingBodyResponse> updateDecisionMakingBody(
            @PathVariable Long id,
            @Valid @RequestBody DecisionMakingBodyUpdateRequest updateDecisionMakingBodyDto) {
        DecisionMakingBodyResponse decisionMakingBody = decisionMakingBodyService.updateDecisionMakingBody(id, updateDecisionMakingBodyDto);
        return ResponseEntity.ok(decisionMakingBody);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDecisionMakingBody(@PathVariable Long id) {
        decisionMakingBodyService.deleteDecisionMakingBody(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/exists/name-ru/{nameRu}")
    public ResponseEntity<Boolean> existsByNameRu(@PathVariable String nameRu) {
        boolean exists = decisionMakingBodyService.existsByNameRu(nameRu);
        return ResponseEntity.ok(exists);
    }
}