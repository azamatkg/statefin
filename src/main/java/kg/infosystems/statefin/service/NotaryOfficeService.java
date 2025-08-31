package kg.infosystems.statefin.service;

import kg.infosystems.statefin.dto.request.reference.NotaryOfficeCreateRequest;
import kg.infosystems.statefin.dto.request.reference.NotaryOfficeSearchRequest;
import kg.infosystems.statefin.dto.update.reference.NotaryOfficeUpdateRequest;
import kg.infosystems.statefin.dto.response.reference.NotaryOfficeResponse;
import kg.infosystems.statefin.entity.common.ReferenceEntityStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface NotaryOfficeService {

    NotaryOfficeResponse createNotaryOffice(NotaryOfficeCreateRequest createNotaryOfficeDto);

    NotaryOfficeResponse getNotaryOfficeById(Long id);

    NotaryOfficeResponse getNotaryOfficeByNameRu(String nameRu);

    Page<NotaryOfficeResponse> getAllNotaryOffices(Pageable pageable);

    Page<NotaryOfficeResponse> getAllActiveNotaryOffices(Pageable pageable);

    Page<NotaryOfficeResponse> searchNotaryOffices(String searchTerm, Pageable pageable);

    Page<NotaryOfficeResponse> searchNotaryOffices(String searchTerm, ReferenceEntityStatus status, Pageable pageable);

    NotaryOfficeResponse updateNotaryOffice(Long id, NotaryOfficeUpdateRequest updateNotaryOfficeDto);

    void deleteNotaryOffice(Long id);

    NotaryOfficeResponse activateNotaryOffice(Long id);

    NotaryOfficeResponse deactivateNotaryOffice(Long id);

    boolean existsByNameRu(String nameRu);

    boolean existsByNameEn(String nameEn);

    boolean existsByContactEmail(String contactEmail);

//    boolean isReferencedByCollateralRegistrations(Long id);

    Page<NotaryOfficeResponse> getNotaryOfficesByStatus(ReferenceEntityStatus status, Pageable pageable);
}