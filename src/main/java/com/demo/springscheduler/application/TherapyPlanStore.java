package com.demo.springscheduler.application;

import com.demo.springscheduler.domain.therapy.TherapyPlan;
import com.demo.springscheduler.domain.therapy.TherapyPlanRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TherapyPlanStore {

    private final TherapyPlanRepository therapyPlanRepository;
    @Transactional
    public void saveAll(List<TherapyPlan> therapyPlans) {
        therapyPlanRepository.saveAll(therapyPlans);
    }
}
