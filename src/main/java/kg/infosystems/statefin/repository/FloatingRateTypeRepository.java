package kg.infosystems.statefin.repository;

import kg.infosystems.statefin.entity.common.ReferenceEntityStatus;
import kg.infosystems.statefin.entity.reference.FloatingRateType;
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
public interface FloatingRateTypeRepository extends JpaRepository<FloatingRateType, Long>, JpaSpecificationExecutor<FloatingRateType> {

    Optional<FloatingRateType> findByNameRu(String nameRu);

    boolean existsByNameRu(String nameRu);

    Page<FloatingRateType> findByStatusOrderByNameRuAsc(ReferenceEntityStatus status, Pageable pageable);

    List<FloatingRateType> findByStatusOrderByNameRuAsc(ReferenceEntityStatus status);

    @Query("SELECT frt FROM FloatingRateType frt WHERE " +
           "LOWER(frt.nameEn) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
           "OR LOWER(frt.nameRu) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
           "OR LOWER(frt.nameKg) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
           "OR LOWER(frt.description) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
           "ORDER BY frt.nameRu ASC")
    Page<FloatingRateType> searchFloatingRateTypes(@Param("searchTerm") String searchTerm, Pageable pageable);

    //TODO uncomment later
//    @Query("SELECT CASE WHEN COUNT(cp) > 0 THEN true ELSE false END FROM CreditProgram cp WHERE " +
//           "cp.interestRateType.id = :floatingRateTypeId " +
//           "OR cp.penaltyRatePrincipalType.id = :floatingRateTypeId " +
//           "OR cp.penaltyRateInterestType.id = :floatingRateTypeId")
//    boolean isReferencedByCreditPrograms(@Param("floatingRateTypeId") Long floatingRateTypeId);

    @Query("SELECT 1")
    boolean isReferencedByCreditPrograms(@Param("floatingRateTypeId") Long floatingRateTypeId);
}