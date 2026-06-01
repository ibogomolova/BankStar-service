package bank.recommendationservice.fintech.interfaces;

import bank.recommendationservice.fintech.dto.RecommendationDTO;

import java.util.UUID;

public interface RecommendationRuleSet {
    RecommendationDTO recommend(UUID userId);
}