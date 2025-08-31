package kg.infosystems.statefin.service;

import kg.infosystems.statefin.dto.request.reference.FloatingRateTypeCreateRequest;
import kg.infosystems.statefin.dto.request.reference.FloatingRateTypeSearchRequest;
import kg.infosystems.statefin.dto.update.reference.FloatingRateTypeUpdateRequest;
import kg.infosystems.statefin.dto.response.reference.FloatingRateTypeResponse;
import kg.infosystems.statefin.entity.common.ReferenceEntityStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FloatingRateTypeService {

    FloatingRateTypeResponse createFloatingRateType(FloatingRateTypeCreateRequest createFloatingRateTypeDto);

    FloatingRateTypeResponse getFloatingRateTypeById(Long id);

    Page<FloatingRateTypeResponse> getAllFloatingRateTypes(Pageable pageable);

    Page<FloatingRateTypeResponse> getAllActiveFloatingRateTypes(Pageable pageable);

    Page<FloatingRateTypeResponse> searchFloatingRateTypes(String searchTerm, Pageable pageable);

    Page<FloatingRateTypeResponse> searchFloatingRateTypes(String searchTerm, ReferenceEntityStatus status, Pageable pageable);

    FloatingRateTypeResponse updateFloatingRateType(Long id, FloatingRateTypeUpdateRequest updateFloatingRateTypeDto);

    void deleteFloatingRateType(Long id);

    FloatingRateTypeResponse activateFloatingRateType(Long id);

    FloatingRateTypeResponse deactivateFloatingRateType(Long id);

    boolean existsByNameRu(String nameRu);

//    boolean isReferencedByCreditPrograms(Long id);

    Page<FloatingRateTypeResponse> getFloatingRateTypesByStatus(ReferenceEntityStatus status, Pageable pageable);
}