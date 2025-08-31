package kg.infosystems.statefin.specification;

import jakarta.persistence.criteria.Predicate;
import kg.infosystems.statefin.entity.common.ReferenceEntityStatus;
import kg.infosystems.statefin.entity.reference.RepaymentOrder;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class RepaymentOrderSpecification {

    public static Specification<RepaymentOrder> searchByTerm(String searchTerm) {
        return (root, query, criteriaBuilder) -> {
            if (!StringUtils.hasText(searchTerm)) {
                return criteriaBuilder.conjunction();
            }

            String likePattern = "%" + searchTerm.toLowerCase() + "%";
            List<Predicate> predicates = new ArrayList<>();

            // Search in repayment order names (EN, RU, KG)
            predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("nameEn")), likePattern));
            predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("nameRu")), likePattern));
            predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("nameKg")), likePattern));

            // Search in description
            predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("description")), likePattern));

            // Search in code
            predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("code")), likePattern));

            return criteriaBuilder.or(predicates.toArray(new Predicate[0]));
        };
    }

    public static Specification<RepaymentOrder> hasStatus(ReferenceEntityStatus status) {
        return (root, query, criteriaBuilder) -> {
            if (status == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("status"), status);
        };
    }

    public static Specification<RepaymentOrder> isActive() {
        return (root, query, criteriaBuilder) -> 
            criteriaBuilder.equal(root.get("status"), ReferenceEntityStatus.ACTIVE);
    }

    public static Specification<RepaymentOrder> hasOrderPriority(Integer orderPriority) {
        return (root, query, criteriaBuilder) -> {
            if (orderPriority == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("orderPriority"), orderPriority);
        };
    }

    public static Specification<RepaymentOrder> searchByTermAndStatus(String searchTerm, ReferenceEntityStatus status) {
        return Specification.where(searchByTerm(searchTerm))
                           .and(hasStatus(status));
    }
}