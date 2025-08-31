package kg.infosystems.statefin.service.impl;

import kg.infosystems.statefin.dto.request.reference.CurrencyCreateRequest;
import kg.infosystems.statefin.dto.request.reference.CurrencySearchRequest;
import kg.infosystems.statefin.dto.update.reference.CurrencyUpdateRequest;
import kg.infosystems.statefin.dto.response.reference.CurrencyResponse;
import kg.infosystems.statefin.entity.common.ReferenceEntityStatus;
import kg.infosystems.statefin.entity.reference.Currency;
import kg.infosystems.statefin.exception.ResourceNotFoundException;
import kg.infosystems.statefin.repository.CurrencyRepository;
import kg.infosystems.statefin.service.CurrencyService;
import kg.infosystems.statefin.specification.CurrencySpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CurrencyServiceImpl implements CurrencyService {

    private final CurrencyRepository currencyRepository;

    @Override
    @Transactional
    public CurrencyResponse createCurrency(CurrencyCreateRequest createCurrencyDto) {

        if (currencyRepository.existsByCode(createCurrencyDto.getCode())) {
            throw new IllegalArgumentException("Currency with code " + createCurrencyDto.getCode() + " already exists");
        }

        if (currencyRepository.existsByNameRu(createCurrencyDto.getNameRu())) {
            throw new IllegalArgumentException("Currency with nameRu " + createCurrencyDto.getNameRu() + " already exists");
        }

        Currency currency = Currency.builder()
                .code(createCurrencyDto.getCode().toUpperCase())
                .nameEn(createCurrencyDto.getNameEn())
                .nameRu(createCurrencyDto.getNameRu())
                .nameKg(createCurrencyDto.getNameKg())
                .description(createCurrencyDto.getDescription())
                .status(createCurrencyDto.getStatus() != null ? createCurrencyDto.getStatus() : ReferenceEntityStatus.ACTIVE)
                .build();

        Currency savedCurrency = currencyRepository.save(currency);

        return mapToResponseDto(savedCurrency);
    }

    @Override
    @Transactional(readOnly = true)
    public CurrencyResponse getCurrencyById(Long id) {
        Currency currency = currencyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Currency not found with id: " + id));
        return mapToResponseDto(currency);
    }

    @Override
    @Transactional(readOnly = true)
    public CurrencyResponse getCurrencyByCode(String code) {
        Currency currency = currencyRepository.findByCode(code.toUpperCase())
                .orElseThrow(() -> new ResourceNotFoundException("Currency not found with code: " + code));
        return mapToResponseDto(currency);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CurrencyResponse> getAllCurrencies(Pageable pageable) {
        return currencyRepository.findAll(pageable)
                .map(this::mapToResponseDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CurrencyResponse> getAllActiveCurrencies(Pageable pageable) {
        return currencyRepository.findByStatusOrderByCodeAsc(ReferenceEntityStatus.ACTIVE, pageable)
                .map(this::mapToResponseDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CurrencyResponse> searchCurrencies(String searchTerm, Pageable pageable) {

        Specification<Currency> spec = CurrencySpecification.searchByTerm(searchTerm);
        return currencyRepository.findAll(spec, pageable)
                .map(this::mapToResponseDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CurrencyResponse> searchCurrencies(String searchTerm, ReferenceEntityStatus status, Pageable pageable) {

        Specification<Currency> spec = CurrencySpecification.searchByTermAndStatus(searchTerm, status);
        return currencyRepository.findAll(spec, pageable)
                .map(this::mapToResponseDto);
    }

    @Override
    @Transactional
    public CurrencyResponse updateCurrency(Long id, CurrencyUpdateRequest updateCurrencyDto) {
        Currency currency = currencyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Currency not found with id: " + id));

        if (updateCurrencyDto.getNameEn() != null) {
            currency.setNameEn(updateCurrencyDto.getNameEn());
        }
        if (updateCurrencyDto.getNameRu() != null) {
            if (!currency.getNameRu().equals(updateCurrencyDto.getNameRu()) &&
                    currencyRepository.existsByNameRu(updateCurrencyDto.getNameRu())) {
                throw new IllegalArgumentException("Currency with nameRu " + updateCurrencyDto.getNameRu() + " already exists");
            }
            currency.setNameRu(updateCurrencyDto.getNameRu());
        }
        if (updateCurrencyDto.getNameKg() != null) {
            currency.setNameKg(updateCurrencyDto.getNameKg());
        }
        if (updateCurrencyDto.getDescription() != null) {
            currency.setDescription(updateCurrencyDto.getDescription());
        }
        if (updateCurrencyDto.getStatus() != null) {
            currency.setStatus(updateCurrencyDto.getStatus());
        }

        Currency updatedCurrency = currencyRepository.save(currency);
        return mapToResponseDto(updatedCurrency);
    }

    @Override
    @Transactional
    public void deleteCurrency(Long id) {
        Currency currency = currencyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Currency not found with id: " + id));

        //TODO uncomment later
//        if (currencyRepository.isReferencedByCreditPrograms(id)) {
//            throw new IllegalStateException("Currency with ID " + id + " cannot be deleted as it is referenced by credit programs");
//        }

        currencyRepository.deleteById(id);
    }

    @Override
    @Transactional
    public CurrencyResponse activateCurrency(Long id) {
        Currency currency = currencyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Currency not found with id: " + id));

        currency.setStatus(ReferenceEntityStatus.ACTIVE);
        Currency updatedCurrency = currencyRepository.save(currency);
        return mapToResponseDto(updatedCurrency);
    }

    @Override
    @Transactional
    public CurrencyResponse deactivateCurrency(Long id) {
        Currency currency = currencyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Currency not found with id: " + id));

        currency.setStatus(ReferenceEntityStatus.INACTIVE);
        Currency updatedCurrency = currencyRepository.save(currency);
        return mapToResponseDto(updatedCurrency);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByCode(String code) {
        return currencyRepository.existsByCode(code.toUpperCase());
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByNameRu(String nameRu) {
        return currencyRepository.existsByNameRu(nameRu);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isReferencedByCreditPrograms(Long id) {
        return currencyRepository.isReferencedByCreditPrograms(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CurrencyResponse> getCurrenciesByStatus(ReferenceEntityStatus status, Pageable pageable) {
        return currencyRepository.findByStatusOrderByCodeAsc(status, pageable)
                .map(this::mapToResponseDto);
    }

    private CurrencyResponse mapToResponseDto(Currency currency) {
        boolean isReferenced = currencyRepository.isReferencedByCreditPrograms(currency.getId());

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
                .isReferencedByCreditPrograms(isReferenced)
                .build();
    }
}