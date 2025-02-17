package bank.recommendationservice.fintech.other;

import bank.recommendationservice.fintech.exception.UnknownQueryTypeException;

import java.util.Arrays;

public enum QueryType {
    USER_OF("USER_OF"),

    ACTIVE_USER_OF("ACTIVE_USER_OF"),

    TRANSACTION_SUM_COMPARE("TRANSACTION_SUM_COMPARE"),

    TRANSACTION_SUM_COMPARE_DEPOSIT_WITHDRAW("TRANSACTION_SUM_COMPARE_DEPOSIT_WITHDRAW");

    private final String queryType;

    QueryType(String queryType) {
        this.queryType = queryType;
    }

    public String getQueryType() {
        return queryType;
    }

    public static boolean isValidQuery(String query) {
        return Arrays.stream(QueryType.values())
                .anyMatch(q -> q.getQueryType().equals(query));
    }

    public static QueryType fromString(String query) {
        for (QueryType type : QueryType.values()) {
            if (type.getQueryType().equals(query)) {
                return type;
            }
        }
        throw new UnknownQueryTypeException("Неизвестный тип запроса: " + query);
    }
}