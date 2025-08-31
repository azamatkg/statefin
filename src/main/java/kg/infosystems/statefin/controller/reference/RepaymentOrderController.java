package kg.infosystems.statefin.controller.reference;

import jakarta.validation.Valid;
import kg.infosystems.statefin.dto.request.reference.RepaymentOrderCreateRequest;
import kg.infosystems.statefin.dto.update.reference.RepaymentOrderUpdateRequest;
import kg.infosystems.statefin.dto.response.reference.RepaymentOrderResponse;
import kg.infosystems.statefin.entity.common.ReferenceEntityStatus;
import kg.infosystems.statefin.entity.reference.RepaymentOrder;
import kg.infosystems.statefin.repository.RepaymentOrderRepository;
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
@RequestMapping("/api/repayment-orders")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
@PreAuthorize("isAuthenticated()")
public class RepaymentOrderController {

    private final RepaymentOrderRepository repaymentOrderRepository;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RepaymentOrderResponse> createRepaymentOrder(
            @Valid @RequestBody RepaymentOrderCreateRequest createDto) {

        RepaymentOrder repaymentOrder = RepaymentOrder.builder()
                .nameEn(createDto.getNameEn())
                .nameRu(createDto.getNameRu())
                .nameKg(createDto.getNameKg())
                .description(createDto.getDescription())
                .status(createDto.getStatus() != null ? createDto.getStatus() : ReferenceEntityStatus.ACTIVE)
                .build();

        RepaymentOrder savedRepaymentOrder = repaymentOrderRepository.save(repaymentOrder);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapToResponseDto(savedRepaymentOrder));
    }

    @GetMapping("/{id}")
    public ResponseEntity<RepaymentOrderResponse> getRepaymentOrderById(@PathVariable Long id) {
        RepaymentOrder repaymentOrder = repaymentOrderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Repayment order not found with ID: " + id));
        return ResponseEntity.ok(mapToResponseDto(repaymentOrder));
    }

    @GetMapping
    public ResponseEntity<Page<RepaymentOrderResponse>> getAllRepaymentOrders(
            @PageableDefault(size = 20) Pageable pageable) {
        Page<RepaymentOrderResponse> repaymentOrders = repaymentOrderRepository.findAll(pageable)
                .map(this::mapToResponseDto);
        return ResponseEntity.ok(repaymentOrders);
    }

    @GetMapping("/active")
    public ResponseEntity<List<RepaymentOrderResponse>> getActiveRepaymentOrders() {
        List<RepaymentOrderResponse> repaymentOrders = repaymentOrderRepository
                .findByStatusOrderByNameRuAsc(ReferenceEntityStatus.ACTIVE)
                .stream()
                .map(this::mapToResponseDto)
                .toList();
        return ResponseEntity.ok(repaymentOrders);
    }

    @GetMapping("/search")
    public ResponseEntity<Page<RepaymentOrderResponse>> searchRepaymentOrders(
            @RequestParam String searchTerm,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<RepaymentOrderResponse> repaymentOrders = repaymentOrderRepository
                .searchRepaymentOrders(searchTerm, pageable)
                .map(this::mapToResponseDto);
        return ResponseEntity.ok(repaymentOrders);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RepaymentOrderResponse> updateRepaymentOrder(
            @PathVariable Long id,
            @Valid @RequestBody RepaymentOrderUpdateRequest updateDto) {

        RepaymentOrder repaymentOrder = repaymentOrderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Repayment order not found with ID: " + id));

        if (updateDto.getNameEn() != null) repaymentOrder.setNameEn(updateDto.getNameEn());
        if (updateDto.getNameRu() != null) repaymentOrder.setNameRu(updateDto.getNameRu());
        if (updateDto.getNameKg() != null) repaymentOrder.setNameKg(updateDto.getNameKg());
        if (updateDto.getDescription() != null) repaymentOrder.setDescription(updateDto.getDescription());
        if (updateDto.getStatus() != null) repaymentOrder.setStatus(updateDto.getStatus());

        RepaymentOrder updatedRepaymentOrder = repaymentOrderRepository.save(repaymentOrder);
        return ResponseEntity.ok(mapToResponseDto(updatedRepaymentOrder));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteRepaymentOrder(@PathVariable Long id) {

//        if (repaymentOrderRepository.isReferencedByCreditPrograms(id)) {
//            throw new RuntimeException("Cannot delete repayment order as it is referenced by credit programs");
//        }
        
        repaymentOrderRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

//    @GetMapping("/{id}/referenced")
//    @PreAuthorize("hasRole('ADMIN')")
//    public ResponseEntity<Boolean> isReferencedByCreditPrograms(@PathVariable Long id) {
////        boolean referenced = repaymentOrderRepository.isReferencedByCreditPrograms(id);
//        return ResponseEntity.ok(referenced);
//    }

    private RepaymentOrderResponse mapToResponseDto(RepaymentOrder repaymentOrder) {
        return RepaymentOrderResponse.builder()
                .id(repaymentOrder.getId())
                .version(repaymentOrder.getVersion())
                .nameEn(repaymentOrder.getNameEn())
                .nameRu(repaymentOrder.getNameRu())
                .nameKg(repaymentOrder.getNameKg())
                .description(repaymentOrder.getDescription())
                .status(repaymentOrder.getStatus())
                .createdAt(repaymentOrder.getCreatedAt())
                .updatedAt(repaymentOrder.getUpdatedAt())
                .createdByUsername(repaymentOrder.getCreatedBy() != null ? repaymentOrder.getCreatedBy().getUsername() : null)
                .updatedByUsername(repaymentOrder.getUpdatedBy() != null ? repaymentOrder.getUpdatedBy().getUsername() : null)
//                .isReferencedByCreditPrograms(repaymentOrderRepository.isReferencedByCreditPrograms(repaymentOrder.getId()))
                .build();
    }
}