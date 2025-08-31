package kg.infosystems.statefin.specification;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import kg.infosystems.statefin.dto.request.credit.DecisionSearchRequest;
import kg.infosystems.statefin.entity.credit.decision.Decision;
import kg.infosystems.statefin.entity.credit.decision.DecisionStatus;
import kg.infosystems.statefin.entity.reference.DecisionMakingBody;
import kg.infosystems.statefin.entity.reference.DecisionType;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class DecisionSpecification {

    public static Specification<Decision> searchByTerm(String searchTerm) {
        return (root, query, criteriaBuilder) -> {
            if (!StringUtils.hasText(searchTerm)) {
                return criteriaBuilder.conjunction();
            }

            String likePattern = "%" + searchTerm.toLowerCase() + "%";
            List<Predicate> predicates = new ArrayList<>();

            predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("nameEn")), likePattern));
            predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("nameRu")), likePattern));
            predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("nameKg")), likePattern));

            predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("number")), likePattern));

            predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("note")), likePattern));

            Join<Decision, DecisionType> decisionTypeJoin = root.join("decisionType", JoinType.LEFT);
            predicates.add(criteriaBuilder.like(criteriaBuilder.lower(decisionTypeJoin.get("nameEn")), likePattern));
            predicates.add(criteriaBuilder.like(criteriaBuilder.lower(decisionTypeJoin.get("nameRu")), likePattern));
            predicates.add(criteriaBuilder.like(criteriaBuilder.lower(decisionTypeJoin.get("nameKg")), likePattern));

            Join<Decision, DecisionMakingBody> decisionMakingBodyJoin = root.join("decisionMakingBody", JoinType.LEFT);
            predicates.add(criteriaBuilder.like(criteriaBuilder.lower(decisionMakingBodyJoin.get("nameEn")), likePattern));
            predicates.add(criteriaBuilder.like(criteriaBuilder.lower(decisionMakingBodyJoin.get("nameRu")), likePattern));
            predicates.add(criteriaBuilder.like(criteriaBuilder.lower(decisionMakingBodyJoin.get("nameKg")), likePattern));

            return criteriaBuilder.or(predicates.toArray(new Predicate[0]));
        };
    }

    public static Specification<Decision> hasStatus(String status) {
        return (root, query, criteriaBuilder) -> {
            if (!StringUtils.hasText(status)) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("status"), status);
        };
    }

    public static Specification<Decision> hasDecisionType(String decisionTypeId) {
        return (root, query, criteriaBuilder) -> {
            if (!StringUtils.hasText(decisionTypeId)) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("decisionType").get("id"), decisionTypeId);
        };
    }

    public static Specification<Decision> hasDecisionMakingBody(String decisionMakingBodyId) {
        return (root, query, criteriaBuilder) -> {
            if (!StringUtils.hasText(decisionMakingBodyId)) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("decisionMakingBody").get("id"), decisionMakingBodyId);
        };
    }

    public static Specification<Decision> hasDecisionMakingBody(Long decisionMakingBodyId) {
        return (root, query, criteriaBuilder) -> {
            if (decisionMakingBodyId == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("decisionMakingBody").get("id"), decisionMakingBodyId);
        };
    }

    public static Specification<Decision> hasDecisionType(Long decisionTypeId) {
        return (root, query, criteriaBuilder) -> {
            if (decisionTypeId == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("decisionType").get("id"), decisionTypeId);
        };
    }

    public static Specification<Decision> hasStatus(DecisionStatus status) {
        return (root, query, criteriaBuilder) -> {
            if (status == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("status"), status);
        };
    }

    public static Specification<Decision> buildSearchAndFilterSpecification(DecisionSearchRequest searchAndFilterDto) {
        Specification<Decision> spec = Specification.where(null);

        if (searchAndFilterDto == null) {
            return spec;
        }

        if (StringUtils.hasText(searchAndFilterDto.getSearchTerm())) {
            spec = spec.and(searchByTerm(searchAndFilterDto.getSearchTerm()));
        }

        if (searchAndFilterDto.getDecisionMakingBodyId() != null) {
            spec = spec.and(hasDecisionMakingBody(searchAndFilterDto.getDecisionMakingBodyId()));
        }

        if (searchAndFilterDto.getDecisionTypeId() != null) {
            spec = spec.and(hasDecisionType(searchAndFilterDto.getDecisionTypeId()));
        }

        if (searchAndFilterDto.getStatus() != null) {
            spec = spec.and(hasStatus(searchAndFilterDto.getStatus()));
        }

        return spec;
    }
}