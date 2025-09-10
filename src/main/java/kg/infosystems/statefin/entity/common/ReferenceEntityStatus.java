package kg.infosystems.statefin.entity.common;

import lombok.Getter;

@Getter
public enum ReferenceEntityStatus {
    ACTIVE("Active", "Активный", "Активдүү"),
    INACTIVE("Inactive", "Неактивный", "Активдүү эмес");

    private final String nameEn;
    private final String nameRu;
    private final String nameKg;

    ReferenceEntityStatus(String nameEn, String nameRu, String nameKg) {
        this.nameEn = nameEn;
        this.nameRu = nameRu;
        this.nameKg = nameKg;
    }

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
}