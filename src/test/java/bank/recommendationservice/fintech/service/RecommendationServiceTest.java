package bank.recommendationservice.fintech.service;

import bank.recommendationservice.fintech.dto.RecommendationDTO;
import bank.recommendationservice.fintech.exception.NullArgumentException;
import bank.recommendationservice.fintech.exception.UserNotFoundException;
import bank.recommendationservice.fintech.interfaces.RecommendationRuleSet;
import bank.recommendationservice.fintech.model.DynamicRule;
import bank.recommendationservice.fintech.model.DynamicRuleQuery;
import bank.recommendationservice.fintech.other.ComparisonType;
import bank.recommendationservice.fintech.other.ProductType;
import bank.recommendationservice.fintech.other.TransactionType;
import bank.recommendationservice.fintech.repository.DynamicRuleRepository;
import bank.recommendationservice.fintech.repository.RecommendationsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.EmptyResultDataAccessException;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class RecommendationServiceTest {

    @InjectMocks
    private RecommendationService recommendationService;

    @Mock
    private List<RecommendationRuleSet> ruleSets;

    @Mock
    private DynamicRuleRepository dynamicRuleRepository;

    @Mock
    private RecommendationsRepository recommendationsRepository;

    @Mock
    private RuleStatsService ruleStatsService;

    private UUID userId;
    private String userName;
    private UUID productId;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userId = UUID.randomUUID();
        userName = "testUser";
        productId = UUID.randomUUID();
    }

    @Test
    void testGetRecommendationsByUserId_Positive() {
        // data
        DynamicRule dynamicRule = new DynamicRule();
        dynamicRule.setProductId(productId); // Устанавливаем UUID
        dynamicRule.setProductName("Product 1");
        dynamicRule.setProductText("Recommendation for Product 1");
        dynamicRule.setQueries(List.of(new DynamicRuleQuery("USER_OF", List.of("product1"))));

        when(dynamicRuleRepository.findAll()).thenReturn(List.of(dynamicRule));
        when(recommendationsRepository.usesProductOfType(userId, productId.toString())).thenReturn(true);

        // Настраиваем mock для ruleSets
        RecommendationRuleSet mockRuleSet = mock(RecommendationRuleSet.class);
        when(mockRuleSet.recommend(userId)).thenReturn(new RecommendationDTO(productId, "Product 1", "Recommendation for Product 1"));
        when(ruleSets.stream()).thenReturn(Stream.of(mockRuleSet));

        // test
        List<RecommendationDTO> recommendations = recommendationService.getRecommendations(userId);

        // check
        assertEquals(1, recommendations.size());
        assertEquals(productId, recommendations.get(0).getId());
    }


    @Test
    void testGetRecommendationsByUserId_Negative() {
        // data
        when(dynamicRuleRepository.findAll()).thenReturn(new ArrayList<>());

        // test
        List<RecommendationDTO> recommendations = recommendationService.getRecommendations(userId);

        // check
        assertTrue(recommendations.isEmpty());
    }

    @Test
    void testGetRecommendationsByUserName_Positive() {
        // data
        when(recommendationsRepository.getUserIdByUserName(userName)).thenReturn(userId);
        DynamicRule dynamicRule = new DynamicRule();
        dynamicRule.setProductId(productId);
        dynamicRule.setProductName("Product 1");
        dynamicRule.setProductText("Recommendation for Product 1");
        dynamicRule.setQueries(List.of(new DynamicRuleQuery("USER_OF", List.of("product1"))));

        when(dynamicRuleRepository.findAll()).thenReturn(List.of(dynamicRule));
        when(recommendationsRepository.usesProductOfType(userId, "product1")).thenReturn(true);
        // Настраиваем mock для ruleSets
        RecommendationRuleSet mockRuleSet = mock(RecommendationRuleSet.class);
        when(mockRuleSet.recommend(userId)).thenReturn(new RecommendationDTO(productId, "Product 1", "Recommendation for Product 1"));
        when(ruleSets.stream()).thenReturn(Stream.of(mockRuleSet));

        // test
        List<RecommendationDTO> recommendations = recommendationService.getRecommendations(userName);

        // check
        assertEquals(2, recommendations.size());
        assertEquals(productId, recommendations.get(0).getId());
    }

    @Test
    void testGetRecommendationsByUserName_UserNotFound() {
        // dat
        when(recommendationsRepository.getUserIdByUserName(userName)).thenThrow(new EmptyResultDataAccessException(1));

        // test & check
        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> {
            recommendationService.getRecommendations(userName);
        });
        assertEquals("Пользователь не найден", exception.getMessage());
    }

    @Test
    void testEvaluateDynamicRules_NullRule() {
        // test & check
        NullArgumentException exception = assertThrows(NullArgumentException.class, () -> {
            recommendationService.evaluateDynamicRules(null, userId);
        });
        assertEquals("Динамическое правило не может быть null", exception.getMessage());
    }

    @Test
    void testEvaluateDynamicRules_EmptyQueries() {
        // data
        DynamicRule rule = new DynamicRule();
        rule.setQueries(new ArrayList<>());

        // test
        boolean result = recommendationService.evaluateDynamicRules(rule, userId);

        // check
        assertFalse(result);
    }

    @Test
    void testEvaluateDynamicRules_ValidUserOfQuery() {
        // data
        DynamicRule rule = new DynamicRule();
        rule.setQueries(List.of(new DynamicRuleQuery("USER_OF", List.of("product1"))));

        when(recommendationsRepository.usesProductOfType(userId, "product1")).thenReturn(true);

        // test
        boolean result = recommendationService.evaluateDynamicRules(rule, userId);

        // check
        assertTrue(result);
    }

    @Test
    void testEvaluateDynamicRules_InvalidQueryType() {
        // data
        DynamicRule rule = new DynamicRule();
        rule.setQueries(List.of(new DynamicRuleQuery("INVALID_QUERY", List.of("product1"))));

        // test
        boolean result = recommendationService.evaluateDynamicRules(rule, userId);

        // check
        assertFalse(result);
    }

    @Test
    void testEvaluateDynamicRules_ActiveUserOfQuery() {
        // data
        DynamicRule rule = new DynamicRule();
        rule.setQueries(List.of(new DynamicRuleQuery("ACTIVE_USER_OF", List.of("DEBIT"))));

        when(recommendationsRepository.isActiveUserOfProduct(ProductType.DEBIT, userId)).thenReturn(true);

        // test
        boolean result = recommendationService.evaluateDynamicRules(rule, userId);

        // check
        assertTrue(result);
    }

    @Test
    void testEvaluateDynamicRules_TransactionSumCompare() {
        // data
        DynamicRule rule = new DynamicRule();
        rule.setQueries(List.of(new DynamicRuleQuery("TRANSACTION_SUM_COMPARE", List.of("DEBIT", "DEPOSIT", "GREATER_THAN", "1000"))));

        when(recommendationsRepository.compareTransactionSum(ProductType.DEBIT, TransactionType.DEPOSIT, userId, ComparisonType.GREATER_THAN, 1000)).thenReturn(true);

        // test
        boolean result = recommendationService.evaluateDynamicRules(rule, userId);

        // check
        assertFalse(result);
    }

    @Test
    void testEvaluateDynamicRules_TransactionSumCompareDepositWithdraw() {
        // data
        DynamicRule rule = new DynamicRule();
        rule.setQueries(List.of(new DynamicRuleQuery("TRANSACTION_SUM_COMPARE_DEPOSIT_WITHDRAW", List.of("DEBIT", "GREATER_THAN"))));

        when(recommendationsRepository.compareDepositWithdrawSum(ProductType.DEBIT, userId, ComparisonType.GREATER_THAN)).thenReturn(true);

        // test
        boolean result = recommendationService.evaluateDynamicRules(rule, userId);

        // check
        assertFalse(result);
    }
}