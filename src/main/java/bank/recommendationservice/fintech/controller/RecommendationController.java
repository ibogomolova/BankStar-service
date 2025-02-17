package bank.recommendationservice.fintech.controller;

import bank.recommendationservice.fintech.dto.RecommendationDTO;
import bank.recommendationservice.fintech.model.RecommendationResponse;
import bank.recommendationservice.fintech.service.RecommendationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

/**
 * Контроллер для управления рекомендациями.
 */
@RestController
@RequestMapping("/recommendation")
@Tag(name = "Recommendation", description = "Эндпоинты для управления рекомендациями")
public class RecommendationController {
    private final RecommendationService recommendationService;

    public RecommendationController(RecommendationService recommendationService) {
        this.recommendationService = recommendationService;
    }

    /**
     * Получает список рекомендаций для пользователя с заданным ID.
     *
     * @param userId ID пользователя
     * @return ответ, содержащий список рекомендаций для пользователя
     */
    @GetMapping("/{user_id}")
    @Operation(summary = "Получение рекомендаций пользователя", description = "Возвращает список рекомендаций для пользователя")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешно получены рекомендации",
                    content = @Content(schema = @Schema(implementation = RecommendationResponse.class)))
    })
    public ResponseEntity<RecommendationResponse> getRecommendations(@PathVariable("user_id") UUID userId) {
        List<RecommendationDTO> recommendations = recommendationService.getRecommendations(userId);
        RecommendationResponse response = new RecommendationResponse(userId, recommendations);
        return ResponseEntity.ok(response);
    }
}