package bank.recommendationservice.fintech.exception;

public class RecommendationNotFoundException extends BaseNotFoundException {
    public RecommendationNotFoundException(String message) {
        super(message);
    }
}