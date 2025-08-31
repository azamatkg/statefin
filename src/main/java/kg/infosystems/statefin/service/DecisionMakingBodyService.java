package kg.infosystems.statefin.service;

import kg.infosystems.statefin.dto.request.reference.DecisionMakingBodyCreateRequest;
import kg.infosystems.statefin.dto.update.reference.DecisionMakingBodyUpdateRequest;
import kg.infosystems.statefin.dto.response.reference.DecisionMakingBodyResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface DecisionMakingBodyService {

    DecisionMakingBodyResponse createDecisionMakingBody(DecisionMakingBodyCreateRequest createDecisionMakingBodyDto);

    DecisionMakingBodyResponse getDecisionMakingBodyById(Long id);

    Page<DecisionMakingBodyResponse> getAllDecisionMakingBodies(Pageable pageable);

    Page<DecisionMakingBodyResponse> searchDecisionMakingBodies(String searchTerm, Pageable pageable);

    DecisionMakingBodyResponse updateDecisionMakingBody(Long id, DecisionMakingBodyUpdateRequest updateDecisionMakingBodyDto);

    void deleteDecisionMakingBody(Long id);

    boolean existsByNameRu(String nameRu);

    Page<DecisionMakingBodyResponse> getAllActiveDecisionMakingBodies(Pageable pageable);
}