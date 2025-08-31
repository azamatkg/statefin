package kg.infosystems.statefin.repository;

import kg.infosystems.statefin.entity.credit.decision.Decision;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface DecisionRepository extends JpaRepository<Decision, UUID>, JpaSpecificationExecutor<Decision> {
    
    Optional<Decision> findByNumber(String number);
    
    boolean existsByNumber(String number);
}