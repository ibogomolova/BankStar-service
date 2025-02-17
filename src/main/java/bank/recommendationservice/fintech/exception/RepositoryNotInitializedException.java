package bank.recommendationservice.fintech.exception;

public class RepositoryNotInitializedException extends RuntimeException {
    public RepositoryNotInitializedException() {
    }

    public RepositoryNotInitializedException(String message) {
        super(message);
    }
}