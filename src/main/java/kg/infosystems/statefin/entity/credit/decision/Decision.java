package kg.infosystems.statefin.entity.credit.decision;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import kg.infosystems.statefin.entity.BaseEntity;
import kg.infosystems.statefin.entity.reference.DecisionMakingBody;
import kg.infosystems.statefin.entity.reference.DecisionType;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Entity
@Table(name = "decisions", uniqueConstraints = {
    @UniqueConstraint(columnNames = "name_ru", name = "uk_decision_name")
})
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true, exclude = {"documentPackage", "decisionMakingBody", "decisionType", "creditPrograms"})
public class Decision extends BaseEntity {

    @Column(name = "name_en", nullable = false, unique = true, length = 50)
    private String nameEn;

    @Column(name = "name_ru", nullable = false, length = 50)
    private String nameRu;

    @Column(name = "name_kg", nullable = false, length = 50)
    private String nameKg;

    @Column(name = "date", nullable = false)
    private LocalDate date;

    @Column(name = "number", nullable = false, length = 50)
    private String number;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "decision_making_body_id", nullable = false, referencedColumnName = "id")
    private DecisionMakingBody decisionMakingBody;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "decision_type_id", nullable = false, referencedColumnName = "id")
    private DecisionType decisionType;

    @Column(name = "description", length = 1000)
    private String description;

    @NotNull(message = "{decision.status.required}")
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private DecisionStatus status = DecisionStatus.DRAFT;

    /*

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "document_package_id", referencedColumnName = "id")
    private DocumentFilePackage documentPackage;

    @OneToMany(mappedBy = "decision", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @Builder.Default
    private List<CreditProgram> creditPrograms = new ArrayList<>();

     */
    public String getLocalizedName(String language) {
        return switch (language.toLowerCase()) {
            case "ru" -> nameRu;
            case "kg" -> nameKg;
            default -> nameEn;
        };
    }

    public String toString(Locale locale) {
        String language = locale.getLanguage().toLowerCase();
        String localizedName = getLocalizedName(language);
        return String.format("%s %s %s", localizedName, number, date);
    }

    public String getDecisionNumber() {
        return this.number;
    }

    @Override
    public String toString() {
        return toString(Locale.ENGLISH);
    }

    public boolean isActive() {
        return DecisionStatus.ACTIVE.equals(this.status);
    }

    public boolean isAvailableForCreditPrograms() {
        return !DecisionStatus.REJECTED.equals(this.status);
    }

//    public boolean canBeDeleted() {
//        return creditPrograms == null || creditPrograms.isEmpty();
//    }

    public boolean isFinalState() {
        return DecisionStatus.ACTIVE.equals(this.status) || 
               DecisionStatus.INACTIVE.equals(this.status) || 
               DecisionStatus.REJECTED.equals(this.status);
    }

//    public void updateStatusFromCreditPrograms() {
//        if (creditPrograms == null || creditPrograms.isEmpty()) {
//            return;
//        }
//
//        // Check if any credit program is DRAFT - this takes highest priority
//        boolean hasDraftProgram = creditPrograms.stream()
//            .anyMatch(cp -> cp.getStatus() == ProgramStatus.DRAFT);
//
//        if (hasDraftProgram) {
//            this.status = DecisionStatus.DRAFT;
//            return;
//        }
//
//        // Check if all credit programs are ACTIVE
//        boolean allActive = creditPrograms.stream()
//            .allMatch(cp -> cp.getStatus() == ProgramStatus.ACTIVE);
//
//        if (allActive) {
//            this.status = DecisionStatus.ACTIVE;
//            return;
//        }
//
//        // Handle suspended/closed programs first - they take precedence
//        boolean hasSuspendedOrClosed = creditPrograms.stream()
//            .anyMatch(cp -> cp.getStatus() == ProgramStatus.SUSPENDED ||
//                           cp.getStatus() == ProgramStatus.CLOSED);
//
//        if (hasSuspendedOrClosed) {
//            this.status = DecisionStatus.INACTIVE;
//            return;
//        }
//
//        // Handle other intermediate statuses
//        boolean hasPendingApproval = creditPrograms.stream()
//            .anyMatch(cp -> cp.getStatus() == ProgramStatus.PENDING_APPROVAL);
//
//        if (hasPendingApproval) {
//            this.status = DecisionStatus.PENDING_CONFIRMATION;
//            return;
//        }
//
//        boolean hasApproved = creditPrograms.stream()
//            .anyMatch(cp -> cp.getStatus() == ProgramStatus.APPROVED);
//
//        if (hasApproved) {
//            this.status = DecisionStatus.APPROVED;
//        }
//    }
}