package kg.infosystems.statefin.dto.request.reference;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import kg.infosystems.statefin.entity.common.ReferenceEntityStatus;
import lombok.*;

/**
 * DTO for creating a new notary office.
 *
 * @author azamat
 * @version 1.0
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotaryOfficeCreateRequest {

    @NotBlank(message = "{notary.office.name.english.required}")
    @Size(max = 100, message = "{notary.office.name.english.size}")
    private String nameEn;

    @NotBlank(message = "{notary.office.name.russian.required}")
    @Size(max = 100, message = "{notary.office.name.russian.size}")
    private String nameRu;

    @NotBlank(message = "{notary.office.name.kyrgyz.required}")
    @Size(max = 100, message = "{notary.office.name.kyrgyz.size}")
    private String nameKg;

    @Size(max = 500, message = "{notary.office.description.size}")
    private String description;

    @Size(max = 500, message = "{notary.office.address.size}")
    private String address;

    @Pattern(regexp = "\\+996\\d{9}", message = "{notary.office.phone.pattern}")
    private String contactPhone;

    @Email(message = "{notary.office.email.pattern}")
    @Size(max = 100, message = "{notary.office.email.size}")
    private String contactEmail;

    @Size(max = 50, message = "{notary.office.registration.number.format.size}")
    private String registrationNumberFormat;

    private ReferenceEntityStatus status;
}