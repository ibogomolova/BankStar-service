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
 * Правило: сумма трат по всем продуктам типа DEBIT должно быть больше 100 000
 */
@Component
public class DebitWithdrawsTotalGreaterThan100_000 implements Rule {
    private static final Logger logger = LoggerFactory.getLogger(DebitWithdrawsTotalGreaterThan100_000.class);

    private final RecommendationsRepository recommendationsRepository;

    public DebitWithdrawsTotalGreaterThan100_000(RecommendationsRepository recommendationsRepository) {
        if (recommendationsRepository == null) {
            logger.error("RecommendationsRepository не должен быть null");
            throw new RepositoryNotInitializedException("recommendationsRepository не должен быть null");
        }
        this.recommendationsRepository = recommendationsRepository;
    }

    /**
     * Выполняет проверку правила:
     * сумма трат по всем продуктам типа DEBIT должно быть больше 100 000
     *
     * @param userId id пользователя, для которого выполняется проверка
     * @return true, если сумма трат по всем продуктам типа DEBIT больше 100 000,
     * false - в противном случае
     */
    @Override
    public boolean evaluate(UUID userId) {
        if (userId == null) {
            logger.error("userId не должен быть null");
            throw new NullArgumentException("userId не должен быть null");
        }

        int debitWithdrawsTotal = recommendationsRepository.getWithdrawsOfTypeTotal(userId, ProductType.DEBIT.name());
        int threshold = 100_000;

        return debitWithdrawsTotal > threshold;
    }
}