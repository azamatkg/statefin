package kg.infosystems.statefin.dto.update.reference;

import jakarta.validation.constraints.Size;
import kg.infosystems.statefin.entity.common.ReferenceEntityStatus;
import lombok.*;

/**
 * DTO for updating an existing repayment order.
 *
 * @author azamat
 * @version 1.0
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RepaymentOrderUpdateRequest {

    @Size(max = 100, message = "{repayment.order.name.english.size}")
    private String nameEn;

    @Size(max = 100, message = "{repayment.order.name.russian.size}")
    private String nameRu;

    @Size(max = 100, message = "{repayment.order.name.kyrgyz.size}")
    private String nameKg;

    @Size(max = 500, message = "{repayment.order.description.size}")
    private String description;

    private ReferenceEntityStatus status;
}