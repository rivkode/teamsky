package com.project.quiz.infrastructure.statistics;

import com.project.quiz.application.statistics.repository.ProblemCorrectRateCacheEntry;
import com.project.quiz.application.statistics.repository.ProblemCorrectRateCacheRepository;
import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.Optional;

@Repository
@Slf4j
@RequiredArgsConstructor
public class RedisProblemCorrectRateCacheRepository implements ProblemCorrectRateCacheRepository {

    private static final String KEY_PREFIX = "problem:correct-rate:";
    private static final String NULL_SENTINEL = "NULL";
    private static final Duration TTL = Duration.ofHours(6);

    private final StringRedisTemplate stringRedisTemplate;

    @Override
    public Optional<ProblemCorrectRateCacheEntry> find(Long problemId) {
        String key = key(problemId);
        String cachedValue = null;
        try {
            cachedValue = stringRedisTemplate.opsForValue().get(key);
            if (cachedValue == null) {
                log.debug("cache miss for correct rate. problemId={} key={}", problemId, key);
                return Optional.empty();
            }
            if (NULL_SENTINEL.equals(cachedValue)) {
                log.debug("cache hit with null sentinel for correct rate. problemId={} key={}", problemId, key);
                return Optional.of(new ProblemCorrectRateCacheEntry(null));
            }
            return Optional.of(new ProblemCorrectRateCacheEntry(Integer.valueOf(cachedValue)));
        } catch (NumberFormatException exception) {
            log.warn("failed to parse cached correct rate. problemId={} key={} rawValue={}", problemId, key, cachedValue, exception);
            return Optional.empty();
        } catch (RuntimeException exception) {
            log.warn("failed to read correct rate from redis. problemId={} key={}", problemId, key, exception);
            return Optional.empty();
        }
    }

    @Override
    public void put(Long problemId, Integer correctRate) {
        String key = key(problemId);
        try {
            String value = correctRate == null ? NULL_SENTINEL : String.valueOf(correctRate);
            stringRedisTemplate.opsForValue().set(key, value, TTL);
            if (correctRate == null) {
                log.debug("stored null sentinel for correct rate. problemId={} key={} ttlSeconds={}", problemId, key, TTL.toSeconds());
            } else {
                log.debug("stored correct rate in redis. problemId={} key={} correctRate={} ttlSeconds={}", problemId, key, correctRate, TTL.toSeconds());
            }
        } catch (RuntimeException exception) {
            log.warn("failed to write correct rate to redis. problemId={} key={} correctRate={}", problemId, key, correctRate, exception);
        }
    }

    @Override
    public void evict(Long problemId) {
        String key = key(problemId);
        try {
            stringRedisTemplate.delete(key);
            log.debug("evicted correct rate cache. problemId={} key={}", problemId, key);
        } catch (RuntimeException exception) {
            log.warn("failed to evict correct rate cache. problemId={} key={}", problemId, key, exception);
        }
    }

    private String key(Long problemId) {
        return KEY_PREFIX + problemId;
    }
}
