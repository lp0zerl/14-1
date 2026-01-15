package config;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

@Configuration
@EnableCaching
class CacheConfiguration {
    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        cacheManager.setCaffeine(Caffeine.newBuilder()
                .expireAfterWrite(10, TimeUnit.MINUTES)
                .maximumSize(10000));
        cacheManager.setCacheNames(Arrays.asList(
                "userOfCache",
                "activeUserOfCache",
                "transactionSumCache",
                "depositWithdrawCache",
                "ruleCache",
                "recommendationCache"
        ));
        return cacheManager;
    }
}
