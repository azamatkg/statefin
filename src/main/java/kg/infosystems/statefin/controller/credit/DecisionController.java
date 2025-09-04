package kg.infosystems.statefin.controller.credit;

import jakarta.validation.Valid;
import kg.infosystems.statefin.dto.request.credit.DecisionCreateRequest;
import kg.infosystems.statefin.dto.request.credit.DecisionSearchRequest;
import kg.infosystems.statefin.dto.response.credit.DecisionResponse;
import kg.infosystems.statefin.dto.update.credit.DecisionUpdateRequest;
import kg.infosystems.statefin.entity.credit.decision.DecisionStatus;
import kg.infosystems.statefin.service.DecisionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/decisions")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class DecisionController {

    private final DecisionService decisionService;

    @PostMapping
    @PreAuthorize("hasAuthority('DECISION_WRITE')")
    public ResponseEntity<DecisionResponse> createDecision(@Valid @RequestBody DecisionCreateRequest createDecisionDto) {
        DecisionResponse decision = decisionService.createDecision(createDecisionDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(decision);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('DECISION_READ')")
    public ResponseEntity<DecisionResponse> getDecisionById(@PathVariable UUID id) {
        DecisionResponse decision = decisionService.getDecisionById(id);
        return ResponseEntity.ok(decision);
    }

    @GetMapping
    @PreAuthorize("hasAuthority('DECISION_READ')")
    public ResponseEntity<Page<DecisionResponse>> getAllDecisions(@PageableDefault(size = 20) Pageable pageable) {
        Page<DecisionResponse> decisions = decisionService.getAllDecisions(pageable);
        return ResponseEntity.ok(decisions);
    }

    @GetMapping("/search")
    @PreAuthorize("hasAuthority('DECISION_READ')")
    public ResponseEntity<Page<DecisionResponse>> searchDecisions(
            @RequestParam String searchTerm,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<DecisionResponse> decisions = decisionService.searchDecisions(searchTerm, pageable);
        return ResponseEntity.ok(decisions);
    }

    @GetMapping("/search-and-filter")
    @PreAuthorize("hasAuthority('DECISION_READ')")
    public ResponseEntity<Page<DecisionResponse>> searchAndFilterDecisions(
            @RequestParam(required = false) String searchTerm,
            @RequestParam(required = false) Long decisionMakingBodyId,
            @RequestParam(required = false) Long decisionTypeId,
            @RequestParam(required = false) DecisionStatus status,
            @PageableDefault(size = 20) Pageable pageable) {

        DecisionSearchRequest searchAndFilterDto = DecisionSearchRequest.builder()
                .searchTerm(searchTerm)
                .decisionMakingBodyId(decisionMakingBodyId)
                .decisionTypeId(decisionTypeId)
                .status(status)
                .build();
        
        Page<DecisionResponse> decisions = decisionService.searchAndFilterDecisions(searchAndFilterDto, pageable);
        return ResponseEntity.ok(decisions);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('DECISION_WRITE')")
    public ResponseEntity<DecisionResponse> updateDecision(
            @PathVariable UUID id,
            @Valid @RequestBody DecisionUpdateRequest updateDecisionDto) {
        DecisionResponse decision = decisionService.updateDecision(id, updateDecisionDto);
        return ResponseEntity.ok(decision);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('DECISION_DELETE')")
    public ResponseEntity<Void> deleteDecision(@PathVariable UUID id) {
        decisionService.deleteDecision(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/exists/number/{number}")
    @PreAuthorize("hasAuthority('DECISION_READ')")
    public ResponseEntity<Boolean> existsByNumber(@PathVariable String number) {
        // URL decode the path variable since Spring doesn't automatically decode slashes
        String decodedNumber = java.net.URLDecoder.decode(number, java.nio.charset.StandardCharsets.UTF_8);
        boolean exists = decisionService.existsByNumber(decodedNumber);
        return ResponseEntity.ok(exists);
    }
}