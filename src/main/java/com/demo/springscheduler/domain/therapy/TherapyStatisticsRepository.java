package com.demo.springscheduler.domain.therapy;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TherapyStatisticsRepository extends JpaRepository<TherapyStatistics, Long> {

    Optional<TherapyStatistics> findByTherapyUserIdAndPeriod(Long therapyUserId, Integer period);
}
