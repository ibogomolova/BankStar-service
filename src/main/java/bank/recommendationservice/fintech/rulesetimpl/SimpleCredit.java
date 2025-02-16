package bank.recommendationservice.fintech.rulesetimpl;

import bank.recommendationservice.fintech.dto.RecommendationDTO;
import bank.recommendationservice.fintech.exception.NullArgumentException;
import bank.recommendationservice.fintech.interfaces.RecommendationRuleSet;
import bank.recommendationservice.fintech.other.text.RuleSetText;
import bank.recommendationservice.fintech.ruleimpl.DebitDepositsTotalGreaterThanWithdraws;
import bank.recommendationservice.fintech.ruleimpl.DebitWithdrawsTotalGreaterThan100_000;
import bank.recommendationservice.fintech.ruleimpl.UsesNoCreditProducts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class SimpleCredit implements RecommendationRuleSet {
    private static final Logger logger = LoggerFactory.getLogger(SimpleCredit.class);

    private final UsesNoCreditProducts usesNoCreditProducts;
    private final DebitDepositsTotalGreaterThanWithdraws debitDepositsTotalGreaterThanWithdraws;
    private final DebitWithdrawsTotalGreaterThan100_000 debitWithdrawsTotalGreaterThan100_000;

    public SimpleCredit(UsesNoCreditProducts usesNoCreditProducts,
                        DebitDepositsTotalGreaterThanWithdraws debitDepositsTotalGreaterThanWithdraws,
                        DebitWithdrawsTotalGreaterThan100_000 debitWithdrawsTotalGreaterThan100_000) {
        if (usesNoCreditProducts == null) {
            logger.error("Используемый продукт 'usesNoCreditProducts' не должен быть null");
            throw new NullArgumentException("usesNoCreditProducts не должен быть null");
        }
        if (debitDepositsTotalGreaterThanWithdraws == null) {
            logger.error("Используемый продукт 'debitDepositsTotalGreaterThanWithdraws' не должен быть null");
            throw new NullArgumentException("debitDepositsTotalGreaterThanWithdraws не должен быть null");
        }
        if (debitWithdrawsTotalGreaterThan100_000 == null) {
            logger.error("Используемый продукт 'debitWithdrawsTotalGreaterThan100_000' не должен быть null");
            throw new NullArgumentException("debitWithdrawsTotalGreaterThan100_000 не должен быть null");
        }

        this.usesNoCreditProducts = usesNoCreditProducts;
        this.debitDepositsTotalGreaterThanWithdraws = debitDepositsTotalGreaterThanWithdraws;
        this.debitWithdrawsTotalGreaterThan100_000 = debitWithdrawsTotalGreaterThan100_000;
    }

    /**
     * Вызывает метод evaluate() для каждого из своих полей (usesNoCreditProducts, debitDepositsTotalGreaterThanWithdraws, debitWithdrawsTotalGreaterThan100_000)
     * и если все методы evaluate() возвращают true, возвращает RecommendationDTO, иначе null
     * @param userId id пользователя, для которого будут вызваны методы evaluate()
     * @return RecommendationDTO, если пользователь подходит под рекомендацию, null - иначе
     */
    @Override
    public RecommendationDTO recommend(UUID userId) {
        logger.info("Вызван метод evaluate() из рулсета SimpleCredit");
        if (userId == null) {
            logger.error("userId не должен быть null");
            throw new NullArgumentException("userId не должен быть null");
        }

        boolean noCreditProducts = usesNoCreditProducts.evaluate(userId);
        boolean depositsGreaterThanWithdraws = debitDepositsTotalGreaterThanWithdraws.evaluate(userId);
        boolean withdrawalsGreaterThan100k = debitWithdrawsTotalGreaterThan100_000.evaluate(userId);

        if (noCreditProducts && depositsGreaterThanWithdraws && withdrawalsGreaterThan100k) {
            logger.info("Пользователь с ID {}: подходит под рекомендацию. Все условия выполнены", userId);
            return new RecommendationDTO(UUID.fromString("ab138afb-f3ba-4a93-b74f-0fcee86d447f"),
                    "Простой кредит", RuleSetText.SIMPLE_CREDIT_TEXT);
        } else {
            logger.info("Пользователь с ID: {} не подходит под рекомендацию. Не все условия выполнены", userId);
            return null;
        }
    }
}