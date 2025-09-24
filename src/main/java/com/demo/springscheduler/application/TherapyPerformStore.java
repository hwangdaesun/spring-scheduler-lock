package com.demo.springscheduler.application;

import com.demo.springscheduler.domain.therapy.TherapyPerform;
import com.demo.springscheduler.domain.therapy.TherapyPerformJdbcRepository;
import com.demo.springscheduler.domain.therapy.TherapyPerformRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TherapyPerformStore {
    private final TherapyPerformRepository therapyPerformRepository;
    private final TherapyPerformJdbcRepository therapyPerformJdbcRepository;

    @Transactional
    public void store(TherapyPerform therapyPerform){
        therapyPerformRepository.save(therapyPerform);
    }

    @Transactional
    public void storeAll(List<TherapyPerform> performs, int batchSize) {
        therapyPerformJdbcRepository.batchInsert(performs, batchSize);
    }
}
