package bank.recommendationservice.fintech.exception;

public class UnknownTransactionTypeException extends BaseBadRequestException {
    public UnknownTransactionTypeException(String s) {
        super(s);
    }
}
