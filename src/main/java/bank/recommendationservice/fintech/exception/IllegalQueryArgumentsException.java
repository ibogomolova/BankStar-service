package bank.recommendationservice.fintech.exception;

public class IllegalQueryArgumentsException extends BaseBadRequestException {
    public IllegalQueryArgumentsException(String message) {
        super(message);
    }

    public IllegalQueryArgumentsException(String message, Throwable cause) {
        super(message, cause);
    }
}
