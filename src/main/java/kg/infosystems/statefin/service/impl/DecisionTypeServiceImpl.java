package kg.infosystems.statefin.service.impl;

import kg.infosystems.statefin.dto.request.reference.DecisionTypeCreateRequest;
import kg.infosystems.statefin.dto.update.reference.DecisionTypeUpdateRequest;
import kg.infosystems.statefin.dto.response.reference.DecisionTypeResponse;
import kg.infosystems.statefin.entity.common.ReferenceEntityStatus;
import kg.infosystems.statefin.entity.reference.DecisionType;
import kg.infosystems.statefin.exception.ResourceNotFoundException;
import kg.infosystems.statefin.repository.DecisionTypeRepository;
import kg.infosystems.statefin.service.DecisionTypeService;
import kg.infosystems.statefin.specification.DecisionTypeSpecification;
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
public class DecisionTypeServiceImpl implements DecisionTypeService {

    private final DecisionTypeRepository decisionTypeRepository;

    @Override
    @Transactional
    public DecisionTypeResponse createDecisionType(DecisionTypeCreateRequest createDecisionTypeDto) {

        if (decisionTypeRepository.existsByNameRu(createDecisionTypeDto.getNameRu())) {
            throw new IllegalArgumentException("DecisionType with nameRu " + createDecisionTypeDto.getNameRu() + " already exists");
        }

        DecisionType decisionType = DecisionType.builder()
                .nameEn(createDecisionTypeDto.getNameEn())
                .nameRu(createDecisionTypeDto.getNameRu())
                .nameKg(createDecisionTypeDto.getNameKg())
                .description(createDecisionTypeDto.getDescription())
                .status(createDecisionTypeDto.getStatus())
                .build();

        DecisionType savedDecisionType = decisionTypeRepository.save(decisionType);

        return mapToResponseDto(savedDecisionType);
    }

    @Override
    @Transactional(readOnly = true)
    public DecisionTypeResponse getDecisionTypeById(Long id) {
        log.info("Fetching decision type by ID: {}", id);
        DecisionType decisionType = decisionTypeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("DecisionType not found with id: " + id));
        return mapToResponseDto(decisionType);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DecisionTypeResponse> getAllDecisionTypes(Pageable pageable) {
        return decisionTypeRepository.findAll(pageable)
                .map(this::mapToResponseDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DecisionTypeResponse> searchDecisionTypes(String searchTerm, Pageable pageable) {

        Specification<DecisionType> spec = DecisionTypeSpecification.searchByTerm(searchTerm);
        return decisionTypeRepository.findAll(spec, pageable)
                .map(this::mapToResponseDto);
    }

    @Override
    @Transactional
    public DecisionTypeResponse updateDecisionType(Long id, DecisionTypeUpdateRequest updateDecisionTypeDto) {
        DecisionType decisionType = decisionTypeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("DecisionType not found with id: " + id));

        if (updateDecisionTypeDto.getNameEn() != null) {
            decisionType.setNameEn(updateDecisionTypeDto.getNameEn());
        }
        if (updateDecisionTypeDto.getNameRu() != null) {
            if (!decisionType.getNameRu().equals(updateDecisionTypeDto.getNameRu()) &&
                    decisionTypeRepository.existsByNameRu(updateDecisionTypeDto.getNameRu())) {
                throw new IllegalArgumentException("DecisionType with nameRu " + updateDecisionTypeDto.getNameRu() + " already exists");
            }
            decisionType.setNameRu(updateDecisionTypeDto.getNameRu());
        }
        if (updateDecisionTypeDto.getNameKg() != null) {
            decisionType.setNameKg(updateDecisionTypeDto.getNameKg());
        }
        if (updateDecisionTypeDto.getDescription() != null) {
            decisionType.setDescription(updateDecisionTypeDto.getDescription());
        }
        if (updateDecisionTypeDto.getStatus() != null) {
            decisionType.setStatus(updateDecisionTypeDto.getStatus());
        }

        DecisionType updatedDecisionType = decisionTypeRepository.save(decisionType);
        return mapToResponseDto(updatedDecisionType);
    }

    @Override
    @Transactional
    public void deleteDecisionType(Long id) {
        DecisionType decisionType = decisionTypeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("DecisionType not found with id: " + id));

        if (decisionTypeRepository.isReferencedByDecisions(id)) {
            throw new IllegalStateException("DecisionType with ID " + id + " cannot be deleted as it is referenced by decisions");
        }

        decisionTypeRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByNameRu(String nameRu) {
        return decisionTypeRepository.existsByNameRu(nameRu);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DecisionTypeResponse> getAllActiveDecisionTypes(Pageable pageable) {
        return decisionTypeRepository.findByStatusOrderByNameRuAsc(ReferenceEntityStatus.ACTIVE, pageable)
                .map(this::mapToResponseDto);
    }

    private DecisionTypeResponse mapToResponseDto(DecisionType decisionType) {
        return DecisionTypeResponse.builder()
                .id(decisionType.getId())
                .nameEn(decisionType.getNameEn())
                .nameRu(decisionType.getNameRu())
                .nameKg(decisionType.getNameKg())
                .description(decisionType.getDescription())
                .status(decisionType.getStatus())
                .createdAt(decisionType.getCreatedAt())
                .updatedAt(decisionType.getUpdatedAt())
                .build();
    }
}