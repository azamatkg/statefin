package kg.infosystems.statefin.dto.update.credit;

import jakarta.validation.constraints.Size;
import kg.infosystems.statefin.entity.credit.decision.DecisionStatus;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

/**
 * DTO for updating an existing decision.
 *
 * @author azamat
 * @version 1.0
 */
@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class DecisionUpdateRequest {
    
    @Size(max = 100, message = "{decision.name.english.size}")
    private String nameEn;

    @Size(max = 100, message = "{decision.name.russian.size}")
    private String nameRu;

    @Size(max = 100, message = "{decision.name.kyrgyz.size}")
    private String nameKg;

    private LocalDate date;

    @Size(max = 50, message = "{decision.number.size}")
    private String number;

    private Long decisionMakingBodyId;

    private Long decisionTypeId;

    @Size(max = 1000, message = "{decision.note.size}")
    private String description;

    private DecisionStatus status;

    private UUID documentPackageId;
}