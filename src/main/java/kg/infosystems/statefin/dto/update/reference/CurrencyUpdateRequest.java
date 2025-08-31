package kg.infosystems.statefin.dto.update.reference;

import jakarta.validation.constraints.Size;
import kg.infosystems.statefin.entity.common.ReferenceEntityStatus;
import lombok.*;

/**
 * DTO for updating an existing currency.
 *
 * @author azamat
 * @version 1.0
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CurrencyUpdateRequest {

    @Size(max = 100, message = "{currency.name.english.size}")
    private String nameEn;

    @Size(max = 100, message = "{currency.name.russian.size}")
    private String nameRu;

    @Size(max = 100, message = "{currency.name.kyrgyz.size}")
    private String nameKg;

    @Size(max = 500, message = "{currency.description.size}")
    private String description;

    private ReferenceEntityStatus status;
}