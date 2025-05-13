package com.parkssu.receipie_api.global.service;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Bucket4j;
import io.github.bucket4j.Refill;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 사용자별 요청 제한을 관리하는 서비스 클래스.
 */
@Service
public class RateLimiterService {

    // 사용자별 Bucket을 저장하는 ConcurrentMap
    private final ConcurrentMap<String, Bucket> buckets = new ConcurrentHashMap<>();
    public static final long MAX_TOKENS = 2;  // 하루 최대 요청 수
    private static final Duration REFILL_DURATION = Duration.ofDays(1);

    /**
     * 사용자별 Bucket을 생성하거나 기존 Bucket을 반환하는 메서드
     *
     * @param userId - 사용자 ID
     * @return Bucket - 사용자별 Bucket 인스턴스
     */
    public Bucket resolveBucket(String userId) {
        return buckets.computeIfAbsent(userId, this::createNewBucket);
    }

    /**
     * 새로운 Bucket을 생성하는 메서드
     * 하루 10개의 요청을 허용하도록 설정
     *
     * @param userId - 사용자 ID
     * @return Bucket - 새롭게 생성된 Bucket
     */
    private Bucket createNewBucket(String userId) {
        Refill refill = Refill.intervally(MAX_TOKENS, REFILL_DURATION);
        Bandwidth limit = Bandwidth.classic(MAX_TOKENS, refill);

        return Bucket4j.builder()
                .addLimit(limit)
                .build();
    }

    /**
     * 매일 자정에 모든 Bucket을 초기화하는 메서드
     */
    public void resetAllBuckets() {
        buckets.clear();
        System.out.println("모든 Bucket이 초기화되었습니다.");
    }

    /**
     * 현재 사용자의 요청 횟수를 반환하는 메서드.
     *
     * @param userId 사용자 ID
     * @return int - 사용한 요청 횟수
     */
    public int getUsedRequests(String userId) {
        Bucket bucket = buckets.get(userId);
        if (bucket == null) {
            return 0;
        }

        long remainingTokens = bucket.getAvailableTokens(); // 남은 토큰 수
        long maxTokens = 10; // 하루 최대 요청 수

        // 사용한 요청 수 = 최대 요청 수 - 남은 토큰 수
        return (int) (maxTokens - remainingTokens);
    }

}
