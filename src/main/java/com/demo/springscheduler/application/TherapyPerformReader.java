package com.demo.springscheduler.application;

import com.demo.springscheduler.domain.therapy.TherapyPerform;
import com.demo.springscheduler.domain.therapy.TherapyPerformRepository;
import com.demo.springscheduler.domain.user.TherapyUser;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TherapyPerformReader {
    private final TherapyPerformRepository therapyPerformRepository;

    @Transactional(readOnly = true)
    public List<TherapyPerform> read(TherapyUser therapyUser, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        return therapyPerformRepository.findTherapyPerformsByTherapyUserAndPerformDateTimeBetween(therapyUser,
                startDateTime, endDateTime);
    }
}
