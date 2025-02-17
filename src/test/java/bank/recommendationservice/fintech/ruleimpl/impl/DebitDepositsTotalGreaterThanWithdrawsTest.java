package bank.recommendationservice.fintech.ruleimpl.impl;

import bank.recommendationservice.fintech.exception.NoTransactionsFoundException;
import bank.recommendationservice.fintech.repository.RecommendationsRepository;
import bank.recommendationservice.fintech.ruleimpl.DebitDepositsTotalGreaterThanWithdraws;
import bank.recommendationservice.fintech.other.ProductType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.UUID;

import static bank.recommendationservice.fintech.testdata.RuleImplTestData.TOTAL_HIGH;
import static bank.recommendationservice.fintech.testdata.RuleImplTestData.TOTAL_LOW;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class DebitDepositsTotalGreaterThanWithdrawsTest {
    @Mock
    RecommendationsRepository recommendationsRepository;

    @InjectMocks
    DebitDepositsTotalGreaterThanWithdraws out;

    private final UUID userId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Возвращает true, когда сумма пополнений больше суммы трат")
    void testEvaluatePositive_1() {
        when(recommendationsRepository.getDepositsOfTypeTotal(userId, ProductType.DEBIT.name())).thenReturn(TOTAL_HIGH);
        when(recommendationsRepository.getWithdrawsOfTypeTotal(userId, ProductType.DEBIT.name())).thenReturn(TOTAL_LOW);

        //test
        boolean actual = out.evaluate(userId);

        //check
        assertTrue(actual);
        verify(recommendationsRepository).getDepositsOfTypeTotal(userId, ProductType.DEBIT.name());
        verify(recommendationsRepository).getWithdrawsOfTypeTotal(userId, ProductType.DEBIT.name());
    }

    @Test
    @DisplayName("Возращает false, когда сумма пополнений меньше суммы трат")
    void testEvaluatePositive_2() {
        when(recommendationsRepository.getDepositsOfTypeTotal(userId, ProductType.DEBIT.name())).thenReturn(TOTAL_LOW);
        when(recommendationsRepository.getWithdrawsOfTypeTotal(userId, ProductType.DEBIT.name())).thenReturn(TOTAL_HIGH);

        //test
        boolean actual = out.evaluate(userId);

        //check
        assertFalse(actual);
        verify(recommendationsRepository).getDepositsOfTypeTotal(userId, ProductType.DEBIT.name());
        verify(recommendationsRepository).getWithdrawsOfTypeTotal(userId, ProductType.DEBIT.name());
    }

}