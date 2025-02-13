package bank.recommendationservice.fintech.controller;

import bank.recommendationservice.fintech.dto.RecommendationDTO;
import bank.recommendationservice.fintech.model.RecommendationResponse;
import bank.recommendationservice.fintech.service.RecommendationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = RecommendationController.class)
class RecommendationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RecommendationService recommendationService;


    /**
     * Тестирует, что GET-запрос к /recommendation/{user_id} с userId, у которого есть рекомендации,
     * возвращает список рекомендаций.
     * <p>
     * 1. Mock'ирует RecommendationService, чтобы он возвращал ожидаемый список рекомендаций.
     * 2. Выполняет GET-запрос к /recommendation/{user_id}.
     * 3. Проверяет, что ответ имеет статус 200, тип application/json.
     * 4. Парсит ответ в RecommendationResponse.
     * 5. Проверяет, что список рекомендаций в ответе соответствует ожидаемому.
     */
    @Test
    public void testGetRecommendations() throws Exception {
        // data
        UUID userId = UUID.randomUUID();
        List<RecommendationDTO> expectedRecommendations = Arrays.asList(
                new RecommendationDTO("product1", "description1"),
                new RecommendationDTO("product2", "description2")
        );
        when(recommendationService.getRecommendations(userId)).thenReturn(expectedRecommendations);

        // test
        MvcResult result = mockMvc.perform(get("/recommendation/{user_id}", userId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        // check
        String responseContent = result.getResponse().getContentAsString();
        RecommendationResponse response = new ObjectMapper().readValue(responseContent, RecommendationResponse.class);
        assertEquals(expectedRecommendations, response.getRecommendations());
    }

    /**
     * Тестирует, что GET-запрос к /recommendation/{user_id} с пользователем, у которого нет рекомендаций,
     * возвращает пустой список.
     */
    @Test
    public void testGetRecommendationsNotFound() throws Exception {
        // data
        UUID userId = UUID.randomUUID();
        when(recommendationService.getRecommendations(userId)).thenReturn(Collections.emptyList());

        // test & check
        mockMvc.perform(get("/recommendation/{user_id}", userId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.recommendations").isArray())
                .andExpect(jsonPath("$.recommendations").isEmpty())
                .andReturn();
    }
}