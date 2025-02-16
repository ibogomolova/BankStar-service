package bank.recommendationservice.fintech.exception;

public class UnknownComparisonTypeException extends BaseBadRequestException {
    public UnknownComparisonTypeException(String s) {
        super(s);
    }
}