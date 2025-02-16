package bank.recommendationservice.fintech.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Entity
@Table(name = "rule_stats")
@Getter
@Setter
@Schema(description = "Модель статистики срабатывания правил")
public class RuleStats {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "rule_stats_seq_gen")
    @SequenceGenerator(name = "rule_stats_seq_gen", sequenceName = "rule_stats_seq", allocationSize = 1)
    @JsonIgnore
    @Schema(description = "Уникальный идентификатор статистики", example = "1")
    private Long id;

    @Column(name = "count", nullable = false)
    @Schema(description = "Счетчик срабатываний рекомендаций", example = "0")
    private Integer count;

    @JsonIgnore
    @OneToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE})
    @JoinColumn(name = "dynamic_rule_id", nullable = false, unique = true)
    @Schema(description = "Динамическое правило, к которому относится статистика", example = "1")
    private DynamicRule dynamicRule;

    public RuleStats() {
        this.count = 0; // Инициализация счетчика
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RuleStats ruleStats = (RuleStats) o;
        return Objects.equals(id, ruleStats.id)
                && Objects.equals(count, ruleStats.count)
                && Objects.equals(dynamicRule, ruleStats.dynamicRule);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, count, dynamicRule);
    }

    @Override
    public String toString() {
        return "RuleStats{" +
                "id=" + id +
                ", count=" + count +
                ", dynamicRuleQuery=" + dynamicRule +
                '}';
    }
}