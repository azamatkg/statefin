package kg.infosystems.statefin.dto.request.reference;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import kg.infosystems.statefin.entity.common.ReferenceEntityStatus;
import lombok.*;

/**
 * DTO for creating a new credit purpose.
 *
 * @author azamat
 * @version 1.0
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreditPurposeCreateRequest {

    @NotBlank(message = "{credit.purpose.name.english.required}")
    @Size(max = 100, message = "{credit.purpose.name.english.size}")
    private String nameEn;

    @NotBlank(message = "{credit.purpose.name.russian.required}")
    @Size(max = 100, message = "{credit.purpose.name.russian.size}")
    private String nameRu;

    @NotBlank(message = "{credit.purpose.name.kyrgyz.required}")
    @Size(max = 100, message = "{credit.purpose.name.kyrgyz.size}")
    private String nameKg;

    @Size(max = 500, message = "{credit.purpose.description.size}")
    private String description;

    private ReferenceEntityStatus status;
}