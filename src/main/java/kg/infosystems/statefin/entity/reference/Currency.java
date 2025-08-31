package kg.infosystems.statefin.entity.reference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import kg.infosystems.statefin.entity.common.BaseMultilingualReferenceEntity;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "currencies", uniqueConstraints = {
        @UniqueConstraint(columnNames = "code", name = "uk_currency_code"),
        @UniqueConstraint(columnNames = "name_ru", name = "uk_currency_name_ru")
})
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@ToString
@EqualsAndHashCode(callSuper = true)
public class Currency extends BaseMultilingualReferenceEntity {

    @NotBlank(message = "{currency.code.required}")
    @Pattern(regexp = "[A-Z]{3}", message = "{currency.code.pattern}")
    @Size(min = 3, max = 3, message = "{currency.code.size}")
    @Column(name = "code", nullable = false, length = 3, unique = true)
    private String code;

    @Size(max = 5, message = "{currency.symbol.size}")
    @Column(name = "symbol", length = 5)
    private String symbol;
}