package bank.recommendationservice.fintech.other;

import bank.recommendationservice.fintech.exception.UnknownComparisonTypeException;
import lombok.Getter;

@Getter
public enum ComparisonType {
    GREATER_THAN(">"),

    LESS_THAN("<"),

    EQUALS("="),

    GREATER_THAN_OR_EQUALS(">="),

    LESS_THAN_OR_EQUALS("<=");


    private final String comparisonType;

    ComparisonType(String comparisonType) {
        this.comparisonType = comparisonType;
    }

    public static ComparisonType fromString(String symbol) {
        return switch (symbol) {
            case ">" -> GREATER_THAN;
            case "<" -> LESS_THAN;
            case "=" -> EQUALS;
            case ">=" -> GREATER_THAN_OR_EQUALS;
            case "<=" -> LESS_THAN_OR_EQUALS;
            default -> throw new UnknownComparisonTypeException("Неизвестный тип сравнения: " + symbol);
        };
    }
}