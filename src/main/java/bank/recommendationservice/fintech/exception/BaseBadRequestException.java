package bank.recommendationservice.fintech.exception;

public class BaseBadRequestException extends RuntimeException {
    public BaseBadRequestException(String message) {
        super(message);
    }

    public BaseBadRequestException(String message, Throwable cause) {
        super(message, cause);
    }
}
