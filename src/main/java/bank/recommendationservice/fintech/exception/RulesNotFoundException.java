package bank.recommendationservice.fintech.exception;

import lombok.Getter;

@Getter
public class RulesNotFoundException extends BaseNotFoundException {
    private final Long ruleId;

    public RulesNotFoundException(String message, Long ruleId) {
        super(message);
        this.ruleId = ruleId;
    }
}
