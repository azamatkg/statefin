package kg.infosystems.statefin.dto.request.reference;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import kg.infosystems.statefin.entity.common.ReferenceEntityStatus;
import lombok.*;

/**
 * DTO for creating a new floating rate type.
 *
 * @author azamat
 * @version 1.0
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FloatingRateTypeCreateRequest {

    @NotBlank(message = "{floating.rate.type.name.english.required}")
    @Size(max = 100, message = "{floating.rate.type.name.english.size}")
    private String nameEn;

    @NotBlank(message = "{floating.rate.type.name.russian.required}")
    @Size(max = 100, message = "{floating.rate.type.name.russian.size}")
    private String nameRu;

    @NotBlank(message = "{floating.rate.type.name.kyrgyz.required}")
    @Size(max = 100, message = "{floating.rate.type.name.kyrgyz.size}")
    private String nameKg;

    @Size(max = 500, message = "{floating.rate.type.description.size}")
    private String description;

    private ReferenceEntityStatus status;
}