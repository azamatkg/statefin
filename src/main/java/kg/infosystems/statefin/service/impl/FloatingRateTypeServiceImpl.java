package kg.infosystems.statefin.service.impl;

import kg.infosystems.statefin.dto.request.reference.FloatingRateTypeCreateRequest;
import kg.infosystems.statefin.dto.request.reference.FloatingRateTypeSearchRequest;
import kg.infosystems.statefin.dto.update.reference.FloatingRateTypeUpdateRequest;
import kg.infosystems.statefin.dto.response.reference.FloatingRateTypeResponse;
import kg.infosystems.statefin.entity.common.ReferenceEntityStatus;
import kg.infosystems.statefin.entity.reference.FloatingRateType;
import kg.infosystems.statefin.exception.ResourceNotFoundException;
import kg.infosystems.statefin.repository.FloatingRateTypeRepository;
import kg.infosystems.statefin.service.FloatingRateTypeService;
import kg.infosystems.statefin.specification.FloatingRateTypeSpecification;
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
public class FloatingRateTypeServiceImpl implements FloatingRateTypeService {

    private final FloatingRateTypeRepository floatingRateTypeRepository;

    @Override
    @Transactional
    public FloatingRateTypeResponse createFloatingRateType(FloatingRateTypeCreateRequest createFloatingRateTypeDto) {

        if (floatingRateTypeRepository.existsByNameRu(createFloatingRateTypeDto.getNameRu())) {
            throw new IllegalArgumentException("FloatingRateType with nameRu " + createFloatingRateTypeDto.getNameRu() + " already exists");
        }

        FloatingRateType floatingRateType = FloatingRateType.builder()
                .nameEn(createFloatingRateTypeDto.getNameEn())
                .nameRu(createFloatingRateTypeDto.getNameRu())
                .nameKg(createFloatingRateTypeDto.getNameKg())
                .description(createFloatingRateTypeDto.getDescription())
                .status(createFloatingRateTypeDto.getStatus() != null ? createFloatingRateTypeDto.getStatus() : ReferenceEntityStatus.ACTIVE)
                .build();

        FloatingRateType savedFloatingRateType = floatingRateTypeRepository.save(floatingRateType);

        return mapToResponseDto(savedFloatingRateType);
    }

    @Override
    @Transactional(readOnly = true)
    public FloatingRateTypeResponse getFloatingRateTypeById(Long id) {
        FloatingRateType floatingRateType = floatingRateTypeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("FloatingRateType not found with id: " + id));
        return mapToResponseDto(floatingRateType);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<FloatingRateTypeResponse> getAllFloatingRateTypes(Pageable pageable) {
        return floatingRateTypeRepository.findAll(pageable)
                .map(this::mapToResponseDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<FloatingRateTypeResponse> getAllActiveFloatingRateTypes(Pageable pageable) {
        return floatingRateTypeRepository.findByStatusOrderByNameRuAsc(ReferenceEntityStatus.ACTIVE, pageable)
                .map(this::mapToResponseDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<FloatingRateTypeResponse> searchFloatingRateTypes(String searchTerm, Pageable pageable) {

        Specification<FloatingRateType> spec = FloatingRateTypeSpecification.searchByTerm(searchTerm);
        return floatingRateTypeRepository.findAll(spec, pageable)
                .map(this::mapToResponseDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<FloatingRateTypeResponse> searchFloatingRateTypes(String searchTerm, ReferenceEntityStatus status, Pageable pageable) {

        Specification<FloatingRateType> spec = FloatingRateTypeSpecification.searchByTermAndStatus(searchTerm, status);
        return floatingRateTypeRepository.findAll(spec, pageable)
                .map(this::mapToResponseDto);
    }

    @Override
    @Transactional
    public FloatingRateTypeResponse updateFloatingRateType(Long id, FloatingRateTypeUpdateRequest updateFloatingRateTypeDto) {
        FloatingRateType floatingRateType = floatingRateTypeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("FloatingRateType not found with id: " + id));

        if (updateFloatingRateTypeDto.getNameEn() != null) {
            floatingRateType.setNameEn(updateFloatingRateTypeDto.getNameEn());
        }
        if (updateFloatingRateTypeDto.getNameRu() != null) {
            if (!floatingRateType.getNameRu().equals(updateFloatingRateTypeDto.getNameRu()) &&
                    floatingRateTypeRepository.existsByNameRu(updateFloatingRateTypeDto.getNameRu())) {
                throw new IllegalArgumentException("FloatingRateType with nameRu " + updateFloatingRateTypeDto.getNameRu() + " already exists");
            }
            floatingRateType.setNameRu(updateFloatingRateTypeDto.getNameRu());
        }
        if (updateFloatingRateTypeDto.getNameKg() != null) {
            floatingRateType.setNameKg(updateFloatingRateTypeDto.getNameKg());
        }
        if (updateFloatingRateTypeDto.getDescription() != null) {
            floatingRateType.setDescription(updateFloatingRateTypeDto.getDescription());
        }
        if (updateFloatingRateTypeDto.getStatus() != null) {
            floatingRateType.setStatus(updateFloatingRateTypeDto.getStatus());
        }

        FloatingRateType updatedFloatingRateType = floatingRateTypeRepository.save(floatingRateType);
        return mapToResponseDto(updatedFloatingRateType);
    }

    @Override
    @Transactional
    public void deleteFloatingRateType(Long id) {
        FloatingRateType floatingRateType = floatingRateTypeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("FloatingRateType not found with id: " + id));

//        if (floatingRateTypeRepository.isReferencedByCreditPrograms(id)) {
//            throw new IllegalStateException("FloatingRateType with ID " + id + " cannot be deleted as it is referenced by credit programs");
//        }

        floatingRateTypeRepository.deleteById(id);
    }

    @Override
    @Transactional
    public FloatingRateTypeResponse activateFloatingRateType(Long id) {
        FloatingRateType floatingRateType = floatingRateTypeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("FloatingRateType not found with id: " + id));

        floatingRateType.setStatus(ReferenceEntityStatus.ACTIVE);
        FloatingRateType updatedFloatingRateType = floatingRateTypeRepository.save(floatingRateType);
        return mapToResponseDto(updatedFloatingRateType);
    }

    @Override
    @Transactional
    public FloatingRateTypeResponse deactivateFloatingRateType(Long id) {
        FloatingRateType floatingRateType = floatingRateTypeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("FloatingRateType not found with id: " + id));

        floatingRateType.setStatus(ReferenceEntityStatus.INACTIVE);
        FloatingRateType updatedFloatingRateType = floatingRateTypeRepository.save(floatingRateType);
        return mapToResponseDto(updatedFloatingRateType);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByNameRu(String nameRu) {
        return floatingRateTypeRepository.existsByNameRu(nameRu);
    }

//    @Override
//    @Transactional(readOnly = true)
//    public boolean isReferencedByCreditPrograms(Long id) {
//        return floatingRateTypeRepository.isReferencedByCreditPrograms(id);
//    }

    @Override
    @Transactional(readOnly = true)
    public Page<FloatingRateTypeResponse> getFloatingRateTypesByStatus(ReferenceEntityStatus status, Pageable pageable) {
        return floatingRateTypeRepository.findByStatusOrderByNameRuAsc(status, pageable)
                .map(this::mapToResponseDto);
    }

    private FloatingRateTypeResponse mapToResponseDto(FloatingRateType floatingRateType) {
        boolean isReferenced = floatingRateTypeRepository.isReferencedByCreditPrograms(floatingRateType.getId());

        return FloatingRateTypeResponse.builder()
                .id(floatingRateType.getId())
                .version(floatingRateType.getVersion())
                .nameEn(floatingRateType.getNameEn())
                .nameRu(floatingRateType.getNameRu())
                .nameKg(floatingRateType.getNameKg())
                .description(floatingRateType.getDescription())
                .status(floatingRateType.getStatus())
                .createdAt(floatingRateType.getCreatedAt())
                .updatedAt(floatingRateType.getUpdatedAt())
                .createdByUsername(floatingRateType.getCreatedBy() != null ? floatingRateType.getCreatedBy().getUsername() : null)
                .updatedByUsername(floatingRateType.getUpdatedBy() != null ? floatingRateType.getUpdatedBy().getUsername() : null)
                .isReferencedByCreditPrograms(isReferenced)
                .build();
    }
}