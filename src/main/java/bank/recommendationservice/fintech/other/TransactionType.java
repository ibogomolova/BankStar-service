package bank.recommendationservice.fintech.other;

import bank.recommendationservice.fintech.exception.UnknownQueryTypeException;
import lombok.Getter;

import java.util.Arrays;

@Getter
public enum TransactionType {
    WITHDRAW("WITHDRAW"),
    DEPOSIT("DEPOSIT");

    private final String transactionType;

    TransactionType(String type) {
        this.transactionType = type;
    }

    public static TransactionType fromString(String transactionType) {
        for (TransactionType type : TransactionType.values()) {
            if (type.getTransactionType().equals(transactionType)) {
                return type;
            }
        }
        throw new UnknownQueryTypeException("Неизвестный тип транзакции: " + transactionType);
    }
}
