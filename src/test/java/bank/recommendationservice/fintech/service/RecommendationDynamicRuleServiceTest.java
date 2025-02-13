package bank.recommendationservice.fintech.service;

import bank.recommendationservice.fintech.exception.RulesNotFoundException;
import bank.recommendationservice.fintech.model.DynamicRule;
import bank.recommendationservice.fintech.model.DynamicRuleQuery;
import bank.recommendationservice.fintech.repository.DynamicRuleQueryRepository;
import bank.recommendationservice.fintech.repository.DynamicRuleRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@RunWith(MockitoJUnitRunner.class)
public class RecommendationDynamicRuleServiceTest {
    @Mock
    private DynamicRuleQueryRepository dynamicRuleQueryRepository;
    @Mock
    private DynamicRuleRepository dynamicRuleRepository;
    @Mock
    private RuleStatsService ruleStatsService;
    @InjectMocks
    private RecommendationDynamicRuleService recommendationDynamicRuleService;
    private DynamicRule dynamicRule;


    /**
     * Инициализация объекта DynamicRule, используемого в тестах.
     * Этот метод аннотирован @Before и @Test, чтобы он выполнялся
     * перед каждым тестом, а также чтобы он был виден в отчетах тестов.
     */
    @Before
    @Test
    public void setup() {
        dynamicRule = new DynamicRule();

    }

    /**
     * Тест на добавление правила, у которого нет запросов.
     * <p>
     * 1. Добавляем правило.
     * 2. Проверяем, что правило было сохранено.
     * 3. Проверяем, что правило было возвращено.
     */
    @Test
    public void testAddRule_NoQueries() {
        // data
        when(dynamicRuleRepository.save(any(DynamicRule.class))).thenReturn(dynamicRule);
        // check
        DynamicRule result = recommendationDynamicRuleService.addRule(dynamicRule);
        // test
        assertEquals(dynamicRule, result);
        verify(dynamicRuleRepository, times(1)).save(dynamicRule);
        verify(ruleStatsService, times(1)).addRuleStats(dynamicRule.getId());
    }


    /**
     * Тест на добавление правила, у которого есть запросы, но без аргументов.
     * <p>
     * 1. Добавляем правило.
     * 2. Проверяем, что правило было сохранено.
     * 3. Проверяем, что правило было возвращено.
     * 4. Проверяем, что связь запроса с правилом была установлена.
     */
    @Test
    public void testAddRule_QueriesNoArguments() {
        // data
        List<DynamicRuleQuery> queries = new ArrayList<>();
        DynamicRuleQuery query = new DynamicRuleQuery();
        queries.add(query);
        dynamicRule.setQueries(queries);
        when(dynamicRuleRepository.save(any(DynamicRule.class))).thenReturn(dynamicRule);
        // test
        DynamicRule result = recommendationDynamicRuleService.addRule(dynamicRule);
        // check
        assertEquals(dynamicRule, result);
        verify(dynamicRuleRepository, times(1)).save(dynamicRule);
        assertEquals(dynamicRule, query.getDynamicRule());
    }


    /**
     * Тест на добавление правила, у которого есть запросы, и у запросов есть аргументы.
     * <p>
     * 1. Добавляем правило.
     * 2. Проверяем, что правило было сохранено.
     * 3. Проверяем, что правило было возвращено.
     * 4. Проверяем, что связь запроса с правилом была установлена.
     * 5. Проверяем, что связь аргументов с запросом была установлена.
     */
    @Test
    public void testAddRule_QueriesWithArguments() {
        // data
        List<DynamicRuleQuery> queries = new ArrayList<>();
        DynamicRuleQuery query = new DynamicRuleQuery();
        List<String> arguments = new ArrayList<>();
        String argument = "argument";

        arguments.add(argument);
        query.setArguments(arguments);
        queries.add(query);
        dynamicRule.setQueries(queries);
        when(dynamicRuleRepository.save(any(DynamicRule.class))).thenReturn(dynamicRule);
        // test
        DynamicRule result = recommendationDynamicRuleService.addRule(dynamicRule);
        // check
        assertEquals(dynamicRule, result);
        verify(dynamicRuleRepository, times(1)).save(dynamicRule);
        assertEquals(dynamicRule, query.getDynamicRule());
        assertNotNull(result.getQueries());
        assertNotNull(result.getQueries().get(0).getArguments());
        assertEquals(result.getQueries().get(0).getArguments(), arguments);

    }

    /**
     * Тест на добавление null правила.
     * <p>
     * Этот тест ожидает, что будет выброшено исключение NullPointerException
     * при попытке добавить null правило с использованием метода addRule.
     */
    @Test(expected = NullPointerException.class)
    public void testAddRule_NullRule() {
        // check & test
        recommendationDynamicRuleService.addRule(null);
    }


    /**
     * Тест на добавление правила с null-запросами.
     * <p>
     * 1. Устанавливаем null в качестве списка запросов для правила.
     * 2. Проверяем, что правило было сохранено.
     * 3. Проверяем, что правило было возвращено.
     */
    @Test
    public void testAddRule_NullQueries() {
        // data
        dynamicRule.setQueries(null);
        when(dynamicRuleRepository.save(any(DynamicRule.class))).thenReturn(dynamicRule);
        // test
        DynamicRule result = recommendationDynamicRuleService.addRule(dynamicRule);
        // check
        assertEquals(dynamicRule, result);
        verify(dynamicRuleRepository, times(1)).save(dynamicRule);
    }


    /**
     * Тест на добавление правила, у которого есть запросы, но у запросов
     * нет аргументов (аргументы null).
     * <p>
     * 1. Добавляем правило.
     * 2. Проверяем, что правило было сохранено.
     * 3. Проверяем, что правило было возвращено.
     * 4. Проверяем, что связь запроса с правилом была установлена.
     */
    @Test
    public void testAddRule_NullArguments() {
        // data
        List<DynamicRuleQuery> queries = new ArrayList<>();
        DynamicRuleQuery query = new DynamicRuleQuery();
        query.setArguments(null);
        queries.add(query);
        dynamicRule.setQueries(queries);
        when(dynamicRuleRepository.save(any(DynamicRule.class))).thenReturn(dynamicRule);
        // test
        DynamicRule result = recommendationDynamicRuleService.addRule(dynamicRule);
        // check
        assertEquals(dynamicRule, result);
        verify(dynamicRuleRepository, times(1)).save(dynamicRule);
        assertEquals(dynamicRule, query.getDynamicRule());
    }


    /**
     * Тест на успешное удаление динамического правила.
     *
     * <p>
     * 1. Создаем правило с уникальным идентификатором.
     * 2. Настраиваем репозиторий для возврата этого правила при запросе.
     * 3. Настраиваем репозиторий запросов для возврата пустого списка.
     * 4. Вызываем метод удаления правила.
     * 5. Проверяем, что возвращенное правило совпадает с удаленным.
     * 6. Проверяем, что метод удаления был вызван один раз для правила.
     * 7. Проверяем, что метод удаления всех связанных запросов был вызван.
     */
    @Test
    public void testDeleteDynamicRule_Success() {
        // data
        Long id = 1L;
        DynamicRule rule = new DynamicRule();
        rule.setId(id);
        when(dynamicRuleRepository.existsById(id)).thenReturn(true);
        when(dynamicRuleQueryRepository.findByDynamicRuleId(id)).thenReturn(Collections.emptyList());

        // test
        recommendationDynamicRuleService.deleteDynamicRule(id);

        // check
        verify(dynamicRuleRepository, times(1)).deleteById(id);
        verify(dynamicRuleQueryRepository, times(1)).deleteAll(anyList());
    }

    /**
     * Тест на удаление динамического правила, которое не существует.
     * <p>
     * 1. Устанавливаем идентификатор правила, которого нет в БД.
     * 2. Настраиваем репозиторий для возврата пустого Optional.
     * 3. Вызываем метод удаления.
     * 4. Ожидаем, что будет выброшено RulesNotFoundException.
     */
    @Test(expected = RulesNotFoundException.class)
    public void testDeleteDynamicRule_NotFound() {
        // data
        Long id = 1L;

        // check & test
        recommendationDynamicRuleService.deleteDynamicRule(id);
    }


    /**
     * Тест на удаление динамического правила с null-идентификатором.
     * <p>
     * 1. Вызываем метод удаления с null-идентификатором.
     * 2. Ожидаем, что будет выброшено RulesNotFoundException.
     */
    @Test(expected = RulesNotFoundException.class)
    public void testDeleteDynamicRule_NullId() {
        // check & test
        recommendationDynamicRuleService.deleteDynamicRule(null);
    }


    /**
     * Тест на удаление динамического правила с пустым идентификатором.
     * <p>
     * 1. Вызываем метод удаления с пустым идентификатором.
     * 2. Ожидаем, что будет выброшено RulesNotFoundException.
     */
    @Test(expected = RulesNotFoundException.class)
    public void testDeleteDynamicRule_EmptyId() {
        // check & test
        recommendationDynamicRuleService.deleteDynamicRule(0L);
    }


    /**
     * Тест на успешное получение всех динамических правил.
     *
     * <p>
     * 1. Подготавливаем список динамических правил.
     * 2. Настраиваем репозиторий для возврата этого списка при запросе.
     * 3. Вызываем метод сервиса, чтобы получить все динамические правила.
     * 4. Проверяем, что возвращенный список соответствует подготовленному списку.
     */
    @Test
    public void testGetAllDynamicRules_Success() {
        // data
        List<DynamicRule> rules = new ArrayList<>();
        rules.add(new DynamicRule());
        rules.add(new DynamicRule());
        when(dynamicRuleRepository.findAll()).thenReturn(rules);

        // test
        List<DynamicRule> result = recommendationDynamicRuleService.getAllDynamicRules();

        // check
        assertEquals(rules, result);
    }


    /**
     * Тест для проверки поведения при получении всех динамических правил, когда репозиторий пуст.
     *
     * <p>
     * 1. Подготовить пустой список динамических правил.
     * 2. Настроить репозиторий для возврата этого пустого списка при запросе.
     * 3. Вызвать метод сервиса для получения всех динамических правил.
     * 4. Убедиться, что возвращенный список пустой.
     */
    @Test
    public void testGetAllDynamicRules_EmptyList() {
        // data
        List<DynamicRule> rules = new ArrayList<>();
        when(dynamicRuleRepository.findAll()).thenReturn(rules);

        // test
        List<DynamicRule> result = recommendationDynamicRuleService.getAllDynamicRules();

        // check
        assertTrue(result.isEmpty());
    }


    /**
     * Тест на получение всех динамических правил, когда репозиторий
     * возвращает null.
     * <p>
     * 1. Подготавливаем null, который будет возвращен репозиторием.
     * 2. Вызываем метод сервиса.
     * 3. Проверяем, что возвращенный список null.
     */
    @Test
    public void testGetAllDynamicRules_NullList() {
        // data
        when(dynamicRuleRepository.findAll()).thenReturn(Collections.emptyList());

        // test
        List<DynamicRule> result = recommendationDynamicRuleService.getAllDynamicRules();

        // check
        assertTrue(result.isEmpty());
    }
}

