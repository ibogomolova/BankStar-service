package bank.recommendationservice.fintech.service;

import com.github.benmanes.caffeine.cache.Cache;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.*;

class CacheServiceTest {
    @Mock
    private Cache<String, Boolean> productTypeCache;

    @Mock
    private Cache<String, Integer> transactionSumCache;

    @Mock
    private Cache<String, Integer> transactionCountCache;

    private CacheService cacheService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        cacheService = new CacheService(productTypeCache, transactionSumCache, transactionCountCache);
    }

    @Test
    public void testClearCaches_ShouldInvalidateAllCaches() {
        cacheService.clearCaches();

        verify(productTypeCache, times(1)).invalidateAll();
        verify(transactionSumCache, times(1)).invalidateAll();
        verify(transactionCountCache, times(1)).invalidateAll();
    }

    @Test
    public void testClearCaches_ShouldNotThrowException() {
        doNothing().when(productTypeCache).invalidateAll();
        doNothing().when(transactionSumCache).invalidateAll();
        doNothing().when(transactionCountCache).invalidateAll();

        assertDoesNotThrow(() -> cacheService.clearCaches());
    }


}