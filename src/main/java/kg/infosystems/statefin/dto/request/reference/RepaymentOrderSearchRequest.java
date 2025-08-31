package kg.infosystems.statefin.dto.request.reference;

import kg.infosystems.statefin.entity.common.ReferenceEntityStatus;
import lombok.*;

/**
 * DTO for searching repayment orders.
 *
 * @author azamat
 * @version 1.0
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RepaymentOrderSearchRequest {

    private String searchTerm;
    private ReferenceEntityStatus status;
}