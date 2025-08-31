package kg.infosystems.statefin.repository;

import kg.infosystems.statefin.entity.common.ReferenceEntityStatus;
import kg.infosystems.statefin.entity.reference.DecisionMakingBody;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DecisionMakingBodyRepository extends JpaRepository<DecisionMakingBody, Long>, JpaSpecificationExecutor<DecisionMakingBody> {
    
    Optional<DecisionMakingBody> findByNameRu(String nameRu);
    
    boolean existsByNameRu(String nameRu);

    Page<DecisionMakingBody> findByStatusOrderByNameRuAsc(ReferenceEntityStatus status, Pageable pageable);

    @Query("SELECT CASE WHEN COUNT(d) > 0 THEN true ELSE false END FROM Decision d WHERE d.decisionMakingBody.id = :decisionMakingBodyId")
    boolean isReferencedByDecisions(@Param("decisionMakingBodyId") Long decisionMakingBodyId);
}