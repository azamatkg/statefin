package kg.infosystems.statefin.repository;

import kg.infosystems.statefin.entity.common.ReferenceEntityStatus;
import kg.infosystems.statefin.entity.reference.CreditPurpose;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CreditPurposeRepository extends JpaRepository<CreditPurpose, Long>, JpaSpecificationExecutor<CreditPurpose> {

    Optional<CreditPurpose> findByNameRu(String nameRu);

    boolean existsByNameRu(String nameRu);

    Page<CreditPurpose> findByStatusOrderByNameRuAsc(ReferenceEntityStatus status, Pageable pageable);

    List<CreditPurpose> findByStatusOrderByNameRuAsc(ReferenceEntityStatus status);

    @Query("SELECT cp FROM CreditPurpose cp WHERE " +
           "LOWER(cp.nameEn) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
           "OR LOWER(cp.nameRu) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
           "OR LOWER(cp.nameKg) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
           "OR LOWER(cp.description) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
           "ORDER BY cp.nameRu ASC")
    Page<CreditPurpose> searchCreditPurposes(@Param("searchTerm") String searchTerm, Pageable pageable);

    //TODO uncomment after CreditProgram added
//    @Query("SELECT CASE WHEN COUNT(cp) > 0 THEN true ELSE false END FROM CreditProgram cp WHERE cp.creditPurpose.id = :creditPurposeId")
//    boolean isReferencedByCreditPrograms(@Param("creditPurposeId") Long creditPurposeId);
    @Query("select 1")
    boolean isReferencedByCreditPrograms(@Param("creditPurposeId") Long creditPurposeId);
}