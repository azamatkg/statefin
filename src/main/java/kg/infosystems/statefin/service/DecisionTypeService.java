package kg.infosystems.statefin.service;

import kg.infosystems.statefin.dto.request.reference.DecisionTypeCreateRequest;
import kg.infosystems.statefin.dto.update.reference.DecisionTypeUpdateRequest;
import kg.infosystems.statefin.dto.response.reference.DecisionTypeResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface DecisionTypeService {

    DecisionTypeResponse createDecisionType(DecisionTypeCreateRequest createDecisionTypeDto);

    DecisionTypeResponse getDecisionTypeById(Long id);

    Page<DecisionTypeResponse> getAllDecisionTypes(Pageable pageable);

    Page<DecisionTypeResponse> searchDecisionTypes(String searchTerm, Pageable pageable);

    DecisionTypeResponse updateDecisionType(Long id, DecisionTypeUpdateRequest updateDecisionTypeDto);

    void deleteDecisionType(Long id);

    boolean existsByNameRu(String nameRu);

    Page<DecisionTypeResponse> getAllActiveDecisionTypes(Pageable pageable);
}