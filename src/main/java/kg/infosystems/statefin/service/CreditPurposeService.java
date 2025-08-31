package kg.infosystems.statefin.service;

import kg.infosystems.statefin.dto.request.reference.CreditPurposeCreateRequest;
import kg.infosystems.statefin.dto.request.reference.CreditPurposeSearchRequest;
import kg.infosystems.statefin.dto.update.reference.CreditPurposeUpdateRequest;
import kg.infosystems.statefin.dto.response.reference.CreditPurposeResponse;
import kg.infosystems.statefin.entity.common.ReferenceEntityStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service interface for CreditPurpose entity operations.
 *
 * @author azamat
 * @version 1.0
 */
public interface CreditPurposeService {

    /**
     * Create a new credit purpose.
     *
     * @param createCreditPurposeDto the credit purpose creation data
     * @return the created credit purpose response
     */
    CreditPurposeResponse createCreditPurpose(CreditPurposeCreateRequest createCreditPurposeDto);

    /**
     * Get credit purpose by ID.
     *
     * @param id the credit purpose ID
     * @return the credit purpose response
     */
    CreditPurposeResponse getCreditPurposeById(Long id);

    /**
     * Get all credit purposes with pagination.
     *
     * @param pageable pagination parameters
     * @return page of credit purpose responses
     */
    Page<CreditPurposeResponse> getAllCreditPurposes(Pageable pageable);

    /**
     * Get all active credit purposes with pagination.
     *
     * @param pageable pagination parameters
     * @return page of active credit purpose responses
     */
    Page<CreditPurposeResponse> getAllActiveCreditPurposes(Pageable pageable);

    /**
     * Search credit purposes by term with pagination.
     *
     * @param searchTerm the search term
     * @param pageable pagination parameters
     * @return page of credit purpose responses
     */
    Page<CreditPurposeResponse> searchCreditPurposes(String searchTerm, Pageable pageable);

    /**
     * Search credit purposes by term and status with pagination.
     *
     * @param searchTerm the search term
     * @param status the credit purpose status
     * @param pageable pagination parameters
     * @return page of credit purpose responses
     */
    Page<CreditPurposeResponse> searchCreditPurposes(String searchTerm, ReferenceEntityStatus status, Pageable pageable);

    /**
     * Update credit purpose by ID.
     *
     * @param id the credit purpose ID
     * @param updateCreditPurposeDto the credit purpose update data
     * @return the updated credit purpose response
     */
    CreditPurposeResponse updateCreditPurpose(Long id, CreditPurposeUpdateRequest updateCreditPurposeDto);

    /**
     * Delete credit purpose by ID.
     *
     * @param id the credit purpose ID
     */
    void deleteCreditPurpose(Long id);

    /**
     * Activate credit purpose by ID.
     *
     * @param id the credit purpose ID
     * @return the activated credit purpose response
     */
    CreditPurposeResponse activateCreditPurpose(Long id);

    /**
     * Deactivate credit purpose by ID.
     *
     * @param id the credit purpose ID
     * @return the deactivated credit purpose response
     */
    CreditPurposeResponse deactivateCreditPurpose(Long id);

    /**
     * Check if credit purpose exists by name (Russian).
     *
     * @param nameRu the credit purpose name in Russian to check
     * @return true if exists, false otherwise
     */
    boolean existsByNameRu(String nameRu);

    /**
     * Check if credit purpose is referenced by credit programs.
     *
     * @param id the credit purpose ID
     * @return true if referenced, false otherwise
     */
    boolean isReferencedByCreditPrograms(Long id);

    /**
     * Get credit purposes by status.
     *
     * @param status the credit purpose status
     * @param pageable pagination parameters
     * @return page of credit purpose responses
     */
    Page<CreditPurposeResponse> getCreditPurposesByStatus(ReferenceEntityStatus status, Pageable pageable);
}