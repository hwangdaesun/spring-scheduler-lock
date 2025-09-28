package com.demo.springscheduler.application.v1;

import com.demo.springscheduler.domain.log.Status;
import com.demo.springscheduler.domain.log.TherapyBatchLog;
import com.demo.springscheduler.domain.log.TherapyBatchLogJdbcRepository;
import com.demo.springscheduler.domain.log.TherapyBatchLogRepository;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TherapyBatchLogUseCase {

    private final TherapyBatchLogRepository therapyBatchLogRepository;
    private final TherapyBatchLogJdbcRepository therapyBatchLogJdbcRepository;

    @Transactional
    public void markAllProgress(List<Long> targetTherapyUserIds, YearMonth yearMonth, LocalDateTime startTime,
                                LocalDateTime endTime) {
        List<TherapyBatchLog> therapyBatchLogs = TherapyBatchLog.markAllProgress(
                targetTherapyUserIds, yearMonth.getYear(), yearMonth.getMonthValue(), startTime, endTime);
        therapyBatchLogJdbcRepository.batchInsert(therapyBatchLogs);
    }

    @Transactional
    public void markProgress(Long therapyUserId, Integer year, Integer month, LocalDateTime startTime,
                             LocalDateTime endTime) {
        TherapyBatchLog therapyBatchLog = TherapyBatchLog.markProgress(therapyUserId, year, month, startTime, endTime);
        therapyBatchLogRepository.save(therapyBatchLog);
    }

    @Transactional
    public void markAllSuccess(List<Long> therapyUserIds, Integer year, Integer month) {
        if (therapyUserIds == null || therapyUserIds.isEmpty()) {
            return;
        }
        LocalDateTime now = LocalDateTime.now();
        therapyBatchLogJdbcRepository.batchMarkSuccess(therapyUserIds, year, month, now);
    }

    @Transactional
    public void markSuccess(Long therapyUserId, Integer year, Integer month) {
        TherapyBatchLog therapyBatchLog = therapyBatchLogRepository.findTherapyBatchLogByTherapyUserIdAndYearAndMonth(
                therapyUserId, year, month).orElseThrow(RuntimeException::new);
        therapyBatchLog.markSuccess(LocalDateTime.now());
    }

    @Transactional
    public void markAllFail(List<Long> therapyUserIds, Integer year, Integer month, String errorMessage) {
        if (therapyUserIds == null || therapyUserIds.isEmpty()) {
            return;
        }
        LocalDateTime now = LocalDateTime.now();
        therapyBatchLogJdbcRepository.batchMarkFail(therapyUserIds, year, month, now, errorMessage);
    }

    @Transactional
    public void markFail(Long therapyUserId, Integer year, Integer month, String errorMessage) {
        TherapyBatchLog therapyBatchLog = therapyBatchLogRepository.findTherapyBatchLogByTherapyUserIdAndYearAndMonth(
                therapyUserId, year, month).orElseThrow(RuntimeException::new);
        therapyBatchLog.markFail(LocalDateTime.now(), errorMessage);
    }

    @Transactional(readOnly = true)
    public List<TherapyBatchLog> findFailedLogs(Status status) {
        // 실패 로그를 재시도 가능 횟수도 제한둘 수 있다.
        return therapyBatchLogRepository.findByStatus(status);
    }
}
