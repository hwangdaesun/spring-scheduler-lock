package com.demo.springscheduler.infra.scheduler;

import com.demo.springscheduler.application.v1.TherapyBatchLogUseCase;
import com.demo.springscheduler.application.v1.TherapyStatisticsUseCase;
import com.demo.springscheduler.application.v1.TherapyUserUseCase;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class TherapyStaticsticsScheduler {

    private final TherapyStatisticsUseCase therapyStatisticsUseCase;
    private final TherapyUserUseCase therapyUserUseCase;
    private final TherapyBatchLogUseCase therapyBatchLogUseCase;
//    private final NamedLockRepository namedLockRepository;

    // 매일 자정에 실행
    @Scheduled(cron = "0 0 * * * *")
    @SchedulerLock(name = SchedulerLockNames.THERAPY_STATISTICS_AGGREGATE, lockAtLeastFor = "PT30S", lockAtMostFor = "PT120S")
    public void aggregateDaily() {
        log.info("[Therapy Statistics Batch] 시작");
        List<Long> targetTherapyUserIds = therapyUserUseCase.findTargetTherapyUsers();

        // 오늘 기준 기간 계산
        LocalDateTime startDateTime = LocalDateTime.now();
        YearMonth currentMonth = YearMonth.from(startDateTime);
        LocalDate lastDayOfMonth = currentMonth.atEndOfMonth();
        LocalDateTime endDateTime = lastDayOfMonth.atTime(LocalTime.MAX);
        YearMonth yearMonth = YearMonth.from(startDateTime);

        // 배치 크기 설정
        final int batchSize = 1000;
        for (int i = 0; i < targetTherapyUserIds.size(); i += batchSize) {
            List<Long> batch = targetTherapyUserIds.subList(i, Math.min(i + batchSize, targetTherapyUserIds.size()));
            log.info("[Therapy Statistics Batch] 배치 처리 시작: index {} ~ {}, size={}",
                    i, Math.min(i + batchSize, targetTherapyUserIds.size()) - 1, batch.size());
            try {
                // 배치 로그 선 등록
                therapyBatchLogUseCase.markAllProgress(batch, yearMonth, startDateTime, endDateTime);
                // 통계 집계 배치 실행
                therapyStatisticsUseCase.aggregateAllTherapyStatics(batch, yearMonth, startDateTime, endDateTime);
                // 배치 성공 기록
                therapyBatchLogUseCase.markAllSuccess(batch, yearMonth.getYear(), yearMonth.getMonthValue());
                log.info("[Therapy Statistics Batch] 배치 처리 완료: size={}", batch.size());
            } catch (Exception e) {
                log.error("[Therapy Statistics Batch] 배치 처리 실패: size={}, error={}", batch.size(), e.getMessage(), e);
                try {
                    therapyBatchLogUseCase.markAllFail(batch, yearMonth.getYear(), yearMonth.getMonthValue(),
                            e.getMessage());
                } catch (Exception inner) {
                    log.warn("[Therapy Statistics Batch] 실패 로그 기록 실패(일괄): size={}, error={}", batch.size(),
                            inner.getMessage(), inner);
                }
            }
        }

        log.info("[Therapy Statistics Batch] 끝");
    }
}
