package bank.recommendationservice.fintech.ruleimpl;

import bank.recommendationservice.fintech.exception.RepositoryNotInitializedException;
import bank.recommendationservice.fintech.other.ProductType;
import bank.recommendationservice.fintech.repository.RecommendationsRepository;
import bank.recommendationservice.fintech.interfaces.Rule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.validation.constraints.Null;
import java.util.UUID;

@Component
public class DebitOrSavingDepositsTotalGreaterThanOrEqualsTo50_000 implements Rule {
    private static final Logger logger = LoggerFactory.getLogger(DebitOrSavingDepositsTotalGreaterThanOrEqualsTo50_000.class);

    private final RecommendationsRepository recommendationsRepository;

    public DebitOrSavingDepositsTotalGreaterThanOrEqualsTo50_000(RecommendationsRepository recommendationsRepository) {
        if (recommendationsRepository == null) {
            logger.error("RecommendationsRepository не должен быть null");
            throw new RepositoryNotInitializedException("recommendationsRepository не должен быть null");
        }
        this.recommendationsRepository = recommendationsRepository;
    }

    @Override
    public boolean evaluate(UUID userId) {
        if (userId == null) {
            throw new NullPointerException("userId не должен быть null");
        }

        int debitDepositsTotal = recommendationsRepository.getDepositsOfTypeTotal(userId, ProductType.DEBIT.name());
        int savingDepositsTotal = recommendationsRepository.getDepositsOfTypeTotal(userId, ProductType.SAVING.name());

        int threshold = 50000;

        return debitDepositsTotal >= threshold || savingDepositsTotal >= threshold;
    }
}
