package kg.infosystems.statefin.entity.common;

import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.MappedSuperclass;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import kg.infosystems.statefin.entity.BaseEntity;
import lombok.*;
import lombok.experimental.SuperBuilder;

@MappedSuperclass
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public abstract class BaseMultilingualReferenceEntity extends BaseEntity {

    @NotBlank(message = "{reference.name.english.required}")
    @Size(max = 100, message = "{reference.name.english.size}")
    @Column(name = "name_en", nullable = false, length = 100)
    private String nameEn;

    @NotBlank(message = "{reference.name.russian.required}")
    @Size(max = 100, message = "{reference.name.russian.size}")
    @Column(name = "name_ru", nullable = false, length = 100)
    private String nameRu;

    @NotBlank(message = "{reference.name.kyrgyz.required}")
    @Size(max = 100, message = "{reference.name.kyrgyz.size}")
    @Column(name = "name_kg", nullable = false, length = 100)
    private String nameKg;

    @Size(max = 500, message = "{reference.description.size}")
    @Column(name = "description", length = 500)
    private String description;

    @NotNull(message = "{reference.status.required}")
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private ReferenceEntityStatus status = ReferenceEntityStatus.ACTIVE;

    public String getLocalizedName(String language) {
        if (language == null) {
            return nameEn;
        }
        return switch (language.toLowerCase()) {
            case "ru" -> nameRu;
            case "kg", "ky" -> nameKg;
            default -> nameEn;
        };
    }

    public boolean isActive() {
        return ReferenceEntityStatus.ACTIVE.equals(this.status);
    }
}