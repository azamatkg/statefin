package kg.infosystems.statefin.dto.update.reference;

import jakarta.validation.constraints.Size;
import kg.infosystems.statefin.entity.common.ReferenceEntityStatus;
import lombok.*;

/**
 * DTO for updating an existing decision type.
 *
 * @author azamat
 * @version 1.0
 */
@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class DecisionTypeUpdateRequest {
    
    @Size(max = 100, message = "{reference.name.english.size}")
    private String nameEn;

    @Size(max = 100, message = "{reference.name.russian.size}")
    private String nameRu;

    @Size(max = 100, message = "{reference.name.kyrgyz.size}")
    private String nameKg;

    @Size(max = 500, message = "{reference.description.size}")
    private String description;

    private ReferenceEntityStatus status;
}