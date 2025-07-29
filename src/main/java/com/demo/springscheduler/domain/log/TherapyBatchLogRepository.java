package com.demo.springscheduler.domain.log;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TherapyBatchLogRepository extends JpaRepository<TherapyBatchLog, Long> {
    Optional<TherapyBatchLog> findTherapyBatchLogByTherapyUserIdAndYearAndMonth(Long therapyUserId, Integer year,
                                                                                Integer month);

    List<TherapyBatchLog> findByStatus(Status status);
}
