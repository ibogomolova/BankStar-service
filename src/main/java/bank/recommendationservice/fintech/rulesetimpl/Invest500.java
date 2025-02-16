package bank.recommendationservice.fintech.rulesetimpl;

import bank.recommendationservice.fintech.dto.RecommendationDTO;
import bank.recommendationservice.fintech.exception.NullArgumentException;
import bank.recommendationservice.fintech.interfaces.RecommendationRuleSet;
import bank.recommendationservice.fintech.other.RuleSetText;
import bank.recommendationservice.fintech.ruleimpl.SavingDepositsTotalGreaterThan1_000;
import bank.recommendationservice.fintech.ruleimpl.UsesAtLeastOneDebitProduct;
import bank.recommendationservice.fintech.ruleimpl.UsesNoInvestProducts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class Invest500 implements RecommendationRuleSet {
    private static final Logger logger = LoggerFactory.getLogger(Invest500.class);

    private final UsesAtLeastOneDebitProduct usesAtLeastOneDebitProduct;
    private final UsesNoInvestProducts usesNoInvestProducts;
    private final SavingDepositsTotalGreaterThan1_000 savingDepositsTotalGreaterThan1_000;

    public Invest500(UsesAtLeastOneDebitProduct usesAtLeastOneDebitProduct,
                     UsesNoInvestProducts usesNoInvestProducts,
                     SavingDepositsTotalGreaterThan1_000 savingDepositsTotalGreaterThan1000) {
        if (usesAtLeastOneDebitProduct == null) {
            logger.error("Используемый продукт 'usesAtLeastOneDebitProduct' не должен быть null");
            throw new NullArgumentException("usesAtLeastOneDebitProduct не должен быть null");
        }
        if (usesNoInvestProducts == null) {
            logger.error("Используемый продукт 'usesNoInvestProducts' не должен быть null");
            throw new NullArgumentException("usesNoInvestProducts не должен быть null");
        }
        if (savingDepositsTotalGreaterThan1000 == null) {
            logger.error("Используемый продукт 'savingDepositsTotalGreaterThan1_000' не должен быть null");
            throw new NullArgumentException("savingDepositsTotalGreaterThan1_000 не должен быть null");
        }

        this.usesAtLeastOneDebitProduct = usesAtLeastOneDebitProduct;
        this.usesNoInvestProducts = usesNoInvestProducts;
        this.savingDepositsTotalGreaterThan1_000 = savingDepositsTotalGreaterThan1000;
    }

    /**
     * Оценивает, является ли пользователь с данным ID подходит под критерии рекомендации и если
     * да, то возвращает {@link RecommendationDTO} с деталями рекомендации.
     *
     * @param userId ID пользователя, которого нужно оценить
     * @return {@link RecommendationDTO} с деталями рекомендации, если пользователь подходит
     * под критерии, иначе null
     * @throws NullArgumentException если переданный userId является null
     */
    @Override
    public RecommendationDTO recommend(UUID userId) {
        logger.info("Вызван метод evaluate() из рулсета Invest500");
        if (userId == null) {
            logger.error("userId не должен быть null");
            throw new NullArgumentException("userId не должен быть null");
        }

        boolean hasDebitProduct = usesAtLeastOneDebitProduct.evaluate(userId);
        boolean hasNoInvestProducts = usesNoInvestProducts.evaluate(userId);
        boolean hasSufficientSavings = savingDepositsTotalGreaterThan1_000.evaluate(userId);

        if (hasDebitProduct && hasNoInvestProducts && hasSufficientSavings) {
            logger.info("Пользователь с ID {}: подходит под рекомендацию. Все условия выполнены", userId);
            return new RecommendationDTO(UUID.fromString("147f6a0f-3b91-413b-ab99-87f081d60d5a"),
                    "Invest 500", RuleSetText.INVEST_500_TEXT);
        } else {
            logger.info("Пользователь с ID: {} не подходит под рекомендацию. Не все условия выполнены", userId);
            return null;
        }
    }
}