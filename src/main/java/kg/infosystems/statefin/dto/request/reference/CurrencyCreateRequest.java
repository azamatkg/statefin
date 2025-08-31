package kg.infosystems.statefin.dto.request.reference;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import kg.infosystems.statefin.entity.common.ReferenceEntityStatus;
import lombok.*;

/**
 * DTO for creating a new currency.
 *
 * @author azamat
 * @version 1.0
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CurrencyCreateRequest {

    @NotBlank(message = "{currency.code.required}")
    @Size(max = 3, message = "{currency.code.size}")
    private String code;

    @NotBlank(message = "{currency.name.english.required}")
    @Size(max = 100, message = "{currency.name.english.size}")
    private String nameEn;

    @NotBlank(message = "{currency.name.russian.required}")
    @Size(max = 100, message = "{currency.name.russian.size}")
    private String nameRu;

    @NotBlank(message = "{currency.name.kyrgyz.required}")
    @Size(max = 100, message = "{currency.name.kyrgyz.size}")
    private String nameKg;

    @Size(max = 500, message = "{currency.description.size}")
    private String description;

    private ReferenceEntityStatus status;
}