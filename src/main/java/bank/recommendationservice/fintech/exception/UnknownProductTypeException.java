package bank.recommendationservice.fintech.exception;

public class UnknownProductTypeException extends BaseBadRequestException{
    public UnknownProductTypeException(String message) {
        super(message);
    }
}