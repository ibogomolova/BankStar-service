package bank.recommendationservice.fintech.service;

import bank.recommendationservice.fintech.dto.RuleStatsDTO;
import bank.recommendationservice.fintech.exception.NullArgumentException;
import bank.recommendationservice.fintech.exception.RulesNotFoundException;
import bank.recommendationservice.fintech.model.RuleStats;
import bank.recommendationservice.fintech.model.RuleStatsResponse;
import bank.recommendationservice.fintech.repository.DynamicRuleRepository;
import bank.recommendationservice.fintech.repository.RuleStatsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RuleStatsService {
    private final RuleStatsRepository ruleStatsRepository;
    private final DynamicRuleRepository dynamicRuleRepository;

    private static final Logger logger = LoggerFactory.getLogger(RuleStatsService.class);

    RuleStatsService(RuleStatsRepository ruleStatsRepository,
                     DynamicRuleRepository dynamicRuleRepository) {
        this.ruleStatsRepository = ruleStatsRepository;
        this.dynamicRuleRepository = dynamicRuleRepository;
    }

    /**
     * Добавляет объект {@code RuleStats} со счетчиком {@code counter} = 0 в репозиторий
     * Вызывается каждый раз, когда добавляется новое динамическое правила
     *
     * @param dynamicRuleId - id динамического правила
     * @throws NullArgumentException,  если id динамического правила null
     * @throws RulesNotFoundException, если динамического правила нет в репозитории
     */
    public void addRuleStats(Long dynamicRuleId) {
        logger.debug("Вызван метод addRuleStats(dynamicRuleId: {})", dynamicRuleId);
        if (dynamicRuleId == null) {
            logger.error("В метод addRuleStats() передант аргумент null");
            throw new NullArgumentException("Параметр dynamicRuleId не может быть null");
        }
        RuleStats ruleStats = new RuleStats();
        ruleStats.setCount(0);
        ruleStats.setDynamicRule(dynamicRuleRepository.findById(dynamicRuleId).orElseThrow(
                () -> new RulesNotFoundException("Динамическое правило не найдено ", dynamicRuleId)));
        logger.debug("Добавлен ruleStats для динамического правила с id: {}", dynamicRuleId);
        ruleStatsRepository.save(ruleStats);
    }

    /**
     * @return {@code List} из DTO всех объектов RuleStats
     */
    public RuleStatsResponse getAllRuleStats() {
        try {
            List<RuleStatsDTO> ruleStatsList = ruleStatsRepository.findAll().stream()
                    .map(ruleStats -> new RuleStatsDTO(ruleStats.getDynamicRule().getId(), ruleStats.getCount()))
                    .toList();
            return new RuleStatsResponse(ruleStatsList);
        } catch (Exception e) {
            logger.error("Ошибка при получении статистики правил: {}", e.getMessage());
            throw new RuntimeException("Ошибка при получении статистики правил", e);
        }
    }


    /**
     * Увеличивает счетчик срабатываний динамического правила
     *
     * @param dynamicRuleId id динамического правила
     * @throws NullArgumentException,  если id динамического правила null
     * @throws RulesNotFoundException, если динамического правила нет в репозитории
     */
    public void increaseCounter(Long dynamicRuleId) {
        logger.debug("Вызван метод increaseCounter()(dynamicRuleId: {})", dynamicRuleId);
        if (dynamicRuleId == null) {
            logger.error("В метод increaseCounter() передант аргумент null");
            throw new NullArgumentException("Параметр dynamicRuleId не может быть null");
        }
        RuleStats ruleStats = ruleStatsRepository.findByDynamicRuleId(dynamicRuleId);

        if (ruleStats != null) {
            ruleStats.setCount(ruleStats.getCount() + 1);
            logger.debug("Увеличен счетчик срабатываний для правила с id: {}", dynamicRuleId);
            ruleStatsRepository.save(ruleStats);
        } else {
            logger.error("Статистика срабатываний по правилу с id: {} не найдена ", dynamicRuleId);
            throw new RulesNotFoundException("Статистика срабатываний по правилу не найдена ", dynamicRuleId);
        }
    }

    public void deleteRuleStats(Long dynamicRuleId) {
        ruleStatsRepository.deleteByDynamicRuleId(dynamicRuleId);
    }
}