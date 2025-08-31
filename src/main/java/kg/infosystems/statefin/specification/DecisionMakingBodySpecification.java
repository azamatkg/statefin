package kg.infosystems.statefin.specification;

import jakarta.persistence.criteria.Predicate;
import kg.infosystems.statefin.entity.reference.DecisionMakingBody;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class DecisionMakingBodySpecification {

    public static Specification<DecisionMakingBody> searchByTerm(String searchTerm) {
        return (root, query, criteriaBuilder) -> {
            if (!StringUtils.hasText(searchTerm)) {
                return criteriaBuilder.conjunction();
            }

            String likePattern = "%" + searchTerm.toLowerCase() + "%";
            List<Predicate> predicates = new ArrayList<>();

            // Search in decision making body names (EN, RU, KG)
            predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("nameEn")), likePattern));
            predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("nameRu")), likePattern));
            predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("nameKg")), likePattern));

            // Search in description
            predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("description")), likePattern));

            return criteriaBuilder.or(predicates.toArray(new Predicate[0]));
        };
    }

    public static Specification<DecisionMakingBody> hasStatus(String status) {
        return (root, query, criteriaBuilder) -> {
            if (!StringUtils.hasText(status)) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("status"), status);
        };
    }

    public static Specification<DecisionMakingBody> isActive() {
        return (root, query, criteriaBuilder) -> 
            criteriaBuilder.equal(root.get("status"), "ACTIVE");
    }
}