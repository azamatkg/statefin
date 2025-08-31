package kg.infosystems.statefin.service.impl;

import kg.infosystems.statefin.dto.request.credit.DecisionCreateRequest;
import kg.infosystems.statefin.dto.request.credit.DecisionSearchRequest;
import kg.infosystems.statefin.dto.update.credit.DecisionUpdateRequest;
import kg.infosystems.statefin.dto.response.credit.DecisionResponse;
import kg.infosystems.statefin.dto.response.reference.DecisionTypeResponse;
import kg.infosystems.statefin.dto.response.reference.DecisionMakingBodyResponse;
import kg.infosystems.statefin.entity.credit.decision.Decision;
import kg.infosystems.statefin.entity.reference.DecisionMakingBody;
import kg.infosystems.statefin.entity.reference.DecisionType;
import kg.infosystems.statefin.exception.DecisionFinalStateException;
import kg.infosystems.statefin.exception.ResourceNotFoundException;
import kg.infosystems.statefin.repository.DecisionMakingBodyRepository;
import kg.infosystems.statefin.repository.DecisionRepository;
import kg.infosystems.statefin.repository.DecisionTypeRepository;
import kg.infosystems.statefin.service.DecisionService;
import kg.infosystems.statefin.specification.DecisionSpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class DecisionServiceImpl implements DecisionService {

    private final DecisionRepository decisionRepository;
    private final DecisionTypeRepository decisionTypeRepository;
    private final DecisionMakingBodyRepository decisionMakingBodyRepository;
//    private final DocumentFilePackageRepository documentFilePackageRepository;

    @Override
    @Transactional
    public DecisionResponse createDecision(DecisionCreateRequest createDecisionDto) {
        log.info("Creating new decision with number: {}", createDecisionDto.getNumber());

        if (decisionRepository.existsByNumber(createDecisionDto.getNumber())) {
            throw new IllegalArgumentException("Decision with number " + createDecisionDto.getNumber() + " already exists");
        }

        DecisionType decisionType = decisionTypeRepository.findById(createDecisionDto.getDecisionTypeId())
                .orElseThrow(() -> new ResourceNotFoundException("DecisionType not found with id: " + createDecisionDto.getDecisionTypeId()));

        DecisionMakingBody decisionMakingBody = decisionMakingBodyRepository.findById(createDecisionDto.getDecisionMakingBodyId())
                .orElseThrow(() -> new ResourceNotFoundException("DecisionMakingBody not found with id: " + createDecisionDto.getDecisionMakingBodyId()));
//TODO uncomment later

//        DocumentFilePackage documentPackage = null;
//        if (createDecisionDto.getDocumentPackageId() != null) {
//            documentPackage = documentFilePackageRepository.findById(createDecisionDto.getDocumentPackageId())
//                    .orElseThrow(() -> new ResourceNotFoundException("DocumentFilePackage not found with id: " + createDecisionDto.getDocumentPackageId()));
//        }

        Decision decision = Decision.builder()
                .nameEn(createDecisionDto.getNameEn())
                .nameRu(createDecisionDto.getNameRu())
                .nameKg(createDecisionDto.getNameKg())
                .date(createDecisionDto.getDate())
                .number(createDecisionDto.getNumber())
                .decisionMakingBody(decisionMakingBody)
                .decisionType(decisionType)
                .description(createDecisionDto.getDescription())
                .status(createDecisionDto.getStatus())
//                .documentPackage(documentPackage)
                .build();

        Decision savedDecision = decisionRepository.save(decision);

        return mapToResponseDto(savedDecision);
    }

    @Override
    @Transactional(readOnly = true)
    public DecisionResponse getDecisionById(UUID id) {
        Decision decision = decisionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Decision not found with id: " + id));
        return mapToResponseDto(decision);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DecisionResponse> getAllDecisions(Pageable pageable) {
        return decisionRepository.findAll(pageable)
                .map(this::mapToResponseDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DecisionResponse> searchDecisions(String searchTerm, Pageable pageable) {

        Specification<Decision> spec = DecisionSpecification.searchByTerm(searchTerm);
        return decisionRepository.findAll(spec, pageable)
                .map(this::mapToResponseDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DecisionResponse> searchAndFilterDecisions(DecisionSearchRequest searchAndFilterDto, Pageable pageable) {

        Specification<Decision> spec = DecisionSpecification.buildSearchAndFilterSpecification(searchAndFilterDto);
        return decisionRepository.findAll(spec, pageable)
                .map(this::mapToResponseDto);
    }

    @Override
    @Transactional
    public DecisionResponse updateDecision(UUID id, DecisionUpdateRequest updateDecisionDto) {
        Decision decision = decisionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Decision not found with id: " + id));

        if (decision.isFinalState()) {
            throw new DecisionFinalStateException("Decision with ID " + id + " is in a final state and cannot be modified");
        }

        if (updateDecisionDto.getNameEn() != null) {
            decision.setNameEn(updateDecisionDto.getNameEn());
        }
        if (updateDecisionDto.getNameRu() != null) {
            decision.setNameRu(updateDecisionDto.getNameRu());
        }
        if (updateDecisionDto.getNameKg() != null) {
            decision.setNameKg(updateDecisionDto.getNameKg());
        }
        if (updateDecisionDto.getDate() != null) {
            decision.setDate(updateDecisionDto.getDate());
        }
        if (updateDecisionDto.getNumber() != null) {
            if (!decision.getNumber().equals(updateDecisionDto.getNumber()) &&
                    decisionRepository.existsByNumber(updateDecisionDto.getNumber())) {
                throw new IllegalArgumentException("Decision with number " + updateDecisionDto.getNumber() + " already exists");
            }
            decision.setNumber(updateDecisionDto.getNumber());
        }
        if (updateDecisionDto.getDecisionMakingBodyId() != null) {
            DecisionMakingBody decisionMakingBody = decisionMakingBodyRepository.findById(updateDecisionDto.getDecisionMakingBodyId())
                    .orElseThrow(() -> new ResourceNotFoundException("DecisionMakingBody not found with id: " + updateDecisionDto.getDecisionMakingBodyId()));
            decision.setDecisionMakingBody(decisionMakingBody);
        }
        if (updateDecisionDto.getDecisionTypeId() != null) {
            DecisionType decisionType = decisionTypeRepository.findById(updateDecisionDto.getDecisionTypeId())
                    .orElseThrow(() -> new ResourceNotFoundException("DecisionType not found with id: " + updateDecisionDto.getDecisionTypeId()));
            decision.setDecisionType(decisionType);
        }
        if (updateDecisionDto.getDescription() != null) {
            decision.setDescription(updateDecisionDto.getDescription());
        }
        if (updateDecisionDto.getStatus() != null) {
            decision.setStatus(updateDecisionDto.getStatus());
        }
        //TODO uncomment later
//        if (updateDecisionDto.getDocumentPackageId() != null) {
//            DocumentFilePackage documentPackage = documentFilePackageRepository.findById(updateDecisionDto.getDocumentPackageId())
//                    .orElseThrow(() -> new ResourceNotFoundException("DocumentFilePackage not found with id: " + updateDecisionDto.getDocumentPackageId()));
//            decision.setDocumentPackage(documentPackage);
//        }

        Decision updatedDecision = decisionRepository.save(decision);
        return mapToResponseDto(updatedDecision);
    }

    @Override
    @Transactional
    public void deleteDecision(UUID id) {
        Decision decision = decisionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Decision not found with id: " + id));

//        if (!decision.canBeDeleted()) {
//            throw new IllegalStateException("Decision with ID " + id + " cannot be deleted as it has associated credit programs");
//        }

        if (decision.isFinalState()) {
            throw new DecisionFinalStateException("Decision with ID " + id + " is in a final state and cannot be deleted");
        }

        decisionRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByNumber(String number) {
        return decisionRepository.existsByNumber(number);
    }

    @Override
    @Transactional(readOnly = true)
    public DecisionTypeResponse getDecisionTypeById(Long id) {
        DecisionType decisionType = decisionTypeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("DecisionType not found with id: " + id));
        
        return DecisionTypeResponse.builder()
                .id(decisionType.getId())
                .nameEn(decisionType.getNameEn())
                .nameRu(decisionType.getNameRu())
                .nameKg(decisionType.getNameKg())
                .description(decisionType.getDescription())
                .status(decisionType.getStatus())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public DecisionMakingBodyResponse getDecisionMakingBodyById(Long id) {
        DecisionMakingBody decisionMakingBody = decisionMakingBodyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("DecisionMakingBody not found with id: " + id));
        
        return DecisionMakingBodyResponse.builder()
                .id(decisionMakingBody.getId())
                .nameEn(decisionMakingBody.getNameEn())
                .nameRu(decisionMakingBody.getNameRu())
                .nameKg(decisionMakingBody.getNameKg())
                .description(decisionMakingBody.getDescription())
                .status(decisionMakingBody.getStatus())
                .build();
    }

//    @Override
//    @Transactional(readOnly = true)
//    public DocumentFilePackageDto.DocumentFilePackageResponseDto getDocumentFilePackageById(UUID id) {
//        DocumentFilePackage documentFilePackage = documentFilePackageRepository.findById(id)
//                .orElseThrow(() -> new ResourceNotFoundException("DocumentFilePackage not found with id: " + id));
//
//        return DocumentFilePackageDto.DocumentFilePackageResponseDto.builder()
//                .id(documentFilePackage.getId())
//                .name(documentFilePackage.getName())
//                .description(documentFilePackage.getDescription())
//                .createdAt(documentFilePackage.getCreatedAt())
//                .updatedAt(documentFilePackage.getUpdatedAt())
//                .build();
//    }

    private DecisionResponse mapToResponseDto(Decision decision) {
        DecisionMakingBody decisionMakingBody = decision.getDecisionMakingBody();
        DecisionType decisionType = decision.getDecisionType();

        return DecisionResponse.builder()
                .id(decision.getId())
                .nameEn(decision.getNameEn())
                .nameRu(decision.getNameRu())
                .nameKg(decision.getNameKg())
                .date(decision.getDate())
                .number(decision.getNumber())
                .decisionMakingBodyNameEn(decisionMakingBody != null ? decisionMakingBody.getNameEn() : null)
                .decisionMakingBodyNameRu(decisionMakingBody != null ? decisionMakingBody.getNameRu() : null)
                .decisionMakingBodyNameKg(decisionMakingBody != null ? decisionMakingBody.getNameKg() : null)
                .decisionTypeNameEn(decisionType != null ? decisionType.getNameEn() : null)
                .decisionTypeNameRu(decisionType != null ? decisionType.getNameRu() : null)
                .decisionTypeNameKg(decisionType != null ? decisionType.getNameKg() : null)
                .description(decision.getDescription())
                .status(decision.getStatus())
//                .documentPackageId(decision.getDocumentPackage() != null ? decision.getDocumentPackage().getId() : null)
                .build();
    }

    @Override
    @Transactional
    public void updateDecisionStatusFromCreditPrograms(UUID decisionId) {

        Decision decision = decisionRepository.findById(decisionId)
                .orElseThrow(() -> new ResourceNotFoundException("Decision not found with id: " + decisionId));
        
//        decision.updateStatusFromCreditPrograms();
        
        decisionRepository.save(decision);
        
    }
}