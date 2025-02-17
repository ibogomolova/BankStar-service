package bank.recommendationservice.fintech.ruleimpl.impl;

import bank.recommendationservice.fintech.exception.NoTransactionsFoundException;
import bank.recommendationservice.fintech.repository.RecommendationsRepository;
import bank.recommendationservice.fintech.other.ProductType;
import bank.recommendationservice.fintech.ruleimpl.SavingDepositsTotalGreaterThan1_000;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.UUID;

import static bank.recommendationservice.fintech.testdata.RuleImplTestData.TOTAL_HIGH;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SavingDepositsTotalGreaterThan1_000Test {

    @Mock
    RecommendationsRepository recommendationsRepository;

    @InjectMocks
    SavingDepositsTotalGreaterThan1_000 out;
    private final UUID userId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Возвращает true, когда сумма пополнений по SAVING больше 1000")
    void testEvaluatePositive_1() {
        when(recommendationsRepository.getDepositsOfTypeTotal(userId, ProductType.SAVING.name())).thenReturn(TOTAL_HIGH);

        //test
        boolean actual = out.evaluate(userId);

        //check
        assertTrue(actual);
        verify(recommendationsRepository).getDepositsOfTypeTotal(userId, ProductType.SAVING.name());
    }

    @Test
    @DisplayName("Возвращает false, когда сумма пополнений по SAVING меньше 1000")
    void testEvaluatePositive_2() {
        when(recommendationsRepository.getDepositsOfTypeTotal(userId, ProductType.SAVING.name())).thenReturn(800);

        //test
        boolean actual = out.evaluate(userId);

        //check
        assertFalse(actual);
        verify(recommendationsRepository).getDepositsOfTypeTotal(userId, ProductType.SAVING.name());
    }

}