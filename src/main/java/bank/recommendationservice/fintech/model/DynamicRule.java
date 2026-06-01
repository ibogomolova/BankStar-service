package bank.recommendationservice.fintech.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 *  Модель динамического правила для генерации рекомендаций
 */
@Entity
@Table(name = "dynamic_rule")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Модель динамического правила")
public class DynamicRule {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "dynamic_rule_seq_gen")
    @SequenceGenerator(name = "dynamic_rule_seq_gen", sequenceName = "dynamic_rule_seq", allocationSize = 1)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Schema(description = "Уникальный идентификатор динамического правила", example = "1")
    private Long id;

    @Column(name = "product_name", nullable = false)
    @Schema(description = "Название продукта", example = "Кредитная карта")
    private String productName;

    @Column(name = "product_id", nullable = false)
    @Schema(description = "Уникальный идентификатор продукта", example = "550e8400-e29b-41d4-a716-446655440000")
    private UUID productId;

    @Column(name = "product_text", nullable = false)
    @Schema(description = "Текст продукта", example = "Предлагаем вам кредитную карту с выгодными условиями")
    private String productText;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "dynamicRule", orphanRemoval = true)
    @JsonProperty("rule")
    @Schema(description = "Список запросов для динамического правила")
    private List<DynamicRuleQuery> queries;

    public DynamicRule(String productName, UUID productId, String productText, List<DynamicRuleQuery> queries) {
        this.productName = productName;
        this.productId = productId;
        this.productText = productText;
        this.queries = queries;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        DynamicRule that = (DynamicRule) object;
        return Objects.equals(id, that.id)
                && Objects.equals(productName, that.productName)
                && Objects.equals(productId, that.productId)
                && Objects.equals(productText, that.productText)
                && Objects.equals(queries, that.queries);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, productName, productId, productText, queries);
    }

    @Override
    public String toString() {
        return "DynamicRule{" +
                "id=" + id +
                ", productName='" + productName + '\'' +
                ", productId=" + productId +
                ", productText='" + productText + '\'' +
                ", queries=" + queries +
                '}';
    }
}