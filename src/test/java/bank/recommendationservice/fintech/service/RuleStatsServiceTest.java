package bank.recommendationservice.fintech.service;

import bank.recommendationservice.fintech.dto.RuleStatsDTO;
import bank.recommendationservice.fintech.exception.NullArgumentException;
import bank.recommendationservice.fintech.exception.RulesNotFoundException;
import bank.recommendationservice.fintech.model.DynamicRule;
import bank.recommendationservice.fintech.model.DynamicRuleQuery;
import bank.recommendationservice.fintech.model.RuleStats;
import bank.recommendationservice.fintech.model.RuleStatsResponse;
import bank.recommendationservice.fintech.repository.DynamicRuleRepository;
import bank.recommendationservice.fintech.repository.RuleStatsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

class RuleStatsServiceTest {
    @Mock
    private DynamicRuleRepository dynamicRuleRepository;

    @Mock
    private RuleStatsRepository ruleStatsRepository;

    @InjectMocks
    private RuleStatsService ruleStatsService;

    Long ruleId;
    String productName;
    String productText;

    @BeforeEach
    void setUp() {
        ruleId = 1L;
        productName = "Product Name";
        productText = "Product Text";
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Выбрасывает исключение при добавлении счетчика с null dynamicRuleId")
    void addRuleStats_throws_null() {
        assertThrows(NullArgumentException.class, () -> ruleStatsService.addRuleStats(null));
    }

    @Test
    @DisplayName("Выбрасывает исключение при добавлении счетчика к несуществующему правилу")
    void addRuleStats_throws_notFound() {
        when(dynamicRuleRepository.findById(anyLong())).thenReturn(Optional.empty());

        //test & check
        assertThrows(RulesNotFoundException.class, () -> ruleStatsService.addRuleStats(1L));
        verify(dynamicRuleRepository, times(1)).findById(ruleId);
    }

    @Test
    @DisplayName("Положительный тест на добавление счетчика к существующему правилу")
    void addRuleStats_positive() {
        DynamicRule rule = new DynamicRule();
        rule.setId(ruleId);
        rule.setProductName(productName);
        rule.setProductText(productText);
        rule.setQueries(List.of(new DynamicRuleQuery("ACTIVE_USER_OF", List.of("CREDIT"))));
        when(dynamicRuleRepository.findById(ruleId)).thenReturn(Optional.of(rule));

        //test
        ruleStatsService.addRuleStats(ruleId);

        //check
        ArgumentCaptor<RuleStats> captor = ArgumentCaptor.forClass(RuleStats.class);
        verify(ruleStatsRepository, times(1)).save(captor.capture());
        RuleStats savedStats = captor.getValue();
        assertEquals(0, savedStats.getCount());
        assertEquals(rule, savedStats.getDynamicRule());
    }

    @Test
    @DisplayName("Позитивный тест на получение всех счетчиков")
    void getAllRuleStats_positive() {
        DynamicRule rule = new DynamicRule();
        rule.setId(ruleId);
        rule.setProductName(productName);
        rule.setProductText(productText);
        rule.setQueries(List.of(new DynamicRuleQuery("ACTIVE_USER_OF", List.of("CREDIT"))));

        RuleStats ruleStats = new RuleStats();
        ruleStats.setCount(3);
        ruleStats.setDynamicRule(rule);
        when(ruleStatsRepository.findAll()).thenReturn(List.of(ruleStats));

        //test
        RuleStatsResponse response = ruleStatsService.getAllRuleStats();
        List<RuleStatsDTO> dtos = response.getStats();

        //check
        assertNotNull(dtos);
        assertEquals(1, dtos.size());
        RuleStatsDTO dto = dtos.get(0);
        assertEquals(ruleId, dto.getRuleId());
        assertEquals(3, dto.getCount());
    }

    @Test
    @DisplayName("Выбрасывает исключение, если происходит ошибка чтения из репозитория")
    void getAllRuleStats_negative() {
        when(ruleStatsRepository.findAll()).thenThrow(new RuntimeException("Ошибка обработки статистики правил"));

        assertThrows(RuntimeException.class, () -> ruleStatsService.getAllRuleStats());
        verify(ruleStatsRepository).findAll();
    }

    @Test
    @DisplayName("Выбрасывает исключение при попытке увеличить счетчик правила с null dynamicRuleId")
    void increaseCounter_throws_1() {
        assertThrows(NullArgumentException.class, () -> ruleStatsService.increaseCounter(null));
    }

    @Test
    @DisplayName("Выбрасывает исключение при попытке увеличить счетчик у несуществующего правила")
    void increaseCounter_throws_2() {
        //test & check
        assertThrows(RulesNotFoundException.class, () -> ruleStatsService.increaseCounter(ruleId));
    }

    @Test
    @DisplayName("Увеличивает счетчик у существующего правила")
    void increaseCounter_success() {
        RuleStats ruleStats = new RuleStats();
        ruleStats.setCount(10);

        DynamicRule rule = new DynamicRule();
        rule.setId(ruleId);
        rule.setProductText(productText);
        rule.setProductName(productName);
        ruleStats.setDynamicRule(rule);

        //test
        when(ruleStatsRepository.findByDynamicRuleId(ruleId)).thenReturn(ruleStats);
        ruleStatsService.increaseCounter(ruleId);

        //check
        ArgumentCaptor<RuleStats> captor = ArgumentCaptor.forClass(RuleStats.class);
        verify(ruleStatsRepository, times(1)).save(captor.capture());
        RuleStats updatedStats = captor.getValue();
        assertEquals(11, updatedStats.getCount());
        assertEquals(rule, updatedStats.getDynamicRule());
    }

    @Test
    void deleteRuleStats_success() {
        //test & check
        ruleStatsService.deleteRuleStats(ruleId);
        verify(ruleStatsRepository, times(1)).deleteByDynamicRuleId(ruleId);
    }
}