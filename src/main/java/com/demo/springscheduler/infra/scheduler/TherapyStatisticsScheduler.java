package com.demo.springscheduler.infra.scheduler;

import com.demo.springscheduler.application.TherapyUserUseCase;
import com.demo.springscheduler.infra.scheduler.lock.NamedLockRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
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
    private final NamedLockRepository namedLockRepository;

    // 매일 자정에 실행
    @Scheduled(cron = "0 0 0 * * *")
    public void aggregateDaily() {

        List<Long> targetTherapyUserIds = therapyUserUseCase.findTargetTherapyUsers();

        // 특정 기간 설정 (오늘 ~ 달의 마지막 날)

        // 오늘
        LocalDateTime startDateTime = LocalDateTime.now();

        // 이번 달의 마지막 날
        YearMonth currentMonth = YearMonth.from(startDateTime);
        LocalDate lastDayOfMonth = currentMonth.atEndOfMonth();
        LocalDateTime endDateTime = lastDayOfMonth.atTime(LocalTime.MAX);

        for (Long therapyUserId : targetTherapyUserIds) {
            log.info("[Therapy Stats Batch] 사용자 ID {} - 통계 집계 시작", therapyUserId);
            namedLockRepository.acquireLock("batch-lock");
            try {
                statsService.aggregateTherapyStatics(therapyUserId, startDateTime, endDateTime);
                log.info("[Therapy Stats Batch] 사용자 ID {} - 통계 집계 완료", therapyUserId);
            } catch (Exception e) {
                log.error("[Therapy Stats Batch] 사용자 ID {} - 통계 집계 실패: {}", therapyUserId, e.getMessage(), e);
            }finally {
                namedLockRepository.releaseLock("batch-lock");
            }
        }

    }
}
