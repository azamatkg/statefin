package kg.infosystems.statefin.dto.response.reference;

import com.fasterxml.jackson.annotation.JsonFormat;
import kg.infosystems.statefin.entity.common.ReferenceEntityStatus;
import lombok.*;

import java.time.LocalDateTime;

/**
 * DTO for notary office response.
 *
 * @author azamat
 * @version 1.0
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotaryOfficeResponse {

    private Long id;
    private Long version;

    private String nameEn;
    private String nameRu;
    private String nameKg;
    private String description;

    private String address;
    private String contactPhone;
    private String contactEmail;
    private String registrationNumberFormat;

    private ReferenceEntityStatus status;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;

    private String createdByUsername;
    private String updatedByUsername;

    private Boolean isReferencedByCollateralRegistrations;
}