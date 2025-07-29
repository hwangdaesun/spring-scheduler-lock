package com.demo.springscheduler.infra.scheduler;

import com.demo.springscheduler.application.TherapyBatchLogUseCase;
import com.demo.springscheduler.application.TherapyStatisticsUseCase;
import com.demo.springscheduler.domain.NamedLockRepository;
import com.demo.springscheduler.domain.log.Status;
import com.demo.springscheduler.domain.log.TherapyBatchLog;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class FailedTherapyStatisticsScheduler {

    private final TherapyBatchLogUseCase therapyBatchLogUseCase;
    private final TherapyStatisticsUseCase therapyStatisticsUseCase;
    private final NamedLockRepository namedLockRepository;

    @Scheduled(cron = "0 0 5 * * *") // 매일 새벽 5시에 실행
    public void recover() {
        List<TherapyBatchLog> failedLogs = therapyBatchLogUseCase.findFailedLogs(Status.FAIL);

        for (TherapyBatchLog batchLog : failedLogs) {
            Long userId = batchLog.getTherapyUserId();
            log.info("[Therapy Stats Batch] 사용자 ID {} - 통계 집계 시작", userId);
            YearMonth yearMonth = YearMonth.of(batchLog.getYear(), batchLog.getMonth());
            LocalDateTime start = batchLog.getStartTime();
            LocalDateTime end = batchLog.getEndTime();

            namedLockRepository.acquireLock("batch-lock");

            try {
                therapyStatisticsUseCase.recoverTherapyStatistics(userId, yearMonth, start, end);
                batchLog.markSuccess(LocalDateTime.now());
                log.info("[Therapy Stats Batch] 사용자 ID {} - 통계 집계 완료", userId);
            } catch (Exception e) {
                batchLog.markFail(LocalDateTime.now(), e.getMessage());
                log.error("[Therapy Stats Batch] 사용자 ID {} - 통계 집계 실패: {}", userId, e.getMessage(), e);
            } finally {
                namedLockRepository.releaseLock("batch-lock");
            }
        }
    }

}
