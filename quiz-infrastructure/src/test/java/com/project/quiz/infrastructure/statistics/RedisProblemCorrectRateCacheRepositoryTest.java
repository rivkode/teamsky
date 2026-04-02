package com.project.quiz.infrastructure.statistics;

import com.project.quiz.application.statistics.repository.ProblemCorrectRateCacheEntry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.Duration;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RedisProblemCorrectRateCacheRepositoryTest {

    private static final String KEY = "problem:correct-rate:1001";

    @Mock
    private StringRedisTemplate stringRedisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    private RedisProblemCorrectRateCacheRepository repository;

    @BeforeEach
    void setUp() {
        repository = new RedisProblemCorrectRateCacheRepository(stringRedisTemplate);
        lenient().when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
    }

    @Test
    void returnsEmptyWhenCacheMiss() {
        when(valueOperations.get(KEY)).thenReturn(null);

        Optional<ProblemCorrectRateCacheEntry> result = repository.find(1001L);

        assertThat(result).isEmpty();
    }

    @Test
    void returnsNullEntryWhenNullSentinelHit() {
        when(valueOperations.get(KEY)).thenReturn("NULL");

        Optional<ProblemCorrectRateCacheEntry> result = repository.find(1001L);

        assertThat(result).hasValue(new ProblemCorrectRateCacheEntry(null));
    }

    @Test
    void returnsCorrectRateWhenNumericValueExists() {
        when(valueOperations.get(KEY)).thenReturn("67");

        Optional<ProblemCorrectRateCacheEntry> result = repository.find(1001L);

        assertThat(result).hasValue(new ProblemCorrectRateCacheEntry(67));
    }

    @Test
    void returnsEmptyWhenCachedValueCannotBeParsed() {
        when(valueOperations.get(KEY)).thenReturn("abc");

        Optional<ProblemCorrectRateCacheEntry> result = repository.find(1001L);

        assertThat(result).isEmpty();
    }

    @Test
    void returnsEmptyWhenRedisReadFails() {
        when(valueOperations.get(KEY)).thenThrow(new RuntimeException("redis down"));

        Optional<ProblemCorrectRateCacheEntry> result = repository.find(1001L);

        assertThat(result).isEmpty();
    }

    @Test
    void storesNullSentinelWhenCorrectRateIsNull() {
        repository.put(1001L, null);

        verify(valueOperations).set(eq(KEY), eq("NULL"), any(Duration.class));
    }

    @Test
    void storesNumericValueWhenCorrectRateExists() {
        repository.put(1001L, 42);

        verify(valueOperations).set(eq(KEY), eq("42"), any(Duration.class));
    }

    @Test
    void swallowsRedisWriteFailure() {
        doThrow(new RuntimeException("write fail"))
                .when(valueOperations)
                .set(eq(KEY), eq("42"), any(Duration.class));

        repository.put(1001L, 42);
    }

    @Test
    void evictsCacheKey() {
        repository.evict(1001L);

        verify(stringRedisTemplate).delete(KEY);
    }

    @Test
    void swallowsRedisDeleteFailure() {
        doThrow(new RuntimeException("delete fail"))
                .when(stringRedisTemplate)
                .delete(KEY);

        repository.evict(1001L);
    }
}
