package kg.infosystems.statefin.entity.credit.decision;

import java.util.Locale;

public enum DecisionStatus {
    
    DRAFT("Draft", "Черновик", "Долбоор"),
    PENDING_CONFIRMATION("Pending Confirmation", "Ожидает подтверждения", "Ырастоону күтүүдө"),
    APPROVED("Approved", "Утвержден", "Бекитилген"),
    REJECTED("Rejected", "Отклонен", "Четке кагылган"),
    ACTIVE("Active", "Активен", "Активдүү"),
    INACTIVE("Inactive", "Неактивен", "Активдүү эмес");

    private final String nameEn;
    private final String nameRu;
    private final String nameKg;

    DecisionStatus(String nameEn, String nameRu, String nameKg) {
        this.nameEn = nameEn;
        this.nameRu = nameRu;
        this.nameKg = nameKg;
    }

    public String getLocalizedName(String language) {
        return switch (language.toLowerCase()) {
            case "ru" -> nameRu;
            case "kg" -> nameKg;
            default -> nameEn;
        };
    }

    public String getName(Locale locale) {
        String language = locale.getLanguage().toLowerCase();
        return getLocalizedName(language);
    }

    public String getNameEn() {
        return nameEn;
    }

    public String getNameRu() {
        return nameRu;
    }

    public String getNameKg() {
        return nameKg;
    }
}