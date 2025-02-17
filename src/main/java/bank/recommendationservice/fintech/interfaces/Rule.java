package bank.recommendationservice.fintech.interfaces;

import java.util.UUID;

public interface Rule {
    boolean evaluate(UUID userId);
}