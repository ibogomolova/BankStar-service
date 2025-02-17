package bank.recommendationservice.fintech.repository;

import bank.recommendationservice.fintech.exception.NullArgumentException;
import bank.recommendationservice.fintech.other.ProductType;
import com.github.benmanes.caffeine.cache.Cache;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class RecommendationsRepositoryTest {
    @Mock
    private JdbcTemplate jdbcTemplate;

    @Mock
    private Cache<String, Boolean> productTypeCache;

    @InjectMocks
    private RecommendationsRepository recommendationsRepository;

    private final UUID userId = UUID.randomUUID();
    private final String productType = "testProductType";
    private final ProductType enumProductType = ProductType.valueOf("DEBIT");

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test   // Тестирование метода usesProductOfType, когда продукт существует
    public void testUsesProductOfType_WhenExists_ReturnsTrue() {
        // Настройка мока для кэша
        when(productTypeCache.get(any(String.class), any())).thenAnswer(invocation -> {
            // Вызываем лямбда-выражение, переданное в метод get
            return ((java.util.function.Function<String, Boolean>) invocation.getArgument(1))
                    .apply(invocation.getArgument(0));
        });

        // Настройка мока, чтобы он возвращал 1 при вызове queryForObject
        when(jdbcTemplate.queryForObject(any(String.class), eq(Integer.class), any(UUID.class), any(String.class)))
                .thenReturn(1);

        boolean result = recommendationsRepository.usesProductOfType(userId, productType);

        // Проверка, что результат соответствует ожидаемому значению true
        assertTrue(result);

        // Проверка, что метод queryForObject был вызван с правильными параметрами
        verify(jdbcTemplate).queryForObject(
                eq("SELECT COUNT(t.amount) FROM transactions t JOIN products p ON t.PRODUCT_ID = " +
                        "p.ID WHERE t.USER_ID = ? AND p.TYPE = ?"),
                eq(Integer.class),
                eq(userId),
                eq(productType)
        );
    }

    @Test  // Тестирование метода usesProductOfType, когда продукт не существует
    public void testUsesProductOfType_WhenNotExists_ReturnsFalse() {
        String cacheKey = "product_" + userId + "_" + productType;

        // Настройка мока для кэша
        when(productTypeCache.get(eq(cacheKey), any())).thenAnswer(invocation -> {
            // Вызываем лямбда-выражение, переданное в метод get
            return ((java.util.function.Function<String, Boolean>) invocation.getArgument(1)).apply(cacheKey);
        });

        // Настройка мока, чтобы он возвращал 0 при вызове queryForObject
        when(jdbcTemplate.queryForObject(any(String.class), eq(Integer.class), any(UUID.class), any(String.class)))
                .thenReturn(0); // Возвращаем 0, чтобы имитировать отсутствие продукта

        boolean result = recommendationsRepository.usesProductOfType(userId, productType);

        // Проверка, что результат соответствует ожидаемому значению false
        assertFalse(result);

        // Проверка, что метод queryForObject был вызван с правильными параметрами
        verify(jdbcTemplate).queryForObject(
                eq("SELECT COUNT(t.amount) FROM transactions t JOIN products p ON t.PRODUCT_ID = " +
                        "p.ID WHERE t.USER_ID = ? AND p.TYPE = ?"),
                eq(Integer.class),
                eq(userId),
                eq(productType)
        );
    }


    @Test  // Тестирование метода usesProductOfType с null userId
    public void testUsesProductOfType_WithNullUserId_ThrowsException() {
        NullArgumentException exception = assertThrows(NullArgumentException.class, () -> {
            recommendationsRepository.usesProductOfType(null, productType);
        });
        assertEquals("userId не должен быть пустым", exception.getMessage());
    }

    @Test  // Тестирование метода usesProductOfType с null productType
    public void testUsesProductOfType_WithNullProductType_ThrowsException() {
        NullArgumentException exception = assertThrows(NullArgumentException.class, () -> {
            recommendationsRepository.usesProductOfType(userId, null);
        });
        assertEquals("productType не должен быть пустым", exception.getMessage());
    }

    @Test  // Тестирование метода usesProductOfType с null userId и productType
    public void testUsesProductOfType_WithBothNull_ThrowsException() {
        NullArgumentException exception = assertThrows(NullArgumentException.class, () -> {
            recommendationsRepository.usesProductOfType(null, null);
        });
        assertEquals("userId не должен быть пустым", exception.getMessage());
    }

    @Test  // Тестирование метода getDepositsOfTypeTotal
    public void testGetDepositsOfTypeTotal() {
        Integer expectedSum = 100; // Ожидаемая сумма депозитов

        // Настройка мока для кэша
        when(productTypeCache.get(any(String.class), any())).thenAnswer(invocation -> {
            // Вызываем лямбда-выражение, переданное в метод get
            return ((java.util.function.Function<String, Integer>) invocation.getArgument(1))
                    .apply(invocation.getArgument(0));
        });

        // Настройка мока для jdbcTemplate
        when(jdbcTemplate.queryForObject(any(String.class), eq(Integer.class), any(UUID.class), any(String.class)))
                .thenReturn(expectedSum);

        Integer result = recommendationsRepository.getDepositsOfTypeTotal(userId, productType);

        // Проверка, что результат соответствует ожидаемой сумме
        assertEquals(expectedSum, result);

        // Проверка, что метод queryForObject был вызван с правильными параметрами
        verify(jdbcTemplate).queryForObject(
                eq("SELECT SUM(t.amount) FROM transactions t JOIN products p ON t.product_id = p.id " +
                        "WHERE t.user_id = ? AND p.type = ? AND t.type = 'DEPOSIT';"),
                eq(Integer.class),
                eq(userId),
                eq(productType)
        );
    }

    @Test  // Тестирование метода getDepositsOfTypeTotal с null userId
    public void testGetDepositsOfTypeTotal_WithNullUserId_ThrowsException() {
        NullArgumentException exception = assertThrows(NullArgumentException.class, () -> {
            recommendationsRepository.getDepositsOfTypeTotal(null, productType);
        });
        assertEquals("userId не должен быть пустым", exception.getMessage());
    }

    @Test  // Тестирование метода getDepositsOfTypeTotal с null productType
    public void testGetDepositsOfTypeTotal_WithNullProductType_ThrowsException() {
        NullArgumentException exception = assertThrows(NullArgumentException.class, () -> {
            recommendationsRepository.getDepositsOfTypeTotal(userId, null);
        });
        assertEquals("productType не должен быть пустым", exception.getMessage());
    }

    @Test  // Тестирование метода getDepositsOfTypeTotal с null userId и productType
    public void testGetDepositsOfTypeTotal_WithBothNull_ThrowsException() {
        NullArgumentException exception = assertThrows(NullArgumentException.class, () -> {
            recommendationsRepository.getDepositsOfTypeTotal(null, null);
        });
        assertEquals("userId не должен быть пустым", exception.getMessage());
    }


    @Test  // Тестирование метода getWithdrawsOfTypeTotal
    public void testGetWithdrawsOfTypeTotal() {
        Integer expectedSum = 50; // Ожидаемая сумма выводов
        when(jdbcTemplate.queryForObject(any(String.class), any(Class.class), any(UUID.class), any(String.class)))
                .thenReturn(expectedSum);

        Integer result = recommendationsRepository.getWithdrawsOfTypeTotal(userId, productType);
        // Проверка, что результат соответствует ожидаемой сумме
        assertEquals(expectedSum, result);
        // Проверка, что метод queryForObject был вызван с правильными параметрами
        verify(jdbcTemplate).queryForObject(any(String.class), eq(Integer.class), eq(userId), eq(productType));
    }

    @Test  // Тестирование метода getWithdrawsOfTypeTotal с null userId
    public void testGetWithdrawsOfTypeTotal_WithNullUserId_ThrowsException() {
        NullArgumentException exception = assertThrows(NullArgumentException.class, () -> {
            recommendationsRepository.getWithdrawsOfTypeTotal(null, productType);
        });
        assertEquals("userId не должен быть пустым", exception.getMessage());
    }

    @Test  // Тестирование метода getWithdrawsOfTypeTotal с null productType
    public void testGetWithdrawsOfTypeTotal_WithNullProductType_ThrowsException() {
        NullArgumentException exception = assertThrows(NullArgumentException.class, () -> {
            recommendationsRepository.getWithdrawsOfTypeTotal(userId, null);
        });
        assertEquals("productType не должен быть пустым", exception.getMessage());
    }

    @Test  // Тестирование метода getWithdrawsOfTypeTotal с null userId и productType
    public void testGetWithdrawsOfTypeTotal_WithBothNull_ThrowsException() {
        NullArgumentException exception = assertThrows(NullArgumentException.class, () -> {
            recommendationsRepository.getWithdrawsOfTypeTotal(null, null);
        });
        assertEquals("userId не должен быть пустым", exception.getMessage());
    }

}