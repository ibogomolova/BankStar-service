package bank.recommendationservice.fintech.ruleimpl.impl;

import bank.recommendationservice.fintech.repository.RecommendationsRepository;
import bank.recommendationservice.fintech.ruleimpl.DebitOrSavingDepositsTotalGreaterThanOrEqualsTo50_000;
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

class DebitOrSavingDepositsTotalGreaterThanOrEqualsTo50_000Test {
    @Mock
    RecommendationsRepository recommendationsRepository;

    @InjectMocks
    DebitOrSavingDepositsTotalGreaterThanOrEqualsTo50_000 out;

    private final UUID userId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Возвращает true, когда сумма пополнений по DEBIT больше или равна 50 000, а SAVING - меньше 50 000")
    void testEvaluatePositive_1() {
        when(recommendationsRepository.getDepositsOfTypeTotal(userId, ProductType.DEBIT.name())).thenReturn(TOTAL_HIGH);
        when(recommendationsRepository.getDepositsOfTypeTotal(userId, ProductType.SAVING.name())).thenReturn(TOTAL_LOW);

        //test
        boolean actual = out.evaluate(userId);

        //check
        assertTrue(actual);
        verify(recommendationsRepository).getDepositsOfTypeTotal(userId, ProductType.DEBIT.name());
        verify(recommendationsRepository).getDepositsOfTypeTotal(userId, ProductType.SAVING.name());
    }


    @Test
    @DisplayName("Возвращает true, когда сумма пополнений по SAVING больше или равна 50 000, а DEBIT - меньше 50 000")
    void testEvaluatePositive_2() {
        when(recommendationsRepository.getDepositsOfTypeTotal(userId, ProductType.SAVING.name())).thenReturn(TOTAL_HIGH);
        when(recommendationsRepository.getDepositsOfTypeTotal(userId, ProductType.DEBIT.name())).thenReturn(TOTAL_LOW);

        //test
        boolean actual = out.evaluate(userId);

        //check
        assertTrue(actual);
        verify(recommendationsRepository).getDepositsOfTypeTotal(userId, ProductType.SAVING.name());
        verify(recommendationsRepository).getDepositsOfTypeTotal(userId, ProductType.DEBIT.name());
    }

    @Test
    @DisplayName("Возвращает false, когда сумма пополнений по SAVING И DEBIT меньше 50 000")
    void testEvaluatePositive_3() {
        when(recommendationsRepository.getDepositsOfTypeTotal(userId, ProductType.SAVING.name())).thenReturn(TOTAL_LOW);
        when(recommendationsRepository.getDepositsOfTypeTotal(userId, ProductType.DEBIT.name())).thenReturn(TOTAL_LOW);

        //test
        boolean actual = out.evaluate(userId);

        //check
        assertFalse(actual);
        verify(recommendationsRepository).getDepositsOfTypeTotal(userId, ProductType.SAVING.name());
        verify(recommendationsRepository).getDepositsOfTypeTotal(userId, ProductType.DEBIT.name());
    }

    @Test
    @DisplayName("Возвращает true, когда сумма пополнений по SAVING И DEBIT больше или равна 50 000")
    void testEvaluatePositive_4() {
        when(recommendationsRepository.getDepositsOfTypeTotal(userId, ProductType.SAVING.name())).thenReturn(TOTAL_HIGH);
        when(recommendationsRepository.getDepositsOfTypeTotal(userId, ProductType.DEBIT.name())).thenReturn(TOTAL_HIGH);

        //test
        boolean actual = out.evaluate(userId);

        //check
        assertTrue(actual);
        verify(recommendationsRepository).getDepositsOfTypeTotal(userId, ProductType.SAVING.name());
        verify(recommendationsRepository).getDepositsOfTypeTotal(userId, ProductType.DEBIT.name());
    }

}