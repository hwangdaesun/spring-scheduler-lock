package com.demo.springscheduler.domain.therapy;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TherapyStatisticsRepository extends JpaRepository<TherapyStatistics, Long> {

    Optional<TherapyStatistics> findByTherapyUserIdAndYearAndMonth(Long therapyUserId, Integer year, Integer month);

    // Note: Use therapyUserId for bulk fetch, not the entity PK id.
    List<TherapyStatistics> findAllByTherapyUserIdInAndYearAndMonth(List<Long> therapyUserIds, Integer year,
                                                                    Integer month);
}
