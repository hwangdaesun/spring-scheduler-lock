package com.demo.springscheduler.application;

import com.demo.springscheduler.domain.therapy.TherapyPerform;
import com.demo.springscheduler.domain.therapy.TherapyPerformRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TherapyPerformStore {
    private final TherapyPerformRepository therapyPerformRepository;

    @Transactional
    public void store(TherapyPerform therapyPerform){
        therapyPerformRepository.save(therapyPerform);
    }

}
