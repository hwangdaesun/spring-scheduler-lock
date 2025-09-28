package com.demo.springscheduler.application.v1;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toMap;

import com.demo.springscheduler.application.TherapyPerformReader;
import com.demo.springscheduler.application.TherapyUserReader;
import com.demo.springscheduler.domain.therapy.TherapyCalculator;
import com.demo.springscheduler.domain.therapy.TherapyPerform;
import com.demo.springscheduler.domain.therapy.TherapyStatistics;
import com.demo.springscheduler.domain.therapy.TherapyStatisticsRepository;
import com.demo.springscheduler.domain.user.TherapyUser;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TherapyStatisticsUseCase {
    private final TherapyStatisticsRepository therapyStatisticsRepository;
    private final com.demo.springscheduler.domain.therapy.TherapyStatisticsJdbcRepository therapyStatisticsJdbcRepository;
    private final TherapyPerformReader therapyPerformReader;
    private final TherapyUserReader therapyUserReader;
    private final TherapyCalculator therapyCalculator;
    private final TherapyBatchLogUseCase therapyBatchLogUseCase;

    @Transactional
    public void aggregateTherapyStatics(Long therapyUserId, YearMonth yearMonth, LocalDateTime startDateTime,
                                        LocalDateTime endDateTime) {
        TherapyUser targetUser = therapyUserReader.read(therapyUserId);

        Optional<TherapyStatistics> optionalTherapyStatistics = therapyStatisticsRepository.findByTherapyUserIdAndYearAndMonth(
                targetUser.getId(), yearMonth.getYear(), yearMonth.getMonthValue());
        boolean existing = optionalTherapyStatistics.isPresent();

        // 불필요한 중복 계산 방지
        if (existing && optionalTherapyStatistics.get().getUpdatedAt().toLocalDate()
                .equals(startDateTime.toLocalDate())) {
            return;
        }

        List<TherapyPerform> therapyPerforms = therapyPerformReader.readBetweenDateTime(targetUser, startDateTime,
                endDateTime);
        Double metrics = therapyCalculator.calculate(therapyPerforms);

        if (existing) {
            TherapyStatistics therapyStatistics = optionalTherapyStatistics.get();
            therapyStatistics.update(metrics);
        } else {
            therapyStatisticsRepository.save(
                    TherapyStatistics.create(therapyUserId, yearMonth.getYear(), yearMonth.getMonthValue(), metrics));
        }
    }

    @Transactional
    public void aggregateAllTherapyStatics(List<Long> therapyUserIds, YearMonth yearMonth, LocalDateTime startDateTime,
                                           LocalDateTime endDateTime) {
        if (therapyUserIds == null || therapyUserIds.isEmpty()) {
            return;
        }

        List<TherapyUser> users = therapyUserReader.readAllByIds(therapyUserIds);

        Map<Long, TherapyUser> userMap = users.stream()
                .collect(toMap(TherapyUser::getId, identity()));

        List<TherapyPerform> performs = therapyPerformReader.readInTherapyUsersAndBetweenDateTime(users, startDateTime,
                endDateTime);
        Map<Long, List<TherapyPerform>> performsByUserId = performs.stream()
                .collect(groupingBy(tp -> tp.getTherapyUser().getId()));

        List<TherapyStatistics> existingStatistics = therapyStatisticsRepository
                .findAllByTherapyUserIdInAndYearAndMonth(therapyUserIds, yearMonth.getYear(),
                        yearMonth.getMonthValue());
        Map<Long, TherapyStatistics> statisticsByUserId = existingStatistics.stream()
                .collect(toMap(TherapyStatistics::getTherapyUserId, identity()));

        List<TherapyStatistics> toInsert = new ArrayList<>();

        for (Long therapyUserId : therapyUserIds) {
            TherapyUser therapyUser = userMap.get(therapyUserId);
            if (therapyUser == null) {
                continue;
            }

            TherapyStatistics therapyStatistics = statisticsByUserId.get(therapyUserId);

            if (therapyStatistics != null && therapyStatistics.getUpdatedAt().toLocalDate()
                    .equals(startDateTime.toLocalDate())) {
                continue;
            }

            List<TherapyPerform> myPerforms = performsByUserId.getOrDefault(therapyUserId, List.of());
            Double metric = therapyCalculator.calculate(myPerforms);

            if (therapyStatistics != null) {
                therapyStatistics.update(metric);
            } else {
                toInsert.add(TherapyStatistics.create(therapyUserId, yearMonth.getYear(), yearMonth.getMonthValue(),
                        metric));
            }
        }

        if (!toInsert.isEmpty()) {
            therapyStatisticsJdbcRepository.batchInsert(toInsert);
        }
    }

    @Transactional
    public void recoverTherapyStatistics(Long therapyUserId, YearMonth yearMonth, LocalDateTime startDateTime,
                                         LocalDateTime endDateTime) {
        TherapyUser targetUser = therapyUserReader.read(therapyUserId);

        TherapyStatistics therapyStatistics = therapyStatisticsRepository.findByTherapyUserIdAndYearAndMonth(
                targetUser.getId(), yearMonth.getYear(), yearMonth.getMonthValue()).orElseThrow(RuntimeException::new);

        List<TherapyPerform> therapyPerforms = therapyPerformReader.readBetweenDateTime(targetUser, startDateTime,
                endDateTime);
        Double metrics = therapyCalculator.calculate(therapyPerforms);

        therapyStatistics.update(metrics);
        therapyBatchLogUseCase.markSuccess(therapyUserId, yearMonth.getYear(), yearMonth.getMonthValue());
    }
}
