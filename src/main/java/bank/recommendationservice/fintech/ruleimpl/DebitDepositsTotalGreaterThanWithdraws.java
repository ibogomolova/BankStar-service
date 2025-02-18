package bank.recommendationservice.fintech.ruleimpl;

import bank.recommendationservice.fintech.exception.NullArgumentException;
import bank.recommendationservice.fintech.exception.RepositoryNotInitializedException;
import bank.recommendationservice.fintech.interfaces.Rule;
import bank.recommendationservice.fintech.other.ProductType;
import bank.recommendationservice.fintech.repository.RecommendationsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Правило:
 * Сумма пополнений по всем продуктам типа DEBIT больше,
 * чем сумма трат по всем продуктам типа DEBIT.
 */
@Component
public class DebitDepositsTotalGreaterThanWithdraws implements Rule {
    private static final Logger logger = LoggerFactory.getLogger(DebitDepositsTotalGreaterThanWithdraws.class);

    private final RecommendationsRepository recommendationsRepository;

    public DebitDepositsTotalGreaterThanWithdraws(RecommendationsRepository recommendationsRepository) {
        if (recommendationsRepository == null) {
            logger.error("RecommendationsRepository не должен быть null");
            throw new RepositoryNotInitializedException("recommendationsRepository не должен быть null");
        }
        this.recommendationsRepository = recommendationsRepository;
    }

    /**
     * Оценка правил:
     * - userId != null;
     * - сумма пополнений по всем продуктам типа DEBIT больше,
     * чем сумма трат по всем продуктам типа DEBIT.
     */
    @Override
    public boolean evaluate(UUID userId) {
        if (userId == null) {
            logger.error("userId не должен быть null");
            throw new NullArgumentException("userId не должен быть null");
        }

        int debitDepositsTotal = recommendationsRepository.getDepositsOfTypeTotal(userId, ProductType.DEBIT.name());
        int debitWithdrawsTotal = recommendationsRepository.getWithdrawsOfTypeTotal(userId, ProductType.DEBIT.name());

        return debitDepositsTotal > debitWithdrawsTotal;
    }
}