package kg.infosystems.statefin.service.impl;

import kg.infosystems.statefin.dto.request.reference.CreditPurposeCreateRequest;
import kg.infosystems.statefin.dto.request.reference.CreditPurposeSearchRequest;
import kg.infosystems.statefin.dto.update.reference.CreditPurposeUpdateRequest;
import kg.infosystems.statefin.dto.response.reference.CreditPurposeResponse;
import kg.infosystems.statefin.entity.common.ReferenceEntityStatus;
import kg.infosystems.statefin.entity.reference.CreditPurpose;
import kg.infosystems.statefin.exception.ResourceNotFoundException;
import kg.infosystems.statefin.repository.CreditPurposeRepository;
import kg.infosystems.statefin.service.CreditPurposeService;
import kg.infosystems.statefin.specification.CreditPurposeSpecification;
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
public class CreditPurposeServiceImpl implements CreditPurposeService {

    private final CreditPurposeRepository creditPurposeRepository;

    @Override
    @Transactional
    public CreditPurposeResponse createCreditPurpose(CreditPurposeCreateRequest createCreditPurposeDto) {

        if (creditPurposeRepository.existsByNameRu(createCreditPurposeDto.getNameRu())) {
            throw new IllegalArgumentException("CreditPurpose with nameRu " + createCreditPurposeDto.getNameRu() + " already exists");
        }

        CreditPurpose creditPurpose = CreditPurpose.builder()
                .nameEn(createCreditPurposeDto.getNameEn())
                .nameRu(createCreditPurposeDto.getNameRu())
                .nameKg(createCreditPurposeDto.getNameKg())
                .description(createCreditPurposeDto.getDescription())
                .status(createCreditPurposeDto.getStatus() != null ? createCreditPurposeDto.getStatus() : ReferenceEntityStatus.ACTIVE)
                .build();

        CreditPurpose savedCreditPurpose = creditPurposeRepository.save(creditPurpose);

        return mapToResponseDto(savedCreditPurpose);
    }

    @Override
    @Transactional(readOnly = true)
    public CreditPurposeResponse getCreditPurposeById(Long id) {
        CreditPurpose creditPurpose = creditPurposeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("CreditPurpose not found with id: " + id));
        return mapToResponseDto(creditPurpose);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CreditPurposeResponse> getAllCreditPurposes(Pageable pageable) {
        return creditPurposeRepository.findAll(pageable)
                .map(this::mapToResponseDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CreditPurposeResponse> getAllActiveCreditPurposes(Pageable pageable) {
        return creditPurposeRepository.findByStatusOrderByNameRuAsc(ReferenceEntityStatus.ACTIVE, pageable)
                .map(this::mapToResponseDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CreditPurposeResponse> searchCreditPurposes(String searchTerm, Pageable pageable) {

        Specification<CreditPurpose> spec = CreditPurposeSpecification.searchByTerm(searchTerm);
        return creditPurposeRepository.findAll(spec, pageable)
                .map(this::mapToResponseDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CreditPurposeResponse> searchCreditPurposes(String searchTerm, ReferenceEntityStatus status, Pageable pageable) {

        Specification<CreditPurpose> spec = CreditPurposeSpecification.searchByTermAndStatus(searchTerm, status);
        return creditPurposeRepository.findAll(spec, pageable)
                .map(this::mapToResponseDto);
    }

    @Override
    @Transactional
    public CreditPurposeResponse updateCreditPurpose(Long id, CreditPurposeUpdateRequest updateCreditPurposeDto) {
        CreditPurpose creditPurpose = creditPurposeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("CreditPurpose not found with id: " + id));

        if (updateCreditPurposeDto.getNameEn() != null) {
            creditPurpose.setNameEn(updateCreditPurposeDto.getNameEn());
        }
        if (updateCreditPurposeDto.getNameRu() != null) {
            if (!creditPurpose.getNameRu().equals(updateCreditPurposeDto.getNameRu()) &&
                    creditPurposeRepository.existsByNameRu(updateCreditPurposeDto.getNameRu())) {
                throw new IllegalArgumentException("CreditPurpose with nameRu " + updateCreditPurposeDto.getNameRu() + " already exists");
            }
            creditPurpose.setNameRu(updateCreditPurposeDto.getNameRu());
        }
        if (updateCreditPurposeDto.getNameKg() != null) {
            creditPurpose.setNameKg(updateCreditPurposeDto.getNameKg());
        }
        if (updateCreditPurposeDto.getDescription() != null) {
            creditPurpose.setDescription(updateCreditPurposeDto.getDescription());
        }
        if (updateCreditPurposeDto.getStatus() != null) {
            creditPurpose.setStatus(updateCreditPurposeDto.getStatus());
        }

        CreditPurpose updatedCreditPurpose = creditPurposeRepository.save(creditPurpose);
        return mapToResponseDto(updatedCreditPurpose);
    }

    @Override
    @Transactional
    public void deleteCreditPurpose(Long id) {
        CreditPurpose creditPurpose = creditPurposeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("CreditPurpose not found with id: " + id));

        //TODO uncomment later
//        if (creditPurposeRepository.isReferencedByCreditPrograms(id)) {
//            throw new IllegalStateException("CreditPurpose with ID " + id + " cannot be deleted as it is referenced by credit programs");
//        }

        creditPurposeRepository.deleteById(id);
    }

    @Override
    @Transactional
    public CreditPurposeResponse activateCreditPurpose(Long id) {
        CreditPurpose creditPurpose = creditPurposeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("CreditPurpose not found with id: " + id));

        creditPurpose.setStatus(ReferenceEntityStatus.ACTIVE);
        CreditPurpose updatedCreditPurpose = creditPurposeRepository.save(creditPurpose);
        return mapToResponseDto(updatedCreditPurpose);
    }

    @Override
    @Transactional
    public CreditPurposeResponse deactivateCreditPurpose(Long id) {
        CreditPurpose creditPurpose = creditPurposeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("CreditPurpose not found with id: " + id));

        creditPurpose.setStatus(ReferenceEntityStatus.INACTIVE);
        CreditPurpose updatedCreditPurpose = creditPurposeRepository.save(creditPurpose);
        return mapToResponseDto(updatedCreditPurpose);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByNameRu(String nameRu) {
        return creditPurposeRepository.existsByNameRu(nameRu);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isReferencedByCreditPrograms(Long id) {
        return creditPurposeRepository.isReferencedByCreditPrograms(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CreditPurposeResponse> getCreditPurposesByStatus(ReferenceEntityStatus status, Pageable pageable) {
        return creditPurposeRepository.findByStatusOrderByNameRuAsc(status, pageable)
                .map(this::mapToResponseDto);
    }

    private CreditPurposeResponse mapToResponseDto(CreditPurpose creditPurpose) {
        boolean isReferenced = creditPurposeRepository.isReferencedByCreditPrograms(creditPurpose.getId());

        return CreditPurposeResponse.builder()
                .id(creditPurpose.getId())
                .version(creditPurpose.getVersion())
                .nameEn(creditPurpose.getNameEn())
                .nameRu(creditPurpose.getNameRu())
                .nameKg(creditPurpose.getNameKg())
                .description(creditPurpose.getDescription())
                .status(creditPurpose.getStatus())
                .createdAt(creditPurpose.getCreatedAt())
                .updatedAt(creditPurpose.getUpdatedAt())
                .createdByUsername(creditPurpose.getCreatedBy() != null ? creditPurpose.getCreatedBy().getUsername() : null)
                .updatedByUsername(creditPurpose.getUpdatedBy() != null ? creditPurpose.getUpdatedBy().getUsername() : null)
                .isReferencedByCreditPrograms(isReferenced)
                .build();
    }
}