package kg.infosystems.statefin.dto.response.credit;

import kg.infosystems.statefin.entity.credit.decision.DecisionStatus;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

/**
 * DTO for decision response (includes related entities information).
 *
 * @author azamat
 * @version 1.0
 */
@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class DecisionResponse {
    
    private Long id;
    private String nameEn;
    private String nameRu;
    private String nameKg;
    private LocalDate date;
    private String number;
    private String decisionMakingBodyNameEn;
    private String decisionMakingBodyNameRu;
    private String decisionMakingBodyNameKg;
    private String decisionTypeNameEn;
    private String decisionTypeNameRu;
    private String decisionTypeNameKg;
    private String description;
    private DecisionStatus status;
    private UUID documentPackageId;
}