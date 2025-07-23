package com.demo.springscheduler.application;

import com.demo.springscheduler.domain.therapy.TherapyStatistics;
import com.demo.springscheduler.domain.therapy.TherapyStatisticsRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TherapyStatisticsReader {

    private final TherapyStatisticsRepository therapyStatisticsRepository;

    @Transactional(readOnly = true)
    public List<TherapyStatistics> readAll() {
        return therapyStatisticsRepository.findAll();
    }
}
