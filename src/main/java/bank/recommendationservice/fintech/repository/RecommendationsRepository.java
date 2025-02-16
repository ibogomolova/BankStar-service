package bank.recommendationservice.fintech.repository;

import bank.recommendationservice.fintech.exception.NullArgumentException;
import bank.recommendationservice.fintech.other.ComparisonType;
import bank.recommendationservice.fintech.other.ProductType;
import bank.recommendationservice.fintech.other.TransactionType;
import com.github.benmanes.caffeine.cache.Cache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public class RecommendationsRepository {
    private static final Logger logger = LoggerFactory.getLogger(RecommendationsRepository.class);
    private final JdbcTemplate jdbcTemplate;
    private final Cache<String, Boolean> productTypeCache;
    private final Cache<String, Integer> transactionSumCache;
    private final Cache<String, Integer> transactionCountCache;

    public RecommendationsRepository(
            @Qualifier("recommendationsJdbcTemplate") JdbcTemplate jdbcTemplate,
            Cache<String, Boolean> productTypeCache,
            Cache<String, Integer> transactionSumCache,
            Cache<String, Integer> transactionCountCache
    ) {
        this.jdbcTemplate = jdbcTemplate;
        this.productTypeCache = productTypeCache;
        this.transactionSumCache = transactionSumCache;
        this.transactionCountCache = transactionCountCache;
    }

    /**
     * Проверка, есть ли в базе данных хотя бы одна транзакция типа productType у пользователя с id userId
     *
     * @param userId      - ID пользователя
     * @param productType - тип продукта
     * @return {@code true} если есть; {@code false}, если нет
     */
    public boolean usesProductOfType(UUID userId, String productType) {
        logger.info("Вызван метод usesProductOfType() с параметрами userId={}, productType={}", userId, productType);
        if (userId == null) {
            throw new NullArgumentException("userId не должен быть пустым");
        }
        if (productType == null) {
            throw new NullArgumentException("productType не должен быть пустым");
        }

        String cacheKey = "product_" + userId + "_" + productType;
        return productTypeCache.get(cacheKey, key -> {
            Integer count = jdbcTemplate.queryForObject(
                    "SELECT COUNT(t.amount) FROM transactions t JOIN products p ON t.PRODUCT_ID = p.ID WHERE t.USER_ID = ? AND p.TYPE = ?",
                    Integer.class,
                    userId,
                    productType
            );
            return count != null && count > 0;
        });
    }

    /**
     * Возвращает сумму всех пополнений (депозитов) по продукту типа productType у пользователя с id userId
     *
     * @param userId      - id пользователя
     * @param productType - тип продукта
     * @return int со значением суммы всех пополнений
     */
    public int getDepositsOfTypeTotal(UUID userId, String productType) {
        logger.info("Вызван метод getDepositsOfTypeTotal() с параметрами userId={}, productType={}", userId, productType);
        if (userId == null) {
            throw new NullArgumentException("userId не должен быть пустым");
        }
        if (productType == null) {
            throw new NullArgumentException("productType не должен быть пустым");
        }

        String cacheKey = "deposit_" + userId + "_" + productType;
        return transactionSumCache.get(cacheKey, key -> {
            Integer total = jdbcTemplate.queryForObject("SELECT SUM(t.amount) " +
                            "FROM transactions t JOIN products p ON t.product_id = p.id " +
                            "WHERE t.user_id = ? AND p.type = ? AND t.type = 'DEPOSIT';",
                    Integer.class,
                    userId,
                    productType);

            return total != null ? total.intValue() : 0;
        });
    }

    /**
     * Возвращает сумму всех трат (withdraw) по продукту типа productType у пользователя с id userId
     *
     * @param userId      - id пользователя
     * @param productType - тип продукта
     * @return int со значением суммы всех трат
     */
    public int getWithdrawsOfTypeTotal(UUID userId, String productType) {
        logger.info("Вызван метод getWithdrawsOfTypeTotal() с параметрами userId={}, productType={}", userId, productType);
        if (userId == null) {
            throw new NullArgumentException("userId не должен быть пустым");
        }
        if (productType == null) {
            throw new NullArgumentException("productType не должен быть пустым");
        }
        Integer total = jdbcTemplate.queryForObject("SELECT SUM(t.amount) " +
                        "FROM transactions t JOIN products p ON t.PRODUCT_ID = p.id " +
                        "WHERE t.USER_ID = ? AND p.TYPE = ? AND t.TYPE = 'WITHDRAW'",
                Integer.class,
                userId,
                productType);

        return total != null ? total : 0;
    }

    /**
     * Возвращает true, если пользователь с id {@code userId} активно использует продукт типа {@code productType},
     * т.е. имеет 5 и более транзакций по этому продукту.
     *
     * @param productType - тип продукта
     * @param userId      - id пользователя
     * @return {@code true}, если пользователь активно использует продукт; {@code false} - иначе
     */
    public boolean isActiveUserOfProduct(ProductType productType, UUID userId) {
        if (productType == null) {
            throw new NullArgumentException("productType не должен быть пустым");
        }
        if (userId == null) {
            throw new NullArgumentException("userId не должен быть пустым");
        }

        String query = "SELECT COUNT(t.amount) FROM transactions t JOIN products p ON t.PRODUCT_ID = p.ID WHERE p.TYPE = ? AND t.USER_ID = ?";

        Object[] params = new Object[]{productType.name(), userId};

        String cacheKey = "count_" + userId + "_" + productType.name();
        Integer count = transactionCountCache.get(cacheKey, key ->
                jdbcTemplate.queryForObject(query, Integer.class, params)
        );

        return count != null && count >= 5;
    }


    /**
     * Сравнивает сумму транзакций по продукту с заданной константой на основе типа сравнения.
     *
     * @param productType     - тип продукта, по которому фильтруются транзакции
     * @param transactionType - тип транзакции (например, DEPOSIT или WITHDRAW)
     * @param userId          - ID пользователя, для которого производится проверка
     * @param comparisonType  - тип сравнения (например, GREATER_THAN, LESS_THAN)
     * @param constant        - константа, с которой сравнивается сумма транзакций
     * @return {@code true} если сумма транзакций соответствует условию сравнения с константой; {@code false} в противном случае
     * @throws IllegalArgumentException если передан недопустимый тип сравнения
     */
    public boolean compareTransactionSum(ProductType productType, TransactionType transactionType, UUID userId, ComparisonType comparisonType, int constant) {
        String query = "SELECT SUM(amount) FROM transactions WHERE product_type = ? AND transaction_type = ? AND user_id = ?";
        Object[] params = new Object[]{productType.name(), transactionType.name(), userId};
        Integer sum = jdbcTemplate.queryForObject(query, Integer.class, params);
        if (sum == null) {
            return false;
        }
        return switch (comparisonType) {
            case GREATER_THAN -> sum > constant;
            case LESS_THAN -> sum < constant;
            case EQUALS -> sum.equals(constant);
            case GREATER_THAN_OR_EQUALS -> sum >= constant;
            case LESS_THAN_OR_EQUALS -> sum <= constant;
            default -> throw new IllegalArgumentException("Недопустимый тип сравнения: " + comparisonType);
        };
    }

    /**
     * Сравнивает сумму всех транзакций типа DEPOSIT с суммой всех транзакций типа WITHDRAW по продукту X.
     *
     * @param productType    тип продукта (DEBIT, CREDIT, INVEST, SAVING)
     * @param comparisonType оператор сравнения (>, <, =, >=, <=)
     * @return true, если сумма DEPOSIT больше/меньше/равна сумме WITHDRAW, false в противном случае
     */

    public boolean compareDepositWithdrawSum(ProductType productType, UUID userId, ComparisonType comparisonType) {
        String depositQuery = "SELECT SUM(amount) FROM transactions WHERE product_type = ? AND transaction_type = 'DEPOSIT' AND user_id = ?";
        String withdrawQuery = "SELECT SUM(amount) FROM transactions WHERE product_type = ? AND transaction_type = 'WITHDRAW' AND user_id = ?";
        Object[] params = new Object[]{productType.name(), userId};
        Integer depositSum = jdbcTemplate.queryForObject(depositQuery, Integer.class, params);
        Integer withdrawSum = jdbcTemplate.queryForObject(withdrawQuery, Integer.class, params);
        if (depositSum == null || withdrawSum == null) {
            return false;
        }
        return switch (comparisonType) {
            case GREATER_THAN -> depositSum > withdrawSum;
            case LESS_THAN -> depositSum < withdrawSum;
            case EQUALS -> depositSum.equals(withdrawSum);
            case GREATER_THAN_OR_EQUALS -> depositSum >= withdrawSum;
            case LESS_THAN_OR_EQUALS -> depositSum <= withdrawSum;
            default -> throw new IllegalArgumentException("Unknown comparison type: " + comparisonType);
        };
    }


    /**
     * Возвращает ID пользователя по имени пользователя.
     *
     * @param userName имя пользователя
     * @return ID пользователя
     * @throws NullPointerException если {@code userName} - {@code null}
     */
    public UUID getUserIdByUserName(String userName) {
        String sql = "SELECT id FROM users WHERE username = ?";
        return jdbcTemplate.queryForObject(sql, new Object[]{userName}, UUID.class);
    }


    /**
     * Возвращает полное имя пользователя по его имени пользователя.
     *
     * @param username имя пользователя
     * @return полное имя пользователя
     */
    public String getFullNameByUsername(String username) {
        String sql = "SELECT first_name, last_name FROM users WHERE username = ?";
        return jdbcTemplate.queryForObject(sql, new Object[]{username}, (rs, rowNum) -> {
            String firstName = rs.getString("first_name");
            String lastName = rs.getString("last_name");
            return firstName + " " + lastName;
        });
    }


    /**
     * Проверяет, существует ли пользователь с указанным именем.
     *
     * @param userName имя пользователя
     * @return true, если пользователь существует, false - в противном случае
     */
    public boolean validateUserName(String userName) {
        String sql = "SELECT COUNT(*) FROM users WHERE username = ?";
        Integer count = jdbcTemplate.queryForObject(sql, new Object[]{userName}, Integer.class);

        return count != null && count > 0;
    }
}

