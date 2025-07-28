package com.demo.springscheduler.infra.scheduler;

import com.demo.springscheduler.application.TherapyBatchLogUseCase;
import com.demo.springscheduler.application.TherapyStatisticsUseCase;
import com.demo.springscheduler.application.TherapyUserUseCase;
import com.demo.springscheduler.domain.NamedLockRepository;
import com.demo.springscheduler.domain.log.TherapyBatchLog;
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

    private final TherapyStatisticsUseCase statsUseCase;
    private final TherapyUserUseCase therapyUserUseCase;
    private final NamedLockRepository namedLockRepository;
    private final TherapyBatchLogUseCase therapyBatchLogUseCase;

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
        YearMonth yearMonth = YearMonth.from(startDateTime);

        List<TherapyBatchLog> therapyBatchLogs = TherapyBatchLog.markProgress(
                targetTherapyUserIds, yearMonth.getYear(), yearMonth.getMonthValue(), startDateTime, endDateTime);
        therapyBatchLogUseCase.batchInsert(therapyBatchLogs);

        for(Long therapyUserId : targetTherapyUserIds) {
            log.info("[Therapy Stats Batch] 사용자 ID {} - 통계 집계 시작", therapyUserId);
            namedLockRepository.acquireLock("batch-lock");
            try {
                statsUseCase.aggregateTherapyStatics(
                        therapyUserId, yearMonth, startDateTime, endDateTime);
                therapyBatchLogUseCase.markSuccess(therapyUserId, yearMonth.getYear(), yearMonth.getMonthValue());
                log.info("[Therapy Stats Batch] 사용자 ID {} - 통계 집계 완료", therapyUserId);
            } catch(Exception e) {
                log.error("[Therapy Stats Batch] 사용자 ID {} - 통계 집계 실패: {}", therapyUserId, e.getMessage(), e);
                therapyBatchLogUseCase.markFail(therapyUserId, yearMonth.getYear(), yearMonth.getMonthValue(), e.getMessage());
            } finally {
                namedLockRepository.releaseLock("batch-lock");
            }
        }

    }
}
