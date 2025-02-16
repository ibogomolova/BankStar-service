package bank.recommendationservice.fintech.exception;

public class NoTransactionsFoundException extends BaseNotFoundException {
    public NoTransactionsFoundException(String message) {
        super(message);
    }
}