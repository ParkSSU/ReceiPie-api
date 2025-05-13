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
 * ✅ RateLimiterService
 * - 사용자별 이미지 전송 제한을 관리하는 서비스 클래스
 */
@Service
public class RateLimiterService {

    // 사용자별 Bucket을 저장하는 ConcurrentMap
    private final ConcurrentMap<String, Bucket> buckets = new ConcurrentHashMap<>();

    // 하루 최대 이미지 전송 개수
    public static final long MAX_IMAGES = 2;  // 일일 최대 이미지 전송 개수
    private static final Duration REFILL_DURATION = Duration.ofDays(1);

    /**
     * 사용자별 Bucket을 생성하거나 기존 Bucket을 반환하는 메서드
     *
     * @param userId 사용자 ID
     * @return Bucket 사용자별 Bucket 인스턴스
     */
    public Bucket resolveBucket(String userId) {
        return buckets.computeIfAbsent(userId, this::createNewBucket);
    }

    /**
     * 새로운 Bucket을 생성하는 메서드
     *
     * @param userId 사용자 ID
     * @return Bucket 새롭게 생성된 Bucket
     */
    private Bucket createNewBucket(String userId) {
        Refill refill = Refill.intervally(MAX_IMAGES, REFILL_DURATION);
        Bandwidth limit = Bandwidth.classic(MAX_IMAGES, refill);
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
     * 사용자가 오늘 소모한 이미지 개수를 반환하는 메서드
     *
     * @param userId 사용자 ID
     * @return int 사용한 이미지 개수
     */
    public int getUsedImages(String userId) {
        Bucket bucket = buckets.get(userId);
        if (bucket == null) {
            return 0;
        }

        long remainingTokens = bucket.getAvailableTokens();
        return (int) (MAX_IMAGES - remainingTokens);
    }
}
