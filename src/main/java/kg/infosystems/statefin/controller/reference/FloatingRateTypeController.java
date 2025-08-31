package kg.infosystems.statefin.controller.reference;

import jakarta.validation.Valid;
import kg.infosystems.statefin.dto.request.reference.FloatingRateTypeCreateRequest;
import kg.infosystems.statefin.dto.update.reference.FloatingRateTypeUpdateRequest;
import kg.infosystems.statefin.dto.response.reference.FloatingRateTypeResponse;
import kg.infosystems.statefin.entity.common.ReferenceEntityStatus;
import kg.infosystems.statefin.entity.reference.FloatingRateType;
import kg.infosystems.statefin.repository.FloatingRateTypeRepository;
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
@RequestMapping("/api/floating-rate-types")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
@PreAuthorize("isAuthenticated()")
public class FloatingRateTypeController {

    private final FloatingRateTypeRepository floatingRateTypeRepository;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<FloatingRateTypeResponse> createFloatingRateType(
            @Valid @RequestBody FloatingRateTypeCreateRequest createDto) {

        FloatingRateType floatingRateType = FloatingRateType.builder()
                .nameEn(createDto.getNameEn())
                .nameRu(createDto.getNameRu())
                .nameKg(createDto.getNameKg())
                .description(createDto.getDescription())
                .status(createDto.getStatus() != null ? createDto.getStatus() : ReferenceEntityStatus.ACTIVE)
                .build();

        FloatingRateType savedFloatingRateType = floatingRateTypeRepository.save(floatingRateType);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapToResponseDto(savedFloatingRateType));
    }

    @GetMapping("/{id}")
    public ResponseEntity<FloatingRateTypeResponse> getFloatingRateTypeById(@PathVariable Long id) {
        FloatingRateType floatingRateType = floatingRateTypeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Floating rate type not found with ID: " + id));
        return ResponseEntity.ok(mapToResponseDto(floatingRateType));
    }

    @GetMapping
    public ResponseEntity<Page<FloatingRateTypeResponse>> getAllFloatingRateTypes(
            @PageableDefault(size = 20) Pageable pageable) {
        Page<FloatingRateTypeResponse> floatingRateTypes = floatingRateTypeRepository.findAll(pageable)
                .map(this::mapToResponseDto);
        return ResponseEntity.ok(floatingRateTypes);
    }

    @GetMapping("/active")
    public ResponseEntity<List<FloatingRateTypeResponse>> getActiveFloatingRateTypes() {
        List<FloatingRateTypeResponse> floatingRateTypes = floatingRateTypeRepository
                .findByStatusOrderByNameRuAsc(ReferenceEntityStatus.ACTIVE)
                .stream()
                .map(this::mapToResponseDto)
                .toList();
        return ResponseEntity.ok(floatingRateTypes);
    }

    @GetMapping("/search")
    public ResponseEntity<Page<FloatingRateTypeResponse>> searchFloatingRateTypes(
            @RequestParam String searchTerm,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<FloatingRateTypeResponse> floatingRateTypes = floatingRateTypeRepository
                .searchFloatingRateTypes(searchTerm, pageable)
                .map(this::mapToResponseDto);
        return ResponseEntity.ok(floatingRateTypes);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<FloatingRateTypeResponse> updateFloatingRateType(
            @PathVariable Long id,
            @Valid @RequestBody FloatingRateTypeUpdateRequest updateDto) {

        FloatingRateType floatingRateType = floatingRateTypeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Floating rate type not found with ID: " + id));

        if (updateDto.getNameEn() != null) floatingRateType.setNameEn(updateDto.getNameEn());
        if (updateDto.getNameRu() != null) floatingRateType.setNameRu(updateDto.getNameRu());
        if (updateDto.getNameKg() != null) floatingRateType.setNameKg(updateDto.getNameKg());
        if (updateDto.getDescription() != null) floatingRateType.setDescription(updateDto.getDescription());
        if (updateDto.getStatus() != null) floatingRateType.setStatus(updateDto.getStatus());

        FloatingRateType updatedFloatingRateType = floatingRateTypeRepository.save(floatingRateType);
        return ResponseEntity.ok(mapToResponseDto(updatedFloatingRateType));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteFloatingRateType(@PathVariable Long id) {

        if (floatingRateTypeRepository.isReferencedByCreditPrograms(id)) {
            throw new RuntimeException("Cannot delete floating rate type as it is referenced by credit programs");
        }
        
        floatingRateTypeRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/referenced")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Boolean> isReferencedByCreditPrograms(@PathVariable Long id) {
        boolean referenced = floatingRateTypeRepository.isReferencedByCreditPrograms(id);
        return ResponseEntity.ok(referenced);
    }

    private FloatingRateTypeResponse mapToResponseDto(FloatingRateType floatingRateType) {
        return FloatingRateTypeResponse.builder()
                .id(floatingRateType.getId())
                .version(floatingRateType.getVersion())
                .nameEn(floatingRateType.getNameEn())
                .nameRu(floatingRateType.getNameRu())
                .nameKg(floatingRateType.getNameKg())
                .description(floatingRateType.getDescription())
                .status(floatingRateType.getStatus())
                .createdAt(floatingRateType.getCreatedAt())
                .updatedAt(floatingRateType.getUpdatedAt())
                .createdByUsername(floatingRateType.getCreatedBy() != null ? floatingRateType.getCreatedBy().getUsername() : null)
                .updatedByUsername(floatingRateType.getUpdatedBy() != null ? floatingRateType.getUpdatedBy().getUsername() : null)
                .isReferencedByCreditPrograms(floatingRateTypeRepository.isReferencedByCreditPrograms(floatingRateType.getId()))
                .build();
    }
}