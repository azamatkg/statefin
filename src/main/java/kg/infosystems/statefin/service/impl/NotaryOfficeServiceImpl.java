package kg.infosystems.statefin.service.impl;

import kg.infosystems.statefin.dto.request.reference.NotaryOfficeCreateRequest;
import kg.infosystems.statefin.dto.request.reference.NotaryOfficeSearchRequest;
import kg.infosystems.statefin.dto.update.reference.NotaryOfficeUpdateRequest;
import kg.infosystems.statefin.dto.response.reference.NotaryOfficeResponse;
import kg.infosystems.statefin.entity.common.ReferenceEntityStatus;
import kg.infosystems.statefin.entity.reference.NotaryOffice;
import kg.infosystems.statefin.repository.NotaryOfficeRepository;
import kg.infosystems.statefin.service.NotaryOfficeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class NotaryOfficeServiceImpl implements NotaryOfficeService {

    private final NotaryOfficeRepository notaryOfficeRepository;

    @Override
    @Transactional
    public NotaryOfficeResponse createNotaryOffice(NotaryOfficeCreateRequest createDto) {

        if (notaryOfficeRepository.existsByNameRu(createDto.getNameRu())) {
            throw new RuntimeException("Notary office with Russian name '" + createDto.getNameRu() + "' already exists");
        }
        if (notaryOfficeRepository.existsByNameEn(createDto.getNameEn())) {
            throw new RuntimeException("Notary office with English name '" + createDto.getNameEn() + "' already exists");
        }
        if (createDto.getContactEmail() != null && !createDto.getContactEmail().isEmpty() &&
            notaryOfficeRepository.existsByContactEmail(createDto.getContactEmail())) {
            throw new RuntimeException("Notary office with contact email '" + createDto.getContactEmail() + "' already exists");
        }

        NotaryOffice notaryOffice = NotaryOffice.builder()
                .nameEn(createDto.getNameEn())
                .nameRu(createDto.getNameRu())
                .nameKg(createDto.getNameKg())
                .description(createDto.getDescription())
                .address(createDto.getAddress())
                .contactPhone(createDto.getContactPhone())
                .contactEmail(createDto.getContactEmail())
                .registrationNumberFormat(createDto.getRegistrationNumberFormat())
                .status(createDto.getStatus() != null ? createDto.getStatus() : ReferenceEntityStatus.ACTIVE)
                .build();

        NotaryOffice savedNotaryOffice = notaryOfficeRepository.save(notaryOffice);

        return toResponseDto(savedNotaryOffice);
    }

    @Override
    public NotaryOfficeResponse getNotaryOfficeById(Long id) {
        NotaryOffice notaryOffice = findNotaryOfficeById(id);
        return toResponseDto(notaryOffice);
    }

    @Override
    public NotaryOfficeResponse getNotaryOfficeByNameRu(String nameRu) {
        NotaryOffice notaryOffice = notaryOfficeRepository.findByNameRu(nameRu)
                .orElseThrow(() -> new RuntimeException("Notary office not found with Russian name: " + nameRu));
        return toResponseDto(notaryOffice);
    }

    @Override
    public Page<NotaryOfficeResponse> getAllNotaryOffices(Pageable pageable) {
        return notaryOfficeRepository.findAll(pageable)
                .map(this::toResponseDto);
    }

    @Override
    public Page<NotaryOfficeResponse> getAllActiveNotaryOffices(Pageable pageable) {
        return notaryOfficeRepository.findByStatusOrderByNameEnAsc(ReferenceEntityStatus.ACTIVE, pageable)
                .map(this::toResponseDto);
    }

    @Override
    public Page<NotaryOfficeResponse> searchNotaryOffices(String searchTerm, Pageable pageable) {
        return notaryOfficeRepository.searchNotaryOffices(searchTerm, pageable)
                .map(this::toResponseDto);
    }

    @Override
    public Page<NotaryOfficeResponse> searchNotaryOffices(String searchTerm, 
                                                                           ReferenceEntityStatus status, 
                                                                           Pageable pageable) {
        return notaryOfficeRepository.searchNotaryOfficesByStatus(searchTerm, status, pageable)
                .map(this::toResponseDto);
    }

    @Override
    @Transactional
    public NotaryOfficeResponse updateNotaryOffice(Long id, 
                                                                    NotaryOfficeUpdateRequest updateDto) {
        NotaryOffice notaryOffice = findNotaryOfficeById(id);

        if (updateDto.getNameRu() != null && !updateDto.getNameRu().equals(notaryOffice.getNameRu())) {
            if (notaryOfficeRepository.existsByNameRu(updateDto.getNameRu())) {
                throw new RuntimeException("Notary office with Russian name '" + updateDto.getNameRu() + "' already exists");
            }
            notaryOffice.setNameRu(updateDto.getNameRu());
        }

        if (updateDto.getNameEn() != null && !updateDto.getNameEn().equals(notaryOffice.getNameEn())) {
            if (notaryOfficeRepository.existsByNameEn(updateDto.getNameEn())) {
                throw new RuntimeException("Notary office with English name '" + updateDto.getNameEn() + "' already exists");
            }
            notaryOffice.setNameEn(updateDto.getNameEn());
        }

        if (updateDto.getContactEmail() != null && !updateDto.getContactEmail().equals(notaryOffice.getContactEmail())) {
            if (!updateDto.getContactEmail().isEmpty() && notaryOfficeRepository.existsByContactEmail(updateDto.getContactEmail())) {
                throw new RuntimeException("Notary office with contact email '" + updateDto.getContactEmail() + "' already exists");
            }
            notaryOffice.setContactEmail(updateDto.getContactEmail());
        }

        if (updateDto.getNameKg() != null) {
            notaryOffice.setNameKg(updateDto.getNameKg());
        }
        if (updateDto.getDescription() != null) {
            notaryOffice.setDescription(updateDto.getDescription());
        }
        if (updateDto.getAddress() != null) {
            notaryOffice.setAddress(updateDto.getAddress());
        }
        if (updateDto.getContactPhone() != null) {
            notaryOffice.setContactPhone(updateDto.getContactPhone());
        }
        if (updateDto.getRegistrationNumberFormat() != null) {
            notaryOffice.setRegistrationNumberFormat(updateDto.getRegistrationNumberFormat());
        }
        if (updateDto.getStatus() != null) {
            notaryOffice.setStatus(updateDto.getStatus());
        }

        NotaryOffice updatedNotaryOffice = notaryOfficeRepository.save(notaryOffice);

        return toResponseDto(updatedNotaryOffice);
    }

    @Override
    @Transactional
    public void deleteNotaryOffice(Long id) {
        NotaryOffice notaryOffice = findNotaryOfficeById(id);

//        if (isReferencedByCollateralRegistrations(id)) {
//            throw new RuntimeException("Cannot delete notary office as it is referenced by collateral registration records");
//        }

        notaryOfficeRepository.delete(notaryOffice);
    }

    @Override
    @Transactional
    public NotaryOfficeResponse activateNotaryOffice(Long id) {
        NotaryOffice notaryOffice = findNotaryOfficeById(id);
        notaryOffice.setStatus(ReferenceEntityStatus.ACTIVE);
        NotaryOffice savedNotaryOffice = notaryOfficeRepository.save(notaryOffice);
        return toResponseDto(savedNotaryOffice);
    }

    @Override
    @Transactional
    public NotaryOfficeResponse deactivateNotaryOffice(Long id) {
        NotaryOffice notaryOffice = findNotaryOfficeById(id);
        notaryOffice.setStatus(ReferenceEntityStatus.INACTIVE);
        NotaryOffice savedNotaryOffice = notaryOfficeRepository.save(notaryOffice);
        return toResponseDto(savedNotaryOffice);
    }

    @Override
    public boolean existsByNameRu(String nameRu) {
        return notaryOfficeRepository.existsByNameRu(nameRu);
    }

    @Override
    public boolean existsByNameEn(String nameEn) {
        return notaryOfficeRepository.existsByNameEn(nameEn);
    }

    @Override
    public boolean existsByContactEmail(String contactEmail) {
        return notaryOfficeRepository.existsByContactEmail(contactEmail);
    }

//    @Override
//    public boolean isReferencedByCollateralRegistrations(Long id) {
//        return notaryOfficeRepository.isReferencedByCollateralRegistrations(id);
//    }

    @Override
    public Page<NotaryOfficeResponse> getNotaryOfficesByStatus(ReferenceEntityStatus status, 
                                                                                 Pageable pageable) {
        return notaryOfficeRepository.findByStatusOrderByNameEnAsc(status, pageable)
                .map(this::toResponseDto);
    }

    private NotaryOffice findNotaryOfficeById(Long id) {
        return notaryOfficeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notary office not found with ID: " + id));
    }

    private NotaryOfficeResponse toResponseDto(NotaryOffice notaryOffice) {
        return NotaryOfficeResponse.builder()
                .id(notaryOffice.getId())
                .version(notaryOffice.getVersion())
                .nameEn(notaryOffice.getNameEn())
                .nameRu(notaryOffice.getNameRu())
                .nameKg(notaryOffice.getNameKg())
                .description(notaryOffice.getDescription())
                .address(notaryOffice.getAddress())
                .contactPhone(notaryOffice.getContactPhone())
                .contactEmail(notaryOffice.getContactEmail())
                .registrationNumberFormat(notaryOffice.getRegistrationNumberFormat())
                .status(notaryOffice.getStatus())
                .createdAt(notaryOffice.getCreatedAt())
                .updatedAt(notaryOffice.getUpdatedAt())
                .createdByUsername(notaryOffice.getCreatedBy() != null ? notaryOffice.getCreatedBy().getUsername() : null)
                .updatedByUsername(notaryOffice.getUpdatedBy() != null ? notaryOffice.getUpdatedBy().getUsername() : null)
//                .isReferencedByCollateralRegistrations(isReferencedByCollateralRegistrations(notaryOffice.getId()))
                .build();
    }
}