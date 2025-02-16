package bank.recommendationservice.fintech.exception;

public class UnknownQueryTypeException extends BaseBadRequestException {
    public UnknownQueryTypeException(String s) {
        super(s);
    }
}
