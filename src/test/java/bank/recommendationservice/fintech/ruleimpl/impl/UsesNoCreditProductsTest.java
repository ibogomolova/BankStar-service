package bank.recommendationservice.fintech.ruleimpl.impl;

import bank.recommendationservice.fintech.repository.RecommendationsRepository;
import bank.recommendationservice.fintech.other.ProductType;
import bank.recommendationservice.fintech.ruleimpl.UsesNoCreditProducts;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UsesNoCreditProductsTest {

    @Mock
    RecommendationsRepository recommendationsRepository;

    @InjectMocks
    UsesNoCreditProducts out;

    private final UUID userId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Возвращает true, когда у пользователя нет транзакций продукта CREDIT")
    void testEvaluatePositive_1() {
        when(recommendationsRepository.usesProductOfType(userId, ProductType.CREDIT.name())).thenReturn(false);

        //test
        boolean actual = out.evaluate(userId);

        //check
        assertTrue(actual);
        verify(recommendationsRepository).usesProductOfType(userId, ProductType.CREDIT.name());
    }

    @Test
    @DisplayName("Возвращает false, когда у пользователя есть транзакции продукта CREDIT")
    void testEvaluatePositive_2() {
        when(recommendationsRepository.usesProductOfType(userId, ProductType.CREDIT.name())).thenReturn(true);

        //test
        boolean actual = out.evaluate(userId);

        //check
        assertFalse(actual);
        verify(recommendationsRepository).usesProductOfType(userId, ProductType.CREDIT.name());
    }
}