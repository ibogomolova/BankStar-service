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
 * Сумма пополнений продуктов с типом SAVING больше 1000 ₽
 */
@Component
public class SavingDepositsTotalGreaterThan1_000 implements Rule {
    private static final Logger logger = LoggerFactory.getLogger(SavingDepositsTotalGreaterThan1_000.class);
    private final RecommendationsRepository recommendationsRepository;

    public SavingDepositsTotalGreaterThan1_000(RecommendationsRepository recommendationsRepository) {
        if (recommendationsRepository == null) {
            logger.error("RecommendationsRepository не должен быть null");
            throw new RepositoryNotInitializedException("recommendationsRepository не должен быть null");
        }
        this.recommendationsRepository = recommendationsRepository;
    }

    /**
     * Оценка правила {@link SavingDepositsTotalGreaterThan1_000}.
     * <p>
     * Проверяет, что сумма пополнений продуктов с типом {@link ProductType#SAVING} для
     * пользователя с идентификатором {@code userId} больше 1000 ₽.
     *
     * @param userId идентификатор пользователя
     * @return {@code true}, если сумма пополнений продуктов с типом {@link ProductType#SAVING}
     * больше 1000 ₽, {@code false} - иначе
     * @throws NullArgumentException если {@code userId} - {@code null}
     */
    @Override
    public boolean evaluate(UUID userId) {
        if (userId == null) {
            logger.error("userId не должен быть null");
            throw new NullArgumentException("userId не должен быть null");
        }
        int total = recommendationsRepository.getDepositsOfTypeTotal(userId, ProductType.SAVING.name());
        int threshold = 1000;

        return total > threshold;
    }
}