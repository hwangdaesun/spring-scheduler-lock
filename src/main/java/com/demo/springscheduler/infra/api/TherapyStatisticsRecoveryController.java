package com.demo.springscheduler.infra.api;

import com.demo.springscheduler.application.v1.TherapyStatisticsUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class TherapyStatisticsRecoveryController {

    private final TherapyStatisticsUseCase statsUseCase;

    // 스케줄러 실패 시, 개발자가 수동으로 데이터를 복구할 수 있는 API입니다. 보안상 해당 API는 특정 IP 또는 관리자 권한을 가진 사용자만 접근할 수 있도록 제한해야 합니다.

    @PostMapping("/v1/therapy-statistics/recover")
    public ResponseEntity<Void> recoverTherapyStatistics(
            @RequestBody RecoverTherapyStatistics recoverTherapyStatistics) {
        statsUseCase.recoverTherapyStatistics(recoverTherapyStatistics.therapyUserId(),
                recoverTherapyStatistics.yearMonth(), recoverTherapyStatistics.startDateTime(),
                recoverTherapyStatistics.endDateTime());
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
}
