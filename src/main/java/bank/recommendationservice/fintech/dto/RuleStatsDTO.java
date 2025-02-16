package bank.recommendationservice.fintech.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
@Schema(description = "DTO для представления счетчика срабатываний динамических правил")
public class RuleStatsDTO {

    @JsonProperty("rule_id")
    private Long ruleId;

    @JsonProperty("count")
    private int count;

    public RuleStatsDTO(Long ruleId, int count) {
        this.ruleId = ruleId;
        this.count = count;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RuleStatsDTO that = (RuleStatsDTO) o;
        return count == that.count && Objects.equals(ruleId, that.ruleId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ruleId, count);
    }

    @Override
    public String toString() {
        return "RuleStatsDTO{" +
                "ruleId=" + ruleId +
                ", count=" + count +
                '}';
    }
}
