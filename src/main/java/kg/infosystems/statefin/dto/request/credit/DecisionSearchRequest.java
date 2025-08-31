package kg.infosystems.statefin.dto.request.credit;

import kg.infosystems.statefin.entity.credit.decision.DecisionStatus;
import lombok.*;

/**
 * DTO for decision search and filtering criteria.
 *
 * @author azamat
 * @version 1.0
 */
@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class DecisionSearchRequest {
    
    private String searchTerm;
    private Long decisionMakingBodyId;
    private Long decisionTypeId;
    private DecisionStatus status;
}