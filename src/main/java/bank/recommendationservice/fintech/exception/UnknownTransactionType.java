package bank.recommendationservice.fintech.exception;

public class UnknownTransactionType extends BaseBadRequestException {
    public UnknownTransactionType(String s) {
        super(s);
    }
}