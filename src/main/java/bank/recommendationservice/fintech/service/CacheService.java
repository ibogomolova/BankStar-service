package bank.recommendationservice.fintech.service;

import com.github.benmanes.caffeine.cache.Cache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class CacheService {
    private final Cache<String, Boolean> productTypeCache;
    private final Cache<String, Integer> transactionSumCache;
    private final Cache<String, Integer> transactionCountCache;

    private static final Logger logger = LoggerFactory.getLogger(CacheService.class);

    public CacheService(Cache<String, Boolean> productTypeCache,
                        Cache<String, Integer> transactionSumCache,
                        Cache<String, Integer> transactionCountCache) {
        this.productTypeCache = productTypeCache;
        this.transactionSumCache = transactionSumCache;
        this.transactionCountCache = transactionCountCache;
    }

    public void clearCaches() {
        productTypeCache.invalidateAll();
        transactionSumCache.invalidateAll();
        transactionCountCache.invalidateAll();
        logger.info("Все кеши были успешно очищены.");
    }
}
