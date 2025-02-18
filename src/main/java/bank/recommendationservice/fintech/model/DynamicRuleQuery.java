package bank.recommendationservice.fintech.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Objects;

/**
 *  Модель запроса для динамического правила.
 */
@Entity
@Table(name = "dynamic_rule_query")
@Getter
@Setter
@Schema(description = "Модель запроса для динамического правила")
public class DynamicRuleQuery {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "dynamic_rule_query_seq_gen")
    @SequenceGenerator(name = "dynamic_rule_query_seq_gen", sequenceName = "dynamic_rule_query_seq", allocationSize = 1)
    @JsonIgnore
    @Schema(description = "Уникальный идентификатор запроса", example = "1")
    private Long id;

    @Column(name = "query", nullable = false)
    @Schema(description = "Строка запроса", example = "USER_OF")
    private String query;

    @ElementCollection
    @CollectionTable(name = "query_arguments", joinColumns = @JoinColumn(name = "dynamic_rule_query_id"))

    @Schema(description = "Список аргументов для запроса", example = "[\"CREDIT\"]")

    private List<String> arguments;

    @Column(name = "negate", nullable = false)
    @Schema(description = "Флаг отрицания запроса", example = "false")
    private boolean negate;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "dynamic_rule_id", nullable = false)
    private DynamicRule dynamicRule;

    public DynamicRuleQuery() {
    }

    public DynamicRuleQuery(String query, List<String> arguments) {
        this.query = query;
        this.arguments = arguments;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        DynamicRuleQuery that = (DynamicRuleQuery) object;
        return negate == that.negate
                && Objects.equals(id, that.id)
                && Objects.equals(query, that.query)
                && Objects.equals(arguments, that.arguments);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, query, arguments, negate);
    }

    @Override
    public String toString() {
        return "DynamicRuleQuery{" +
                "id=" + id +
                ", query='" + query + '\'' +
                ", arguments=" + arguments +
                ", negate=" + negate +
                '}';
    }
}