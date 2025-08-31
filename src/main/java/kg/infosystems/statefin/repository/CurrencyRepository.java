package kg.infosystems.statefin.repository;

import kg.infosystems.statefin.entity.common.ReferenceEntityStatus;
import kg.infosystems.statefin.entity.reference.Currency;
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
public interface CurrencyRepository extends JpaRepository<Currency, Long>, JpaSpecificationExecutor<Currency> {

    Optional<Currency> findByCode(String code);

    boolean existsByCode(String code);

    Optional<Currency> findByNameRu(String nameRu);

    boolean existsByNameRu(String nameRu);

    Page<Currency> findByStatusOrderByCodeAsc(ReferenceEntityStatus status, Pageable pageable);

    List<Currency> findByStatusOrderByCodeAsc(ReferenceEntityStatus status);

    @Query("SELECT c FROM Currency c WHERE " +
           "LOWER(c.code) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
           "OR LOWER(c.nameEn) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
           "OR LOWER(c.nameRu) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
           "OR LOWER(c.nameKg) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
           "OR LOWER(c.description) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
           "ORDER BY c.code ASC")
    Page<Currency> searchCurrencies(@Param("searchTerm") String searchTerm, Pageable pageable);

    //TODO uncomment later
//    @Query("SELECT CASE WHEN COUNT(cp) > 0 THEN true ELSE false END FROM CreditProgram cp WHERE cp.currency.id = :currencyId")
//    boolean isReferencedByCreditPrograms(@Param("currencyId") Long currencyId);

    @Query("SELECT 1")
    boolean isReferencedByCreditPrograms(@Param("currencyId") Long currencyId);

    List<Currency> findByStatusOrderByCode(ReferenceEntityStatus status);
}