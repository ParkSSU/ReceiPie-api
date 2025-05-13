package com.parkssu.receipie_api.global.service;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 매일 자정에 모든 사용자의 요청량을 초기화하는 스케줄러.
 */
@Component
@RequiredArgsConstructor
public class BucketResetScheduler {

    private final RateLimiterService rateLimiterService;

    /**
     * 매일 자정(00:00)에 모든 사용자의 Bucket을 초기화.
     */
    @Scheduled(cron = "0 0 0 * * ?")
    public void resetBucketsDaily() {
        rateLimiterService.resetAllBuckets();
    }
}
