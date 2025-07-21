package com.demo.springscheduler.domain.therapy;

import com.demo.springscheduler.domain.user.TherapyUser;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TherapyPerformRepository extends JpaRepository<TherapyPerform, Long> {

    List<TherapyPerform> findTherapyPerformsByTherapyUserAndPerformDateTimeBetween(TherapyUser therapyUser,
                                                                                   LocalDateTime startDateTime,
                                                                                   LocalDateTime endDateTime);
}
