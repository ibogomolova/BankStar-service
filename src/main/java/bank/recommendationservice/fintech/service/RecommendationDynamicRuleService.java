package bank.recommendationservice.fintech.service;

import bank.recommendationservice.fintech.exception.RulesNotFoundException;
import bank.recommendationservice.fintech.model.DynamicRule;
import bank.recommendationservice.fintech.repository.DynamicRuleQueryRepository;
import bank.recommendationservice.fintech.repository.DynamicRuleRepository;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.Valid;
import java.util.Collections;
import java.util.List;

@Service
@Slf4j
public class RecommendationDynamicRuleService {
    private final DynamicRuleRepository dynamicRuleRepository;
    private final DynamicRuleQueryRepository dynamicRuleQueryRepository;
    private final RuleStatsService ruleStatsService;

    private static final Logger logger = LoggerFactory.getLogger(RecommendationDynamicRuleService.class);

    public RecommendationDynamicRuleService(DynamicRuleRepository dynamicRuleRepository,
                                            DynamicRuleQueryRepository dynamicRuleQueryRepository,
                                            RuleStatsService ruleStatsService) {
        this.dynamicRuleRepository = dynamicRuleRepository;
        this.dynamicRuleQueryRepository = dynamicRuleQueryRepository;
        this.ruleStatsService = ruleStatsService;
    }


    /**
     * Сохраняет новое правило в базе данных.
     * <p>
     * Перед сохранением правила, оно обеспечивает, что запросы правила и их аргументы должным образом связаны с
     * правилом.
     * <p>
     * Данный метод является транзакционным, что означает, что если возникнет любая ошибка во время операции
     * сохранения, операция будет отменена.
     *
     * @param rule правило, которое нужно сохранить
     * @return сохраненное правило
     * @throws RulesNotFoundException если правило не может быть сохранено
     */

    public DynamicRule addRule( DynamicRule rule) {
        logger.info("Добавление нового правила: {}", rule.toString());
        if (rule.getQueries() != null) {
            rule.getQueries().forEach(query -> {
                query.setDynamicRule(rule);
            });
        }
        DynamicRule savedRule = dynamicRuleRepository.save(rule);
        ruleStatsService.addRuleStats(rule.getId());
        return savedRule;
    }


    /**
     * Удаляет динамическое правило из базы данных по идентификатору.
     * <p>
     * Метод сначала пытается найти правило по переданному идентификатору.
     * Если правило не найдено, выбрасывается исключение RulesNotFoundException.
     * Все связанные с правилом запросы также удаляются из базы данных.
     * <p>
     *
     * @param id идентификатор правила, которое необходимо удалить
     * @throws RulesNotFoundException если правило с указанным идентификатором не найдено
     */
    @Transactional
    public void deleteDynamicRule(Long id) {
        logger.info("Удаление правила с id: {}", id);
        if (!dynamicRuleRepository.existsById(id)) {
            logger.error("Правило с id: {} не найдено", id);
            throw new RulesNotFoundException("Не удалось удалить правило - правило не найдено ", id);
        }
        ruleStatsService.deleteRuleStats(id);
        dynamicRuleRepository.deleteById(id);
    }

    /**
     * Получает список всех существующих динамических правил.
     *
     * @return список динамических правил
     */

    public List<DynamicRule> getAllDynamicRules() {
        return Collections.unmodifiableList(dynamicRuleRepository.findAll());
    }
}

