package silva.daniel.project.study.streams.lambda.lab.virtual.threads.scheduler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class ClearCacheScheduler {

    private static final Logger log = LoggerFactory.getLogger(ClearCacheScheduler.class);

    // Método que será executado para limpar o cache
    @CacheEvict(value = "processCache", allEntries = true)
    @Scheduled(fixedRate = 3 * 60 * 1000)  // 5 min
    public void clearCache() {
        log.warn("Cache cleared!");
    }
}
