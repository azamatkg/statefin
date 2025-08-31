package kg.infosystems.statefin.service;

import kg.infosystems.statefin.dto.request.credit.DecisionCreateRequest;
import kg.infosystems.statefin.dto.request.credit.DecisionSearchRequest;
import kg.infosystems.statefin.dto.update.credit.DecisionUpdateRequest;
import kg.infosystems.statefin.dto.response.credit.DecisionResponse;
import kg.infosystems.statefin.dto.response.reference.DecisionTypeResponse;
import kg.infosystems.statefin.dto.response.reference.DecisionMakingBodyResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface DecisionService {

    DecisionResponse createDecision(DecisionCreateRequest createDecisionDto);

    DecisionResponse getDecisionById(UUID id);

    Page<DecisionResponse> getAllDecisions(Pageable pageable);

    Page<DecisionResponse> searchDecisions(String searchTerm, Pageable pageable);

    Page<DecisionResponse> searchAndFilterDecisions(DecisionSearchRequest searchAndFilterDto, Pageable pageable);

    DecisionResponse updateDecision(UUID id, DecisionUpdateRequest updateDecisionDto);

    void deleteDecision(UUID id);

    boolean existsByNumber(String number);

    DecisionTypeResponse getDecisionTypeById(Long id);

    DecisionMakingBodyResponse getDecisionMakingBodyById(Long id);

    //TODO uncomment later
//    DocumentFilePackageDto.DocumentFilePackageResponseDto getDocumentFilePackageById(UUID id);

    void updateDecisionStatusFromCreditPrograms(UUID decisionId);
}