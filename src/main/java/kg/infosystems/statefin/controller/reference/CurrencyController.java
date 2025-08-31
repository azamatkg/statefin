package kg.infosystems.statefin.controller.reference;

import jakarta.validation.Valid;
import kg.infosystems.statefin.dto.request.reference.CurrencyCreateRequest;
import kg.infosystems.statefin.dto.response.reference.CurrencyResponse;
import kg.infosystems.statefin.dto.update.reference.CurrencyUpdateRequest;
import kg.infosystems.statefin.entity.common.ReferenceEntityStatus;
import kg.infosystems.statefin.entity.reference.Currency;
import kg.infosystems.statefin.repository.CurrencyRepository;
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
@RequestMapping("/api/currencies")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
@PreAuthorize("isAuthenticated()")
public class CurrencyController {

    private final CurrencyRepository currencyRepository;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CurrencyResponse> createCurrency(
            @Valid @RequestBody CurrencyCreateRequest createDto) {

        if (currencyRepository.existsByCode(createDto.getCode())) {
            throw new RuntimeException("Currency with code " + createDto.getCode() + " already exists");
        }
        
        Currency currency = Currency.builder()
                .code(createDto.getCode())
                .nameEn(createDto.getNameEn())
                .nameRu(createDto.getNameRu())
                .nameKg(createDto.getNameKg())
                .description(createDto.getDescription())
                .status(createDto.getStatus() != null ? createDto.getStatus() : ReferenceEntityStatus.ACTIVE)
                .build();

        Currency savedCurrency = currencyRepository.save(currency);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapToResponseDto(savedCurrency));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CurrencyResponse> getCurrencyById(@PathVariable Long id) {
        Currency currency = currencyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Currency not found with ID: " + id));
        return ResponseEntity.ok(mapToResponseDto(currency));
    }

    @GetMapping("/code/{code}")
    public ResponseEntity<CurrencyResponse> getCurrencyByCode(@PathVariable String code) {
        Currency currency = currencyRepository.findByCode(code)
                .orElseThrow(() -> new RuntimeException("Currency not found with code: " + code));
        return ResponseEntity.ok(mapToResponseDto(currency));
    }

    @GetMapping
    public ResponseEntity<Page<CurrencyResponse>> getAllCurrencies(
            @PageableDefault(size = 20) Pageable pageable) {
        Page<CurrencyResponse> currencies = currencyRepository.findAll(pageable)
                .map(this::mapToResponseDto);
        return ResponseEntity.ok(currencies);
    }

    @GetMapping("/active")
    public ResponseEntity<List<CurrencyResponse>> getActiveCurrencies() {
        List<CurrencyResponse> currencies = currencyRepository
                .findByStatusOrderByCode(ReferenceEntityStatus.ACTIVE)
                .stream()
                .map(this::mapToResponseDto)
                .toList();
        return ResponseEntity.ok(currencies);
    }

    @GetMapping("/search")
    public ResponseEntity<Page<CurrencyResponse>> searchCurrencies(
            @RequestParam String searchTerm,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<CurrencyResponse> currencies = currencyRepository
                .searchCurrencies(searchTerm, pageable)
                .map(this::mapToResponseDto);
        return ResponseEntity.ok(currencies);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CurrencyResponse> updateCurrency(
            @PathVariable Long id,
            @Valid @RequestBody CurrencyUpdateRequest updateDto) {

        Currency currency = currencyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Currency not found with ID: " + id));

        if (updateDto.getNameEn() != null) currency.setNameEn(updateDto.getNameEn());
        if (updateDto.getNameRu() != null) currency.setNameRu(updateDto.getNameRu());
        if (updateDto.getNameKg() != null) currency.setNameKg(updateDto.getNameKg());
        if (updateDto.getDescription() != null) currency.setDescription(updateDto.getDescription());
        if (updateDto.getStatus() != null) currency.setStatus(updateDto.getStatus());

        Currency updatedCurrency = currencyRepository.save(currency);
        return ResponseEntity.ok(mapToResponseDto(updatedCurrency));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteCurrency(@PathVariable Long id) {

        if (currencyRepository.isReferencedByCreditPrograms(id)) {
            throw new RuntimeException("Cannot delete currency as it is referenced by credit programs");
        }
        
        currencyRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/exists/code/{code}")
    public ResponseEntity<Boolean> existsByCode(@PathVariable String code) {
        boolean exists = currencyRepository.existsByCode(code);
        return ResponseEntity.ok(exists);
    }

    @GetMapping("/{id}/referenced")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Boolean> isReferencedByCreditPrograms(@PathVariable Long id) {
        boolean referenced = currencyRepository.isReferencedByCreditPrograms(id);
        return ResponseEntity.ok(referenced);
    }

    private CurrencyResponse mapToResponseDto(Currency currency) {
        return CurrencyResponse.builder()
                .id(currency.getId())
                .version(currency.getVersion())
                .code(currency.getCode())
                .nameEn(currency.getNameEn())
                .nameRu(currency.getNameRu())
                .nameKg(currency.getNameKg())
                .description(currency.getDescription())
                .status(currency.getStatus())
                .createdAt(currency.getCreatedAt())
                .updatedAt(currency.getUpdatedAt())
                .createdByUsername(currency.getCreatedBy() != null ? currency.getCreatedBy().getUsername() : null)
                .updatedByUsername(currency.getUpdatedBy() != null ? currency.getUpdatedBy().getUsername() : null)
                .isReferencedByCreditPrograms(currencyRepository.isReferencedByCreditPrograms(currency.getId()))
                .build();
    }
}