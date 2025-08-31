package kg.infosystems.statefin.dto.request.reference;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import kg.infosystems.statefin.entity.common.ReferenceEntityStatus;
import lombok.*;

/**
 * DTO for creating a new decision making body.
 *
 * @author azamat
 * @version 1.0
 */
@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class DecisionMakingBodyCreateRequest {
    
    @NotBlank(message = "{reference.name.english.required}")
    @Size(max = 100, message = "{reference.name.english.size}")
    private String nameEn;

    @NotBlank(message = "{reference.name.russian.required}")
    @Size(max = 100, message = "{reference.name.russian.size}")
    private String nameRu;

    @NotBlank(message = "{reference.name.kyrgyz.required}")
    @Size(max = 100, message = "{reference.name.kyrgyz.size}")
    private String nameKg;

    @Size(max = 500, message = "{reference.description.size}")
    private String description;

    @NotNull(message = "{reference.status.required}")
    private ReferenceEntityStatus status;
}