package bank.recommendationservice.fintech.exception;

public class NullArgumentException extends BaseBadRequestException {
    public NullArgumentException(String message) {
        super(message);
    }
}
