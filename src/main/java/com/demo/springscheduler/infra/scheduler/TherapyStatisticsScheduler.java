package com.demo.springscheduler.infra.scheduler;

import com.demo.springscheduler.application.TherapyUserUseCase;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class TherapyStatisticsScheduler {

    private final TherapyStatisticsService statsService;
    private final TherapyUserUseCase therapyUserUseCase;

    // 매일 자정에 실행
    @Scheduled(cron = "0 0 0 * * *")
    public void aggregateDaily() {

        List<Long> targetTherapyUserIds = therapyUserUseCase.findTargetTherapyUsers();
        // 특정 기간 설정
        LocalDateTime startDateTime = LocalDateTime.now().minusDays(4);
        LocalDateTime endDateTime = LocalDateTime.now().minusDays(5);

        for (Long therapyUserId : targetTherapyUserIds) {
            log.info("[Therapy Stats Batch] 사용자 ID {} - 통계 집계 시작", therapyUserId);

            try {
                statsService.aggregateTherapyStatics(therapyUserId, startDateTime, endDateTime);
                log.info("[Therapy Stats Batch] 사용자 ID {} - 통계 집계 완료", therapyUserId);
            } catch (Exception e) {
                log.error("[Therapy Stats Batch] 사용자 ID {} - 통계 집계 실패: {}", therapyUserId, e.getMessage(), e);
            }
        }

    }
}
