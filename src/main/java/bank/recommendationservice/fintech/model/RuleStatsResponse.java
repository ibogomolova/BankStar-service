package bank.recommendationservice.fintech.model;

import bank.recommendationservice.fintech.dto.RuleStatsDTO;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class RuleStatsResponse {
    @JsonProperty("stats")
    private List<RuleStatsDTO> stats;

    public RuleStatsResponse() {
    }

    public RuleStatsResponse(List<RuleStatsDTO> stats) {
        this.stats = stats;
    }
}
