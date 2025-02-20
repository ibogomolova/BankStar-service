package bank.recommendationservice.fintech.service;

import bank.recommendationservice.fintech.exception.*;
import bank.recommendationservice.fintech.model.DynamicRule;
import bank.recommendationservice.fintech.model.DynamicRuleQuery;
import bank.recommendationservice.fintech.other.ComparisonType;
import bank.recommendationservice.fintech.other.ProductType;
import bank.recommendationservice.fintech.other.QueryType;
import bank.recommendationservice.fintech.other.TransactionType;
import bank.recommendationservice.fintech.repository.DynamicRuleRepository;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Service
@Slf4j
public class RecommendationDynamicRuleService {
    private final DynamicRuleRepository dynamicRuleRepository;
    private final RuleStatsService ruleStatsService;

    private static final Logger logger = LoggerFactory.getLogger(RecommendationDynamicRuleService.class);

    public RecommendationDynamicRuleService(DynamicRuleRepository dynamicRuleRepository,
                                            RuleStatsService ruleStatsService) {
        this.dynamicRuleRepository = dynamicRuleRepository;
        this.ruleStatsService = ruleStatsService;
    }


    /**
     * Создает новое динамическое правило.
     * <p>
     * Данный метод добавляет новое динамическое правило в базу данных. Сначала он проверяет,
     * корректны ли запросы правила, а затем сохраняет правило и добавляет новую запись
     * в таблицу rule_stats.
     * <p>
     * Если какой-либо запрос имеет недопустимые аргументы, выбрасывается исключение
     * IllegalQueryArgumentsException.
     * <p>
     *
     * @param rule DynamicRule, которое нужно добавить
     * @return сохраненное DynamicRule
     * @throws IllegalQueryArgumentsException если какой-либо запрос имеет недопустимые аргументы
     */
    public DynamicRule addRule(DynamicRule rule) {
        logger.info("Проверка запросов правила {}", rule.toString());
        try {
            evaluateQueries(rule.getQueries()); // Throw exception if any query is invalid
        } catch (IllegalQueryArgumentsException e) {
            logger.error("Ошибка при проверке запросов для правила {}: {}", rule, e.getMessage());
            throw e;
        }
        logger.info("Добавление нового правила: {}", rule);
        if (rule.getQueries() != null) {
            rule.getQueries().forEach(query -> {
                query.setDynamicRule(rule);
            });
        }
        DynamicRule savedRule = dynamicRuleRepository.save(rule);
        ruleStatsService.addRuleStats(rule.getId());
        return savedRule;
    }


    /**
     * Удаляет динамическое правило из базы данных по идентификатору.
     * <p>
     * Метод сначала пытается найти правило по переданному идентификатору.
     * Если правило не найдено, выбрасывается исключение RulesNotFoundException.
     * Все связанные с правилом запросы также удаляются из базы данных.
     * <p>
     *
     * @param id идентификатор правила, которое необходимо удалить
     * @throws RulesNotFoundException если правило с указанным идентификатором не найдено
     */
    @Transactional
    public void deleteDynamicRule(Long id) {
        logger.info("Удаление правила с id: {}", id);
        if (!dynamicRuleRepository.existsById(id)) {
            logger.error("Правило с id: {} не найдено", id);
            throw new RulesNotFoundException("Не удалось удалить правило - правило не найдено ", id);
        }
        ruleStatsService.deleteRuleStats(id);
        dynamicRuleRepository.deleteById(id);
    }

    /**
     * Получает список всех существующих динамических правил.
     *
     * @return список динамических правил
     */
    public List<DynamicRule> getAllDynamicRules() {
        return Collections.unmodifiableList(dynamicRuleRepository.findAll());
    }


    /**
     * Проверяет, все ли запросы в данной коллекции являются допустимыми и имеют корректные аргументы.
     * <p>
     * Проверяет, все ли запросы имеют допустимый тип запроса и корректные аргументы в соответствии с типом запроса.
     * Если какой-либо запрос является недопустимым, выбрасывается исключение.
     * <p>
     *
     * @param queries запросы для проверки
     * @throws UnknownQueryTypeException      если какой-либо запрос имеет неизвестный тип
     * @throws IllegalQueryArgumentsException если какой-либо запрос имеет недопустимые аргументы
     */
    private void evaluateQueries(Collection<DynamicRuleQuery> queries) {
        for (DynamicRuleQuery query : queries) {
            if (!QueryType.isValidQuery(query.getQuery())) {
                logger.error("Некорректный формат запроса: {}", query.getQuery());
                throw new UnknownQueryTypeException("Некорректный формат запроса: " + query.getQuery());
            }

            QueryType type = QueryType.fromString(query.getQuery());
            switch (type) {
                case USER_OF -> {
                    try {
                        handleUserOfQuery(query.getArguments());
                    } catch (IllegalQueryArgumentsException e) {
                        throw new IllegalQueryArgumentsException("Некорректный набор аргументов в запросе USER_OF: " + e.getMessage(), e);
                    }
                }
                case ACTIVE_USER_OF -> {
                    try {
                        handleActiveUserOfQuery(query.getArguments());
                    } catch (IllegalQueryArgumentsException e) {
                        throw new IllegalQueryArgumentsException("Некорректный набор аргументов в запросе ACTIVE_USER_OF: " + e.getMessage(), e);
                    }
                }
                case TRANSACTION_SUM_COMPARE -> {
                    try {
                        handleTransactionSumCompareQuery(query.getArguments());
                    } catch (IllegalQueryArgumentsException e) {
                        throw new IllegalQueryArgumentsException("Некорректный набор аргументов в запросе TRANSACTION_SUM_COMPARE: " + e.getMessage(), e);
                    }
                }
                case TRANSACTION_SUM_COMPARE_DEPOSIT_WITHDRAW -> {
                    try {
                        handleTransactionSumCompareDepositWithdrawQuery(query.getArguments());
                    } catch (IllegalQueryArgumentsException e) {
                        throw new IllegalQueryArgumentsException("Некорректный набор аргументов в запросе TRANSACTION_SUM_COMPARE_DEPOSIT_WITHDRAW: " + e.getMessage(), e);
                    }
                }
            }
        }
        logger.info("All queries passed validation");
    }


    /**
     * Проверяет, является ли заданный запрос корректным запросом USER_OF.
     * <p>
     * Проверяет, имеет ли запрос ровно один аргумент, и является ли аргумент корректным типом продукта.
     * Если запрос не корректен, бросается исключение.
     * <p>
     *
     * @param arguments аргументы запроса, которые нужно проверить
     * @throws IllegalQueryArgumentsException если запрос не корректен
     */
    private void handleUserOfQuery(List<String> arguments) {
        if (arguments.size() != 1) {
            logger.error("USER_OF содержит некорректное количество аргументов: {}", arguments.size());
            throw new IllegalQueryArgumentsException("USER_OF содержит некорректное количество аргументов");
        }

        try {
            ProductType.fromString(arguments.get(0));
        } catch (UnknownQueryTypeException e) {
            logger.error("Некорректный тип продукта в запросе USER_OF: {}", arguments.get(0));
            throw new IllegalQueryArgumentsException("Некорректный тип продукта в запросе USER_OF: " + arguments.get(0), e);
        }
    }


    /**
     * Проверяет, является ли заданный запрос корректным запросом ACTIVE_USER_OF.
     * <p>
     * Проверяет, имеет ли запрос ровно один аргумент, и является ли аргумент корректным типом продукта.
     * Если запрос не корректен, бросается исключение.
     * <p>
     *
     * @param arguments аргументы запроса, которые нужно проверить
     * @throws IllegalQueryArgumentsException если запрос не корректен
     */
    private void handleActiveUserOfQuery(List<String> arguments) {
        if (arguments.size() != 1) {
            logger.error("ACTIVE_USER_OF содержит некорректное количество аргументов: {}", arguments.size());
            throw new IllegalQueryArgumentsException("ACTIVE_USER_OF содержит некорректное количество аргументов");
        }

        try {
            ProductType.fromString(arguments.get(0));
        } catch (IllegalQueryArgumentsException e) {
            logger.error("Некорректный тип продукта в запросе ACTIVE_USER_OF: {}", arguments.get(0));
            throw new IllegalQueryArgumentsException("Некорректный тип продукта в запросе ACTIVE_USER_OF: " + arguments.get(0), e);
        }
    }


    /**
     * Проверяет корректность запроса TRANSACTION_SUM_COMPARE, проверяя количество аргументов
     * и тип каждого аргумента. Ожидаемые аргументы: тип продукта, тип транзакции,
     * тип сравнения, целое число.
     *
     * @param arguments список аргументов запроса, которые необходимо проверить
     * @throws IllegalQueryArgumentsException если количество аргументов неправильное
     *                                        или если какой-либо аргумент имеет неправильный тип
     */
    private void handleTransactionSumCompareQuery(List<String> arguments) {
        if (arguments.size() != 4) {
            logger.error("TRANSACTION_SUM_COMPARE содержит некорректное количество аргументов: {}", arguments.size());
            throw new IllegalQueryArgumentsException("TRANSACTION_SUM_COMPARE содержит некорректное количество аргументов");
        }

        try {
            ProductType.fromString(arguments.get(0));
            TransactionType.fromString(arguments.get(1));
            ComparisonType.fromString(arguments.get(2));
            Integer.parseInt(arguments.get(3));
        } catch (UnknownProductTypeException | UnknownTransactionTypeException | UnknownComparisonTypeException e) {
            logger.error("Некорректный аргумент в запросе TRANSACTION_SUM_COMPARE: {}", e.getMessage());
            throw new IllegalQueryArgumentsException("Некоррекнтый аргумент в TRANSACTION_SUM_COMPARE: " + e.getMessage(), e);
        } catch (NumberFormatException e) {
            logger.error("Не удалось прочитать число из запроса TRANSACTION_SUM_COMPARE");
            throw new IllegalQueryArgumentsException("Не удалось прочитать число", e.getCause());
        }
    }


    /**
     * Проверяет корректность запроса TRANSACTION_SUM_COMPARE_DEPOSIT_WITHDRAW, проверяя количество аргументов
     * и тип каждого аргумента. Ожидаемые аргументы: тип продукта, тип сравнения.
     *
     * @param arguments список аргументов запроса, которые необходимо проверить
     * @throws IllegalQueryArgumentsException если количество аргументов неправильное
     *                                        или если какой-либо аргумент имеет неправильный тип
     */
    private void handleTransactionSumCompareDepositWithdrawQuery(List<String> arguments) {
        if (arguments.size() != 2) {
            logger.error("TRANSACTION_SUM_COMPARE_DEPOSIT_WITHDRAW содержит некорректное количество аргументов: {}", arguments.size());
            throw new IllegalQueryArgumentsException("TRANSACTION_SUM_COMPARE_DEPOSIT_WITHDRAW содержит некорректное количество аргументов");
        }

        try {
            ProductType.fromString(arguments.get(0));
            ComparisonType.fromString(arguments.get(1));
        } catch (UnknownProductTypeException | UnknownComparisonTypeException e) {
            logger.error("Некорректный аргумент в запросе TRANSACTION_SUM_COMPARE_DEPOSIT_WITHDRAW: {}", e.getMessage());
            throw new IllegalQueryArgumentsException("Некорректный аргумент в запросе TRANSACTION_SUM_COMPARE_DEPOSIT_WITHDRAW: " + e.getMessage(), e);
        }
    }
}

