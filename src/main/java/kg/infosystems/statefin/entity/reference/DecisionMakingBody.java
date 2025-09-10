package kg.infosystems.statefin.entity.reference;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import kg.infosystems.statefin.entity.common.BaseMultilingualReferenceEntity;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "decision_making_bodies", uniqueConstraints = {
        @UniqueConstraint(columnNames = "name_ru", name = "uk_decision_making_body_name_ru")
})
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@ToString
@EqualsAndHashCode(callSuper = true)
public class DecisionMakingBody extends BaseMultilingualReferenceEntity {
}