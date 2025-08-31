package kg.infosystems.statefin.dto.request.reference;

import kg.infosystems.statefin.entity.common.ReferenceEntityStatus;
import lombok.*;

/**
 * DTO for currency search criteria.
 *
 * @author azamat
 * @version 1.0
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CurrencySearchRequest {

    private String searchTerm;
    private ReferenceEntityStatus status;
}