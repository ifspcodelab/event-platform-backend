package br.edu.ifsp.spo.eventos.eventplatformbackend.common.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
public class CacheConfig {
    @Value("${app.cache.siteExpireTimeInSeconds}")
    private Integer siteExpireTimeInSeconds;

    @Value("${app.cache.activityExpireTimeInSeconds}")
    private Integer activityExpireTimeInSeconds;

    public static final String SITE_ACTIVITY_EVENT_CACHE = "SITE_ACTIVITY_CACHE";
    public static final String SITE_ACTIVITY_SUBEVENT_CACHE = "SITE_ACTIVITY_SUBEVENT_CACHE";


    @Bean
    public Caffeine caffeineConfig() {
        return Caffeine.newBuilder().expireAfterWrite(siteExpireTimeInSeconds, TimeUnit.SECONDS);
    }

    @Bean
    public CacheManager cacheManager(Caffeine caffeine) {
        CaffeineCacheManager caffeineCacheManager = new CaffeineCacheManager();

        caffeineCacheManager.registerCustomCache(
            SITE_ACTIVITY_EVENT_CACHE,
            Caffeine.newBuilder().expireAfterWrite(activityExpireTimeInSeconds, TimeUnit.SECONDS).build()
        );

        caffeineCacheManager.registerCustomCache(
            SITE_ACTIVITY_SUBEVENT_CACHE,
            Caffeine.newBuilder().expireAfterWrite(activityExpireTimeInSeconds, TimeUnit.SECONDS).build()
        );

        caffeineCacheManager.setCaffeine(caffeine);
        return caffeineCacheManager;
    }
}
