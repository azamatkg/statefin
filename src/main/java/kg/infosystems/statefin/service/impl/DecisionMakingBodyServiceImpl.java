package kg.infosystems.statefin.service.impl;

import kg.infosystems.statefin.dto.request.reference.DecisionMakingBodyCreateRequest;
import kg.infosystems.statefin.dto.update.reference.DecisionMakingBodyUpdateRequest;
import kg.infosystems.statefin.dto.response.reference.DecisionMakingBodyResponse;
import kg.infosystems.statefin.entity.common.ReferenceEntityStatus;
import kg.infosystems.statefin.entity.reference.DecisionMakingBody;
import kg.infosystems.statefin.exception.ResourceNotFoundException;
import kg.infosystems.statefin.repository.DecisionMakingBodyRepository;
import kg.infosystems.statefin.service.DecisionMakingBodyService;
import kg.infosystems.statefin.specification.DecisionMakingBodySpecification;
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
public class DecisionMakingBodyServiceImpl implements DecisionMakingBodyService {

    private final DecisionMakingBodyRepository decisionMakingBodyRepository;

    @Override
    @Transactional
    public DecisionMakingBodyResponse createDecisionMakingBody(DecisionMakingBodyCreateRequest createDecisionMakingBodyDto) {

        if (decisionMakingBodyRepository.existsByNameRu(createDecisionMakingBodyDto.getNameRu())) {
            throw new IllegalArgumentException("DecisionMakingBody with nameRu " + createDecisionMakingBodyDto.getNameRu() + " already exists");
        }

        DecisionMakingBody decisionMakingBody = DecisionMakingBody.builder()
                .nameEn(createDecisionMakingBodyDto.getNameEn())
                .nameRu(createDecisionMakingBodyDto.getNameRu())
                .nameKg(createDecisionMakingBodyDto.getNameKg())
                .description(createDecisionMakingBodyDto.getDescription())
                .status(createDecisionMakingBodyDto.getStatus())
                .build();

        DecisionMakingBody savedDecisionMakingBody = decisionMakingBodyRepository.save(decisionMakingBody);

        return mapToResponseDto(savedDecisionMakingBody);
    }

    @Override
    @Transactional(readOnly = true)
    public DecisionMakingBodyResponse getDecisionMakingBodyById(Long id) {
        DecisionMakingBody decisionMakingBody = decisionMakingBodyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("DecisionMakingBody not found with id: " + id));
        return mapToResponseDto(decisionMakingBody);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DecisionMakingBodyResponse> getAllDecisionMakingBodies(Pageable pageable) {
        return decisionMakingBodyRepository.findAll(pageable)
                .map(this::mapToResponseDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DecisionMakingBodyResponse> searchDecisionMakingBodies(String searchTerm, Pageable pageable) {

        Specification<DecisionMakingBody> spec = DecisionMakingBodySpecification.searchByTerm(searchTerm);
        return decisionMakingBodyRepository.findAll(spec, pageable)
                .map(this::mapToResponseDto);
    }

    @Override
    @Transactional
    public DecisionMakingBodyResponse updateDecisionMakingBody(Long id, DecisionMakingBodyUpdateRequest updateDecisionMakingBodyDto) {
        DecisionMakingBody decisionMakingBody = decisionMakingBodyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("DecisionMakingBody not found with id: " + id));

        if (updateDecisionMakingBodyDto.getNameEn() != null) {
            decisionMakingBody.setNameEn(updateDecisionMakingBodyDto.getNameEn());
        }
        if (updateDecisionMakingBodyDto.getNameRu() != null) {
            if (!decisionMakingBody.getNameRu().equals(updateDecisionMakingBodyDto.getNameRu()) &&
                    decisionMakingBodyRepository.existsByNameRu(updateDecisionMakingBodyDto.getNameRu())) {
                throw new IllegalArgumentException("DecisionMakingBody with nameRu " + updateDecisionMakingBodyDto.getNameRu() + " already exists");
            }
            decisionMakingBody.setNameRu(updateDecisionMakingBodyDto.getNameRu());
        }
        if (updateDecisionMakingBodyDto.getNameKg() != null) {
            decisionMakingBody.setNameKg(updateDecisionMakingBodyDto.getNameKg());
        }
        if (updateDecisionMakingBodyDto.getDescription() != null) {
            decisionMakingBody.setDescription(updateDecisionMakingBodyDto.getDescription());
        }
        if (updateDecisionMakingBodyDto.getStatus() != null) {
            decisionMakingBody.setStatus(updateDecisionMakingBodyDto.getStatus());
        }

        DecisionMakingBody updatedDecisionMakingBody = decisionMakingBodyRepository.save(decisionMakingBody);
        return mapToResponseDto(updatedDecisionMakingBody);
    }

    @Override
    @Transactional
    public void deleteDecisionMakingBody(Long id) {
        DecisionMakingBody decisionMakingBody = decisionMakingBodyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("DecisionMakingBody not found with id: " + id));

        if (decisionMakingBodyRepository.isReferencedByDecisions(id)) {
            throw new IllegalStateException("DecisionMakingBody with ID " + id + " cannot be deleted as it is referenced by decisions");
        }

        decisionMakingBodyRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByNameRu(String nameRu) {
        return decisionMakingBodyRepository.existsByNameRu(nameRu);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DecisionMakingBodyResponse> getAllActiveDecisionMakingBodies(Pageable pageable) {
        return decisionMakingBodyRepository.findByStatusOrderByNameRuAsc(ReferenceEntityStatus.ACTIVE, pageable)
                .map(this::mapToResponseDto);
    }

    private DecisionMakingBodyResponse mapToResponseDto(DecisionMakingBody decisionMakingBody) {
        return DecisionMakingBodyResponse.builder()
                .id(decisionMakingBody.getId())
                .nameEn(decisionMakingBody.getNameEn())
                .nameRu(decisionMakingBody.getNameRu())
                .nameKg(decisionMakingBody.getNameKg())
                .description(decisionMakingBody.getDescription())
                .status(decisionMakingBody.getStatus())
                .createdAt(decisionMakingBody.getCreatedAt())
                .updatedAt(decisionMakingBody.getUpdatedAt())
                .build();
    }
}