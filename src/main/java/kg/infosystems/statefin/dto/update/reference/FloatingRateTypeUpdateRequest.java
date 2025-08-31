package kg.infosystems.statefin.dto.update.reference;

import jakarta.validation.constraints.Size;
import kg.infosystems.statefin.entity.common.ReferenceEntityStatus;
import lombok.*;

/**
 * DTO for updating an existing floating rate type.
 *
 * @author azamat
 * @version 1.0
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FloatingRateTypeUpdateRequest {

    @Size(max = 100, message = "{floating.rate.type.name.english.size}")
    private String nameEn;

    @Size(max = 100, message = "{floating.rate.type.name.russian.size}")
    private String nameRu;

    @Size(max = 100, message = "{floating.rate.type.name.kyrgyz.size}")
    private String nameKg;

    @Size(max = 500, message = "{floating.rate.type.description.size}")
    private String description;

    private ReferenceEntityStatus status;
}