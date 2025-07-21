package com.demo.springscheduler.infra.scheduler;

import com.demo.springscheduler.application.TherapyPerformReader;
import com.demo.springscheduler.application.TherapyUserReader;
import com.demo.springscheduler.domain.therapy.TherapyCalculator;
import com.demo.springscheduler.domain.therapy.TherapyPerform;
import com.demo.springscheduler.domain.therapy.TherapyStaticsRepository;
import com.demo.springscheduler.domain.therapy.TherapyStatistics;
import com.demo.springscheduler.domain.user.TherapyUser;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TherapyStatisticsService {
    private final TherapyStaticsRepository therapyStaticsRepository;
    private final TherapyPerformReader therapyPerformReader;
    private final TherapyUserReader therapyUserReader;
    private final TherapyCalculator therapyCalculator;

    @Transactional
    public void aggregateTherapyStatics(Long therapyUserId, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        TherapyUser targetUser = therapyUserReader.read(therapyUserId);
        List<TherapyPerform> therapyPerforms = therapyPerformReader.read(targetUser, startDateTime, endDateTime);
        Double metrics = therapyCalculator.calculate(therapyPerforms);
        therapyStaticsRepository.save(TherapyStatistics.create(therapyUserId, metrics));
    }

}
