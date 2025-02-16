package bank.recommendationservice.fintech.model;

import bank.recommendationservice.fintech.dto.RecommendationDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Модель ответа для получения рекомендаций
 */
@Getter
@Setter
@Schema(description = "Модель ответа для списка рекомендаций")
public class RecommendationResponse {
    @Schema(description = "Уникальный идентификатор пользователя", example = "550e8400-e29b-41d4-a716-446655440000")
    private UUID user_id;
    @Schema(description = "Список рекомендаций для пользователя")
    private List<RecommendationDTO> recommendations;

    public RecommendationResponse() {
    }

    public RecommendationResponse(UUID user_id, List<RecommendationDTO> recommendations) {
        this.user_id = user_id;
        this.recommendations = recommendations;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RecommendationResponse that = (RecommendationResponse) o;
        return Objects.equals(user_id, that.user_id) && Objects.equals(recommendations, that.recommendations);
    }

    @Override
    public int hashCode() {
        return Objects.hash(user_id, recommendations);
    }

    @Override
    public String toString() {
        return "RecommendationResponse{" +
                "user_id=" + user_id +
                ", recommendations=" + recommendations +
                '}';
    }
}