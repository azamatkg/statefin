package kg.infosystems.statefin.controller.reference;

import jakarta.validation.Valid;
import kg.infosystems.statefin.dto.request.reference.CreditPurposeCreateRequest;
import kg.infosystems.statefin.dto.update.reference.CreditPurposeUpdateRequest;
import kg.infosystems.statefin.dto.response.reference.CreditPurposeResponse;
import kg.infosystems.statefin.entity.common.ReferenceEntityStatus;
import kg.infosystems.statefin.entity.reference.CreditPurpose;
import kg.infosystems.statefin.repository.CreditPurposeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/credit-purposes")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
@PreAuthorize("isAuthenticated()")
public class CreditPurposeController {

    private final CreditPurposeRepository creditPurposeRepository;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CreditPurposeResponse> createCreditPurpose(
            @Valid @RequestBody CreditPurposeCreateRequest createDto) {

        CreditPurpose creditPurpose = CreditPurpose.builder()
                .nameEn(createDto.getNameEn())
                .nameRu(createDto.getNameRu())
                .nameKg(createDto.getNameKg())
                .description(createDto.getDescription())
                .status(createDto.getStatus() != null ? createDto.getStatus() : ReferenceEntityStatus.ACTIVE)
                .build();

        CreditPurpose savedCreditPurpose = creditPurposeRepository.save(creditPurpose);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapToResponseDto(savedCreditPurpose));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CreditPurposeResponse> getCreditPurposeById(@PathVariable Long id) {
        CreditPurpose creditPurpose = creditPurposeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Credit purpose not found with ID: " + id));
        return ResponseEntity.ok(mapToResponseDto(creditPurpose));
    }

    @GetMapping
    public ResponseEntity<Page<CreditPurposeResponse>> getAllCreditPurposes(
            @PageableDefault(size = 20) Pageable pageable) {
        Page<CreditPurposeResponse> creditPurposes = creditPurposeRepository.findAll(pageable)
                .map(this::mapToResponseDto);
        return ResponseEntity.ok(creditPurposes);
    }

    @GetMapping("/active")
    public ResponseEntity<List<CreditPurposeResponse>> getActiveCreditPurposes() {
        List<CreditPurposeResponse> creditPurposes = creditPurposeRepository
                .findByStatusOrderByNameRuAsc(ReferenceEntityStatus.ACTIVE)
                .stream()
                .map(this::mapToResponseDto)
                .toList();
        return ResponseEntity.ok(creditPurposes);
    }

    @GetMapping("/search")
    public ResponseEntity<Page<CreditPurposeResponse>> searchCreditPurposes(
            @RequestParam String searchTerm,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<CreditPurposeResponse> creditPurposes = creditPurposeRepository
                .searchCreditPurposes(searchTerm, pageable)
                .map(this::mapToResponseDto);
        return ResponseEntity.ok(creditPurposes);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CreditPurposeResponse> updateCreditPurpose(
            @PathVariable Long id,
            @Valid @RequestBody CreditPurposeUpdateRequest updateDto) {

        CreditPurpose creditPurpose = creditPurposeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Credit purpose not found with ID: " + id));

        if (updateDto.getNameEn() != null) creditPurpose.setNameEn(updateDto.getNameEn());
        if (updateDto.getNameRu() != null) creditPurpose.setNameRu(updateDto.getNameRu());
        if (updateDto.getNameKg() != null) creditPurpose.setNameKg(updateDto.getNameKg());
        if (updateDto.getDescription() != null) creditPurpose.setDescription(updateDto.getDescription());
        if (updateDto.getStatus() != null) creditPurpose.setStatus(updateDto.getStatus());

        CreditPurpose updatedCreditPurpose = creditPurposeRepository.save(creditPurpose);
        return ResponseEntity.ok(mapToResponseDto(updatedCreditPurpose));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteCreditPurpose(@PathVariable Long id) {

        if (creditPurposeRepository.isReferencedByCreditPrograms(id)) {
            throw new RuntimeException("Cannot delete credit purpose as it is referenced by credit programs");
        }
        
        creditPurposeRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/referenced")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Boolean> isReferencedByCreditPrograms(@PathVariable Long id) {
        boolean referenced = creditPurposeRepository.isReferencedByCreditPrograms(id);
        return ResponseEntity.ok(referenced);
    }

    private CreditPurposeResponse mapToResponseDto(CreditPurpose creditPurpose) {
        return CreditPurposeResponse.builder()
                .id(creditPurpose.getId())
                .version(creditPurpose.getVersion())
                .nameEn(creditPurpose.getNameEn())
                .nameRu(creditPurpose.getNameRu())
                .nameKg(creditPurpose.getNameKg())
                .description(creditPurpose.getDescription())
                .status(creditPurpose.getStatus())
                .createdAt(creditPurpose.getCreatedAt())
                .updatedAt(creditPurpose.getUpdatedAt())
                .createdByUsername(creditPurpose.getCreatedBy() != null ? creditPurpose.getCreatedBy().getUsername() : null)
                .updatedByUsername(creditPurpose.getUpdatedBy() != null ? creditPurpose.getUpdatedBy().getUsername() : null)
                .isReferencedByCreditPrograms(creditPurposeRepository.isReferencedByCreditPrograms(creditPurpose.getId()))
                .build();
    }
}