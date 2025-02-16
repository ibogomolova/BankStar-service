package bank.recommendationservice.fintech.exception;

public abstract class BaseNotFoundException extends RuntimeException {
    public BaseNotFoundException(String message) {
        super(message);
    }
}
