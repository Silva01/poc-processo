package silva.daniel.project.study.streams.lambda.lab.virtual.threads.config;

import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableCaching
@EnableScheduling
public class CacheConfig {

    @Bean
    public org.ehcache.Cache<Object, Object> ehcache() {
        return CacheManagerBuilder.newCacheManagerBuilder()
                .withCache("processCache", CacheConfigurationBuilder.newCacheConfigurationBuilder(Object.class, Object.class, org.ehcache.config.builders.ResourcePoolsBuilder.heap(100))
                        .build())
                .build(true)
                .getCache("processCache", Object.class, Object.class);
    }
}
