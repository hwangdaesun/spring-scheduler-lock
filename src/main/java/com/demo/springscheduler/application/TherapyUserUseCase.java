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
    private final TherapyItemReader therapyItemReader;
    private final TherapyUserReader therapyUserReader;
    private final TherapyUserStore therapyUserStore;
    private final TherapyPlanStore therapyPlanStore;
    private final TherapyPerformStore therapyPerformStore;

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

    @Transactional
    public void performTherapy(String email, Long therapyItemId, Float someData1, Float someData2){
        TherapyUser therapyUser = therapyUserReader.read(email);
        // 사용자 therapyPlan 인지 검증
        TherapyItem therapyItem = therapyItemReader.read(therapyItemId);
        TherapyPerform therapyPerform = TherapyPerform.perform(therapyUser, therapyItem, someData1, someData2);
        therapyPerformStore.store(therapyPerform);
    }
}
