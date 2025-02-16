package bank.recommendationservice.fintech.service;


import bank.recommendationservice.fintech.dto.RecommendationDTO;
import bank.recommendationservice.fintech.exception.*;
import bank.recommendationservice.fintech.interfaces.RecommendationRuleSet;
import bank.recommendationservice.fintech.model.DynamicRule;
import bank.recommendationservice.fintech.model.DynamicRuleQuery;
import bank.recommendationservice.fintech.other.ComparisonType;
import bank.recommendationservice.fintech.other.ProductType;
import bank.recommendationservice.fintech.other.QueryType;
import bank.recommendationservice.fintech.other.TransactionType;
import bank.recommendationservice.fintech.repository.DynamicRuleRepository;
import bank.recommendationservice.fintech.repository.RecommendationsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class RecommendationService {
    private final List<RecommendationRuleSet> ruleSets;

    private final DynamicRuleRepository dynamicRuleRepository;

    private final RecommendationsRepository recommendationsRepository;

    private final RuleStatsService ruleStatsService;

    private static final Logger logger = LoggerFactory.getLogger(RecommendationService.class);

    public RecommendationService(List<RecommendationRuleSet> ruleSets,
                                 DynamicRuleRepository dynamicRuleRepository,
                                 RecommendationsRepository recommendationsRepository,
                                 RuleStatsService ruleStatsService) {
        this.ruleSets = ruleSets;
        this.dynamicRuleRepository = dynamicRuleRepository;
        this.recommendationsRepository = recommendationsRepository;
        this.ruleStatsService = ruleStatsService;
    }


    /**
     * Извлекает список рекомендаций для заданного пользователя на основе динамических и стандартных правил.
     *
     * <p>Этот метод оценивает как динамические, так и стандартные правила рекомендаций для указанного пользователя.
     * Сначала он извлекает все динамические правила из репозитория и оценивает их для пользователя.
     * Если динамическое правило удовлетворено, создается соответствующий RecommendationDTO и добавляется в список.
     * Затем применяются предопределенные наборы правил для генерации стандартных рекомендаций.
     * Оба набора рекомендаций объединяются и возвращаются в качестве результата.
     *
     * @param userId уникальный идентификатор пользователя, для которого извлекаются рекомендации
     * @return список объектов RecommendationDTO, содержащих рекомендации для пользователя
     * @throws NullArgumentException если динамическое правило или userId равно null
     */
    public List<RecommendationDTO> getRecommendations(UUID userId) {
        List<DynamicRule> dynamicRules = dynamicRuleRepository.findAll();
        List<RecommendationDTO> dynamicRecommendations = new ArrayList<>();

        for (DynamicRule rule : dynamicRules) {
            if (evaluateDynamicRules(rule, userId)) {
                dynamicRecommendations.add(new RecommendationDTO(rule.getProductId(), rule.getProductName(), rule.getProductText()));
                ruleStatsService.increaseCounter(rule.getId());
            }
        }

        List<RecommendationDTO> standardRecommendations = ruleSets.stream()
                .map(p -> p.recommend(userId))
                .filter(Objects::nonNull)
                .toList();

        List<RecommendationDTO> allRecommendations = new ArrayList<>();
        allRecommendations.addAll(dynamicRecommendations);
        allRecommendations.addAll(standardRecommendations);

        return allRecommendations;
    }



    public List<RecommendationDTO> getRecommendations(String userName) {
        try {
            UUID userId = recommendationsRepository.getUserIdByUserName(userName);
            List<DynamicRule> dynamicRules = dynamicRuleRepository.findAll();
            List<RecommendationDTO> dynamicRecommendations = new ArrayList<>();

            for (DynamicRule rule : dynamicRules) {
                if (evaluateDynamicRules(rule, userId)) {
                    dynamicRecommendations.add(new RecommendationDTO(rule.getProductId(), rule.getProductName(), rule.getProductText()));
                }

            }

            List<RecommendationDTO> standardRecommendations = ruleSets.stream()
                    .map(p -> p.recommend(userId))
                    .filter(Objects::nonNull)
                    .toList();

            List<RecommendationDTO> allRecommendations = new ArrayList<>();
            allRecommendations.addAll(dynamicRecommendations);
            allRecommendations.addAll(standardRecommendations);

            return allRecommendations;
        } catch (EmptyResultDataAccessException e) {
            logger.error("Пользователь {} не найден", userName);
            throw new UserNotFoundException("Пользователь не найден");
        }
    }

    /**
     * Оценивает динамические правила для заданного пользователя.
     *
     * @param rule   - динамическое правило для оценки
     * @param userId - идентификатор пользователя
     * @return true, если правило оценивается как true, false в противном случае
     * @throws NullArgumentException если rule - null
     */
    public boolean evaluateDynamicRules(DynamicRule rule, UUID userId) {
        if (rule == null) {
            logger.warn("Динамическое правило null");
            throw new NullArgumentException("Динамическое правило не может быть null");
        }
        List<DynamicRuleQuery> queries = rule.getQueries();
        if (queries.isEmpty()) {
            logger.debug("Список запросов для динамического правила {} пустой", rule);
            return false;
        }
        for (DynamicRuleQuery query : queries) {
            try {
                QueryType queryType = QueryType.fromString(query.getQuery());
                if (!QueryType.isValidQuery(queryType.getQueryType())) {
                    logger.warn("Неизвестный тип запроса query: {}", query.getQuery());
                    throw new UnknownQueryTypeException("Неизвестный тип запроса query: " + queryType);
                }
                List<String> queryArguments = query.getArguments();
                return switch (queryType) {
                    case USER_OF -> processUserOfQuery(userId, queryArguments.get(0));
                    case ACTIVE_USER_OF ->
                            processActiveUserOfQuery(ProductType.fromString(queryArguments.get(0)), userId);
                    case TRANSACTION_SUM_COMPARE ->
                            processTransactionSumCompare(ProductType.fromString(queryArguments.get(0)),
                                    TransactionType.fromString(queryArguments.get(1)), userId, ComparisonType.fromString(queryArguments.get(2)), Integer.parseInt(queryArguments.get(3)));

                    case TRANSACTION_SUM_COMPARE_DEPOSIT_WITHDRAW ->
                            processTransactionSumCompareDepositWithdraw(ProductType.fromString(queryArguments.get(0)),
                                    userId, ComparisonType.fromString(queryArguments.get(1)));
                };

            } catch (UnknownQueryTypeException e) {
                logger.error("Не удалось обработать запрос: {}", e.getMessage(), e);
                return false;
            } catch (UnknownComparisonTypeException e) {
                logger.error("Не удалось обработать сравнение: {}", e.getMessage(), e);
                return false;
            } catch (UnknownProductTypeException e) {
                logger.error("Не удалось обработать тип продукта: {}", e.getMessage(), e);
                return false;
            } catch (UnknownTransactionTypeException e) {
                logger.error("Не удалось обработать тип транзакции: {}", e.getMessage(), e);
                return false;
            } catch (NumberFormatException e) {
                logger.error("Не удалось обработать число для сравнения: {}", e.getMessage(), e);
                return false;
            }
        }
        return false;
    }


    /**
     * Оценивает запрос "USER_OF".
     *
     * @param userId      ID пользователя для оценки
     * @param productType тип продукта для проверки
     * @return true, если пользователь использует продукт, false в противном случае
     */
    private boolean processUserOfQuery(UUID userId, String productType) {
        return recommendationsRepository.usesProductOfType(userId, productType);
    }


    /**
     * Определяет, является ли пользователь активным пользователем указанного типа продукта.
     *
     * <p>
     * Метод проверяет, является ли пользователь с указанным ID активным пользователем
     * продукта указанного типа, запрашивая RecommendationsRepository.
     * Пользователь считается активным, если он провел 5 и более транзакций по этому продукту.
     *
     * @param productType тип продукта, для которого проверяется активное использование
     * @param userId      уникальный идентификатор пользователя, для которого производится оценка
     * @return true, если пользователь является активным пользователем продукта, false в противном случае
     */
    private boolean processActiveUserOfQuery(ProductType productType, UUID userId) {
        return recommendationsRepository.isActiveUserOfProduct(productType, userId);
    }


    /**
     * Сравнивает сумму транзакций указанного типа для заданного типа продукта и ID пользователя
     * с заданной константой, используя заданный тип сравнения.
     *
     * @param productType     тип продукта, для которого должна сравниваться сумма
     * @param transactionType тип транзакций, для которых сравнивается сумма
     * @param userId          ID пользователя, для которого должна сравниваться сумма
     * @param comparisonType  тип сравнения, который необходимо выполнить
     * @param constant        константа, с которой сравнивается сумма
     * @return true, если сумма транзакций указанного типа для заданного типа продукта и
     * ID пользователя удовлетворяет сравнению с заданной константой, false в противном случае
     */
    private boolean processTransactionSumCompare(ProductType productType, TransactionType transactionType,
                                                 UUID userId, ComparisonType comparisonType, int constant) {
        return recommendationsRepository.compareTransactionSum(productType, transactionType, userId, comparisonType, constant);
    }


    /**
     * Определяет, является ли сумма транзакций типа {@code deposit} для
     * {@code productType} больше, меньше, равно, больше или равно, меньше или равно
     * сумме транзакций типа {@code withdraw} для того же {@code productType} в
     * зависимости от типа сравнения {@code comparisonType}.
     *
     * @param productType    тип продукта, для которого производится сравнение
     * @param userId         ID пользователя, для которого производится сравнение
     * @param comparisonType тип сравнения, который должен быть выполнен
     * @return true, если сравнение выполняется, false в противном случае
     */
    private boolean processTransactionSumCompareDepositWithdraw(ProductType productType, UUID userId, ComparisonType comparisonType) {
        return recommendationsRepository.compareDepositWithdrawSum(productType, userId, comparisonType);
    }
}