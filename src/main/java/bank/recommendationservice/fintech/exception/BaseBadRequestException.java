package bank.recommendationservice.fintech.exception;

public class BaseBadRequestException extends IllegalArgumentException {
    public BaseBadRequestException(String message) {
        super(message);
    }
}
