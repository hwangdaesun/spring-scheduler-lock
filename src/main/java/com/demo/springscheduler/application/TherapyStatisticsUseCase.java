package com.demo.springscheduler.application;

import com.demo.springscheduler.domain.therapy.TherapyCalculator;
import com.demo.springscheduler.domain.therapy.TherapyPerform;
import com.demo.springscheduler.domain.therapy.TherapyStatistics;
import com.demo.springscheduler.domain.therapy.TherapyStatisticsRepository;
import com.demo.springscheduler.domain.user.TherapyUser;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TherapyStatisticsUseCase {
    private final TherapyStatisticsRepository therapyStatisticsRepository;
    private final TherapyPerformReader therapyPerformReader;
    private final TherapyUserReader therapyUserReader;
    private final TherapyCalculator therapyCalculator;

    @Transactional
    public void aggregateTherapyStatics(Long therapyUserId, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        YearMonth yearMonth = YearMonth.from(startDateTime);
        TherapyUser targetUser = therapyUserReader.read(therapyUserId);

        Optional<TherapyStatistics> optionalTherapyStatistics = therapyStatisticsRepository.findByTherapyUserIdAndYearAndMonth(
                targetUser.getId(), yearMonth.getYear(), yearMonth.getMonthValue());
        boolean existing = optionalTherapyStatistics.isPresent();

        // 불필요한 중복 계산 방지
        if (existing && optionalTherapyStatistics.get().getUpdatedAt().toLocalDate()
                .equals(startDateTime.toLocalDate())) {
            return;
        }

        List<TherapyPerform> therapyPerforms = therapyPerformReader.read(targetUser, startDateTime, endDateTime);
        Double metrics = therapyCalculator.calculate(therapyPerforms);

        if (existing) {
            TherapyStatistics therapyStatistics = optionalTherapyStatistics.get();
            therapyStatistics.update(metrics);
        } else {
            therapyStatisticsRepository.save(
                    TherapyStatistics.create(therapyUserId, yearMonth.getYear(), yearMonth.getMonthValue(), metrics));
        }
    }
}
