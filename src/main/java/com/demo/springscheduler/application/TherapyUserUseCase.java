package com.demo.springscheduler.application;

import com.demo.springscheduler.domain.therapy.TherapyItem;
import com.demo.springscheduler.domain.therapy.TherapyPerform;
import com.demo.springscheduler.domain.therapy.TherapyPlan;
import com.demo.springscheduler.domain.user.TherapyUser;
import jakarta.transaction.Transactional;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TherapyUserUseCase {
    private final TherapyUserReader therapyUserReader;
    private final TherapyUserStore therapyUserStore;
    private final TherapyPlanStore therapyPlanStore;

    @Transactional
    public void createTherapyUser(String email) {
        TherapyUser therapyUser = TherapyUser.create(email);
        therapyUserStore.store(therapyUser);
    }

    @Transactional
    public void assignTherapyItemsToUser(String email, List<TherapyItem> therapyItems) {
        TherapyUser therapyUser = therapyUserReader.read(email);
        List<TherapyPlan> therapyPlans = TherapyPlan.assignItemToUser(therapyUser, therapyItems);
        therapyPlanStore.saveAll(therapyPlans);
    }
}
