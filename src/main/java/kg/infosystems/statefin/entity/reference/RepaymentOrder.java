package kg.infosystems.statefin.entity.reference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.Size;
import kg.infosystems.statefin.entity.common.BaseMultilingualReferenceEntity;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "repayment_orders", uniqueConstraints = {
        @UniqueConstraint(columnNames = "name_ru", name = "uk_repayment_order_name_ru")
})
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@ToString
@EqualsAndHashCode(callSuper = true)
public class RepaymentOrder extends BaseMultilingualReferenceEntity {

    @Column(name = "code", length = 20)
    private String code;

    @Column(name = "order_priority")
    private Integer orderPriority;
}