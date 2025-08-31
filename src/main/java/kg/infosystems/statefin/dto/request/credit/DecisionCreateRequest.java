package kg.infosystems.statefin.dto.request.credit;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import kg.infosystems.statefin.entity.credit.decision.DecisionStatus;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

/**
 * DTO for creating a new decision.
 *
 * @author azamat
 * @version 1.0
 */
@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class DecisionCreateRequest {
    
    @NotBlank(message = "{decision.name.english.required}")
    @Size(max = 100, message = "{decision.name.english.size}")
    private String nameEn;

    @NotBlank(message = "{decision.name.russian.required}")
    @Size(max = 100, message = "{decision.name.russian.size}")
    private String nameRu;

    @NotBlank(message = "{decision.name.kyrgyz.required}")
    @Size(max = 100, message = "{decision.name.kyrgyz.size}")
    private String nameKg;

    @NotNull(message = "{decision.date.required}")
    private LocalDate date;

    @NotBlank(message = "{decision.number.required}")
    @Size(max = 50, message = "{decision.number.size}")
    private String number;

    @NotNull(message = "{decision.body.required}")
    private Long decisionMakingBodyId;

    @NotNull(message = "{decision.type.required}")
    private Long decisionTypeId;

    @Size(max = 1000, message = "{decision.note.size}")
    private String description;

    @NotNull(message = "{decision.status.required}")
    private DecisionStatus status;

    private UUID documentPackageId;
}