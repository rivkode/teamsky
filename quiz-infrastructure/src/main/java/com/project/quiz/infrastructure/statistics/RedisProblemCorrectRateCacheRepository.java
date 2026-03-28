package com.project.quiz.infrastructure.statistics;

import com.project.quiz.application.statistics.repository.ProblemCorrectRateCacheEntry;
import com.project.quiz.application.statistics.repository.ProblemCorrectRateCacheRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class RedisProblemCorrectRateCacheRepository implements ProblemCorrectRateCacheRepository {

    private static final String KEY_PREFIX = "problem:correct-rate:";
    private static final String NULL_SENTINEL = "NULL";
    private static final Duration TTL = Duration.ofHours(6);

    private final StringRedisTemplate stringRedisTemplate;

    @Override
    public Optional<ProblemCorrectRateCacheEntry> find(Long problemId) {
        try {
            String cachedValue = stringRedisTemplate.opsForValue().get(key(problemId));
            if (cachedValue == null) {
                return Optional.empty();
            }
            if (NULL_SENTINEL.equals(cachedValue)) {
                return Optional.of(new ProblemCorrectRateCacheEntry(null));
            }
            return Optional.of(new ProblemCorrectRateCacheEntry(Integer.valueOf(cachedValue)));
        } catch (RuntimeException ignored) {
            return Optional.empty();
        }
    }

    @Override
    public void put(Long problemId, Integer correctRate) {
        try {
            String value = correctRate == null ? NULL_SENTINEL : String.valueOf(correctRate);
            stringRedisTemplate.opsForValue().set(key(problemId), value, TTL);
        } catch (RuntimeException ignored) {
        }
    }

    @Override
    public void evict(Long problemId) {
        try {
            stringRedisTemplate.delete(key(problemId));
        } catch (RuntimeException ignored) {
        }
    }

    private String key(Long problemId) {
        return KEY_PREFIX + problemId;
    }
}
