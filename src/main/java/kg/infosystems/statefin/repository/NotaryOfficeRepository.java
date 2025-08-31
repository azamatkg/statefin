package kg.infosystems.statefin.repository;

import kg.infosystems.statefin.entity.common.ReferenceEntityStatus;
import kg.infosystems.statefin.entity.reference.NotaryOffice;
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
public interface NotaryOfficeRepository extends JpaRepository<NotaryOffice, Long>, JpaSpecificationExecutor<NotaryOffice> {

    Optional<NotaryOffice> findByNameRu(String nameRu);

    boolean existsByNameRu(String nameRu);

    Optional<NotaryOffice> findByNameEn(String nameEn);

    boolean existsByNameEn(String nameEn);

    Page<NotaryOffice> findByStatusOrderByNameEnAsc(ReferenceEntityStatus status, Pageable pageable);

    List<NotaryOffice> findByStatusOrderByNameEnAsc(ReferenceEntityStatus status);

    @Query("SELECT n FROM NotaryOffice n WHERE " +
           "LOWER(n.nameEn) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
           "OR LOWER(n.nameRu) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
           "OR LOWER(n.nameKg) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
           "OR LOWER(n.description) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
           "OR LOWER(n.address) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
           "OR LOWER(n.contactEmail) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
           "OR LOWER(n.registrationNumberFormat) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
           "ORDER BY n.nameEn ASC")
    Page<NotaryOffice> searchNotaryOffices(@Param("searchTerm") String searchTerm, Pageable pageable);

    @Query("SELECT n FROM NotaryOffice n WHERE " +
           "n.status = :status AND (" +
           "LOWER(n.nameEn) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
           "OR LOWER(n.nameRu) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
           "OR LOWER(n.nameKg) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
           "OR LOWER(n.description) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
           "OR LOWER(n.address) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
           "OR LOWER(n.contactEmail) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
           "OR LOWER(n.registrationNumberFormat) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) " +
           "ORDER BY n.nameEn ASC")
    Page<NotaryOffice> searchNotaryOfficesByStatus(@Param("searchTerm") String searchTerm, 
                                                  @Param("status") ReferenceEntityStatus status, 
                                                  Pageable pageable);

    //TODO uncomment later
//    @Query("SELECT CASE WHEN COUNT(crr) > 0 THEN true ELSE false END FROM CollateralRegistrationRecord crr WHERE crr.notaryOffice.id = :notaryOfficeId")
//    boolean isReferencedByCollateralRegistrations(@Param("notaryOfficeId") Long notaryOfficeId);

    List<NotaryOffice> findByStatusOrderByNameEn(ReferenceEntityStatus status);

    Optional<NotaryOffice> findByContactEmail(String contactEmail);

    boolean existsByContactEmail(String contactEmail);
}