package bank.recommendationservice.fintech.controller;

import bank.recommendationservice.fintech.model.DynamicRule;
import bank.recommendationservice.fintech.model.RuleStatsResponse;
import bank.recommendationservice.fintech.service.RecommendationDynamicRuleService;
import bank.recommendationservice.fintech.service.RuleStatsService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(controllers = RecommendationDynamicRuleController.class)
public class RecommendationDynamicRuleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private RecommendationDynamicRuleService recommendationDynamicRuleService;

    @Mock
    private RuleStatsService ruleStatsService;

    @InjectMocks
    private RecommendationDynamicRuleController recommendationDynamicRuleController;

    private ObjectMapper objectMapper;


    @BeforeEach
    public void setUp() {
        objectMapper = new ObjectMapper();
        MockitoAnnotations.openMocks(this);
        System.out.println("recommendationDynamicRuleService: " + recommendationDynamicRuleService);
        mockMvc = MockMvcBuilders.standaloneSetup(recommendationDynamicRuleController).build();

    }


    @Test
    public void testCreateDynamicRule_Success2() throws Exception {
        DynamicRule dynamicRule = new DynamicRule();
        dynamicRule.setProductName("Test Product");
        dynamicRule.setProductText("Test description");
        // Настройте ваш объект dynamicRule

        when(recommendationDynamicRuleService.addRule(any(DynamicRule.class))).thenReturn(dynamicRule);

        mockMvc.perform(post("/rule")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dynamicRule)))
                .andExpect(status().isOk());

        verify(recommendationDynamicRuleService, times(1)).addRule(any(DynamicRule.class));
    }

    @Test
    public void testCreateRule() throws Exception {
        // data
        DynamicRule rule = new DynamicRule();
        rule.setProductName("Test Rule");
        rule.setProductText("Test description");

        // test & check
        mockMvc.perform(post("/rule")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(rule)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void testCreateRuleInvalidRequest() throws Exception {
        // data
        DynamicRule rule = new DynamicRule();
        rule.setProductName(null);
        rule.setProductText("Test description");

        // test & check
        mockMvc.perform(post("/rule")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(rule)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testDeleteRule() throws Exception {
        // data
        Long id = 1L;

        // test & check
        mockMvc.perform(delete("/rule/{id}", id))
                .andExpect(status().isNoContent());
    }

    @Test
    public void testDeleteRuleNotFound() throws Exception {
        // data
        Long id = 1L;

        // test & check
        doThrow(new RuntimeException("Rule not found")).when(recommendationDynamicRuleService).deleteDynamicRule(id);

        mockMvc.perform(delete("/rule/{id}", id))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testGetAllRules() throws Exception {
        // data
        List<DynamicRule> rules = Arrays.asList(
                new DynamicRule(),
                new DynamicRule()
        );

        // test & check
        when(recommendationDynamicRuleService.getAllDynamicRules()).thenReturn(rules);

        mockMvc.perform(get("/rule"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void testGetAllRulesEmpty() throws Exception {
        // data
        List<DynamicRule> rules = Collections.emptyList();

        // test & check
        when(recommendationDynamicRuleService.getAllDynamicRules()).thenReturn(rules);

        mockMvc.perform(get("/rule"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void testGetRuleStats() throws Exception {
        // data
        RuleStatsResponse response = new RuleStatsResponse();

        // test & check
        when(ruleStatsService.getAllRuleStats()).thenReturn(response);

        mockMvc.perform(get("/rule/stats"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void testGetRuleStatsError() throws Exception {
        // data
        RuleStatsResponse response = new RuleStatsResponse();

        // test & check
        when(ruleStatsService.getAllRuleStats()).thenThrow(new RuntimeException("Error"));

        mockMvc.perform(get("/rule/stats"))
                .andExpect(status().isInternalServerError());
    }

    private String asJsonString(Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testCreateDynamicRule_Success() throws Exception {
        DynamicRule rule = new DynamicRule();
        rule.setId(1L);
        rule.setProductName("Test Product");
        rule.setProductText("Test description");


        when(recommendationDynamicRuleService.addRule(any(DynamicRule.class))).thenReturn(rule);

        mockMvc.perform(post("/rule")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(rule)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.productName").value("Test Product"));
    }
}