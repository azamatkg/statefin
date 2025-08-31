package kg.infosystems.statefin.repository;

import kg.infosystems.statefin.entity.common.ReferenceEntityStatus;
import kg.infosystems.statefin.entity.reference.RepaymentOrder;
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
public interface RepaymentOrderRepository extends JpaRepository<RepaymentOrder, Long>, JpaSpecificationExecutor<RepaymentOrder> {

    Optional<RepaymentOrder> findByNameRu(String nameRu);

    boolean existsByNameRu(String nameRu);

    Page<RepaymentOrder> findByStatusOrderByNameRuAsc(ReferenceEntityStatus status, Pageable pageable);

    List<RepaymentOrder> findByStatusOrderByNameRuAsc(ReferenceEntityStatus status);

    @Query("SELECT ro FROM RepaymentOrder ro WHERE " +
           "LOWER(ro.nameEn) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
           "OR LOWER(ro.nameRu) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
           "OR LOWER(ro.nameKg) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
           "OR LOWER(ro.description) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
           "ORDER BY ro.nameRu ASC")
    Page<RepaymentOrder> searchRepaymentOrders(@Param("searchTerm") String searchTerm, Pageable pageable);

//    TODO uncomment later
//    @Query("SELECT CASE WHEN COUNT(cp) > 0 THEN true ELSE false END FROM CreditProgram cp WHERE cp.repaymentOrder.id = :repaymentOrderId")
//    boolean isReferencedByCreditPrograms(@Param("repaymentOrderId") Long repaymentOrderId);
}