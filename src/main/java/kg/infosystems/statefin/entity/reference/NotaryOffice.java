package kg.infosystems.statefin.entity.reference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import kg.infosystems.statefin.entity.common.BaseMultilingualReferenceEntity;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "notary_offices", uniqueConstraints = {
        @UniqueConstraint(columnNames = "name_ru", name = "uk_notary_office_name_ru")
})
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@ToString
@EqualsAndHashCode(callSuper = true)
public class NotaryOffice extends BaseMultilingualReferenceEntity {

    @Column(name = "address", length = 500)
    private String address;

    @Column(name = "contact_phone", length = 20)
    private String contactPhone;

    @Column(name = "contact_email", length = 100)
    private String contactEmail;

    @Column(name = "registration_number_format", length = 50)
    private String registrationNumberFormat;
}