package bank.recommendationservice.fintech.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;


import java.util.Objects;
import java.util.UUID;

/**
 • DTO для представления рекомендации.
 */
@Getter
@Setter
@Schema(description = "DTO для представления рекомендации")
public class RecommendationDTO {

    @NotNull(message = "ID cannot be null")
    @Schema(description = "Уникальный идентификатор рекомендации", example = "550e8400-e29b-41d4-a716-446655440000")
    private UUID id;
    @NotBlank(message = "Name cannot be blank")
    @Schema(description = "Название рекомендации", example = "Специальное предложение по кредиту")
    private String name;
    @NotBlank(message = "Text cannot be blank")
    @Schema(description = "Текст рекомендации", example = "Предложение для вас - выгодный кредит")
    private String text;

    public RecommendationDTO() {
    }

    public RecommendationDTO(UUID id, String name, String text) {
        this.id = id;
        this.name = name;
        this.text = text;
    }

    public RecommendationDTO(String product1, String description1) {
        this.id = UUID.randomUUID();
        this.name = product1;
        this.text = description1;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RecommendationDTO that = (RecommendationDTO) o;
        return Objects.equals(id, that.id) && Objects.equals(name, that.name) && Objects.equals(text, that.text);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, text);
    }

    @Override
    public String toString() {
        return name + " - " + text;
    }
}