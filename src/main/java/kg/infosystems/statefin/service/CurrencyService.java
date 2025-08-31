package kg.infosystems.statefin.service;

import kg.infosystems.statefin.dto.request.reference.CurrencyCreateRequest;
import kg.infosystems.statefin.dto.request.reference.CurrencySearchRequest;
import kg.infosystems.statefin.dto.update.reference.CurrencyUpdateRequest;
import kg.infosystems.statefin.dto.response.reference.CurrencyResponse;
import kg.infosystems.statefin.entity.common.ReferenceEntityStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CurrencyService {

    CurrencyResponse createCurrency(CurrencyCreateRequest createCurrencyDto);

    CurrencyResponse getCurrencyById(Long id);

    CurrencyResponse getCurrencyByCode(String code);

    Page<CurrencyResponse> getAllCurrencies(Pageable pageable);

    Page<CurrencyResponse> getAllActiveCurrencies(Pageable pageable);

    Page<CurrencyResponse> searchCurrencies(String searchTerm, Pageable pageable);

    Page<CurrencyResponse> searchCurrencies(String searchTerm, ReferenceEntityStatus status, Pageable pageable);

    CurrencyResponse updateCurrency(Long id, CurrencyUpdateRequest updateCurrencyDto);

    void deleteCurrency(Long id);

    CurrencyResponse activateCurrency(Long id);

    CurrencyResponse deactivateCurrency(Long id);

    boolean existsByCode(String code);

    boolean existsByNameRu(String nameRu);

    boolean isReferencedByCreditPrograms(Long id);

    Page<CurrencyResponse> getCurrenciesByStatus(ReferenceEntityStatus status, Pageable pageable);
}