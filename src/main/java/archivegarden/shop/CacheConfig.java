package archivegarden.shop;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
@EnableCaching
public class CacheConfig {

    private static final int CACHE_MAX_SIZE = 1000;
    private static final Duration CATEGORY_CACHE_TTL = Duration.ofHours(24);

    @Bean
    public CacheManager cacheManager() {
        Caffeine<Object, Object> caffeine = Caffeine.newBuilder()
                .maximumSize(CACHE_MAX_SIZE)
                .expireAfterWrite(CATEGORY_CACHE_TTL);

        return new CaffeineCacheManager("parentCategories") {{
            setCaffeine(caffeine);
        }};
    }
}
