package kg.infosystems.statefin.dto.request.reference;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import kg.infosystems.statefin.entity.common.ReferenceEntityStatus;
import lombok.*;

/**
 * DTO for creating a new repayment order.
 *
 * @author azamat
 * @version 1.0
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RepaymentOrderCreateRequest {

    @NotBlank(message = "{repayment.order.name.english.required}")
    @Size(max = 100, message = "{repayment.order.name.english.size}")
    private String nameEn;

    @NotBlank(message = "{repayment.order.name.russian.required}")
    @Size(max = 100, message = "{repayment.order.name.russian.size}")
    private String nameRu;

    @NotBlank(message = "{repayment.order.name.kyrgyz.required}")
    @Size(max = 100, message = "{repayment.order.name.kyrgyz.size}")
    private String nameKg;

    @Size(max = 500, message = "{repayment.order.description.size}")
    private String description;

    private ReferenceEntityStatus status;
}