package bank.recommendationservice.fintech.rulesetimpl;

import bank.recommendationservice.fintech.dto.RecommendationDTO;
import bank.recommendationservice.fintech.exception.NullArgumentException;
import bank.recommendationservice.fintech.interfaces.RecommendationRuleSet;
import bank.recommendationservice.fintech.other.RuleSetText;
import bank.recommendationservice.fintech.ruleimpl.DebitDepositsTotalGreaterThanWithdraws;
import bank.recommendationservice.fintech.ruleimpl.DebitOrSavingDepositsTotalGreaterThanOrEqualsTo50_000;
import bank.recommendationservice.fintech.ruleimpl.UsesAtLeastOneDebitProduct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class TopSaving implements RecommendationRuleSet {
    private static final Logger logger = LoggerFactory.getLogger(TopSaving.class);

    private final UsesAtLeastOneDebitProduct usesAtLeastOneDebitProduct;
    private final DebitOrSavingDepositsTotalGreaterThanOrEqualsTo50_000 debitOrSavingDepositsTotalGreaterThanOrEqualsTo50_000;
    private final DebitDepositsTotalGreaterThanWithdraws debitDepositsTotalGreaterThanWithdraws;

    public TopSaving(UsesAtLeastOneDebitProduct usesAtLeastOneDebitProduct,
                     DebitOrSavingDepositsTotalGreaterThanOrEqualsTo50_000 debitOrSavingDepositsTotalGreaterThanOrEqualsTo50_000,
                     DebitDepositsTotalGreaterThanWithdraws debitDepositsTotalGreaterThanWithdraws) {
        if (usesAtLeastOneDebitProduct == null) {
            logger.error("Используемый продукт 'usesAtLeastOneDebitProduct' не должен быть null");
            throw new NullArgumentException("usesAtLeastOneDebitProduct не должен быть null");
        }
        if (debitOrSavingDepositsTotalGreaterThanOrEqualsTo50_000 == null) {
            logger.error("Используемый продукт 'debitOrSavingDepositsTotalGreaterThanOrEqualsTo50_000' не должен быть null");
            throw new NullArgumentException("debitOrSavingDepositsTotalGreaterThanOrEqualsTo50_000 не должен быть null");
        }
        if (debitDepositsTotalGreaterThanWithdraws == null) {
            logger.error("Используемый продукт 'debitDepositsTotalGreaterThanWithdraws' не должен быть null");
            throw new NullArgumentException("debitDepositsTotalGreaterThanWithdraws не должен быть null");
        }

        this.usesAtLeastOneDebitProduct = usesAtLeastOneDebitProduct;
        this.debitOrSavingDepositsTotalGreaterThanOrEqualsTo50_000 = debitOrSavingDepositsTotalGreaterThanOrEqualsTo50_000;
        this.debitDepositsTotalGreaterThanWithdraws = debitDepositsTotalGreaterThanWithdraws;
    }

    /**
     * Оценивает, является ли пользователь с данным ID подходит под критерии рекомендации "Top Saving".
     * Если пользователь имеет как минимум один продукт с типом DEBIT, сумму депозитов по продуктам DEBIT
     * или SAVING >= 50 000, и сумму депозитов по продуктам DEBIT > сумму трат по продуктам DEBIT,
     * то возвращает {@link RecommendationDTO} с деталями рекомендации.
     * @param userId ID пользователя, которого нужно оценить
     * @return {@link RecommendationDTO} с деталями рекомендации, если пользователь подходит
     * под критерии, null - в противном случае
     * @throws NullArgumentException если переданный userId является null
     */

    @Override
    public RecommendationDTO recommend(UUID userId) {
        logger.info("Вызван метод evaluate() из рулсета TopSaving");
        if (userId == null) {
            logger.error("userId не должен быть null");
            throw new NullArgumentException("userId не должен быть null");
        }

        boolean hasDebitProduct = usesAtLeastOneDebitProduct.evaluate(userId);
        boolean depositsGreaterThanOrEqualsTo50k = debitOrSavingDepositsTotalGreaterThanOrEqualsTo50_000.evaluate(userId);
        boolean depositsGreaterThanWithdrawals = debitDepositsTotalGreaterThanWithdraws.evaluate(userId);


        if (hasDebitProduct && depositsGreaterThanOrEqualsTo50k && depositsGreaterThanWithdrawals) {
            logger.info("Пользователь с ID {}: подходит под рекомендацию. Все условия выполнены", userId);
            return new RecommendationDTO(UUID.fromString("59efc529-2fff-41af-baff-90ccd7402925"),
                    "Top Saving", RuleSetText.TOP_SAVING_TEXT);
        } else {
            logger.info("Пользователь с ID: {} не подходит под рекомендацию. Не все условия выполнены", userId);
            return null;
        }
    }
}