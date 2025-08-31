package kg.infosystems.statefin.dto.request.reference;

import kg.infosystems.statefin.entity.common.ReferenceEntityStatus;
import lombok.*;

/**
 * DTO for credit purpose search criteria.
 *
 * @author azamat
 * @version 1.0
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreditPurposeSearchRequest {

    private String searchTerm;
    private ReferenceEntityStatus status;
}