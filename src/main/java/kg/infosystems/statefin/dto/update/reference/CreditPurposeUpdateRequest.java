package kg.infosystems.statefin.dto.update.reference;

import jakarta.validation.constraints.Size;
import kg.infosystems.statefin.entity.common.ReferenceEntityStatus;
import lombok.*;

/**
 * DTO for updating an existing credit purpose.
 *
 * @author azamat
 * @version 1.0
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreditPurposeUpdateRequest {

    @Size(max = 100, message = "{credit.purpose.name.english.size}")
    private String nameEn;

    @Size(max = 100, message = "{credit.purpose.name.russian.size}")
    private String nameRu;

    @Size(max = 100, message = "{credit.purpose.name.kyrgyz.size}")
    private String nameKg;

    @Size(max = 500, message = "{credit.purpose.description.size}")
    private String description;

    private ReferenceEntityStatus status;
}