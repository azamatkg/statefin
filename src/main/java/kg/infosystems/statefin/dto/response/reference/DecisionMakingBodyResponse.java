package kg.infosystems.statefin.dto.response.reference;

import kg.infosystems.statefin.entity.common.ReferenceEntityStatus;
import lombok.*;

import java.time.LocalDateTime;

/**
 * DTO for decision making body response.
 *
 * @author azamat
 * @version 1.0
 */
@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class DecisionMakingBodyResponse {
    
    private Long id;
    private String nameEn;
    private String nameRu;
    private String nameKg;
    private String description;
    private ReferenceEntityStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}