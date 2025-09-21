package com.demo.springscheduler.application.v1;

import com.demo.springscheduler.application.TherapyItemReader;
import com.demo.springscheduler.application.TherapyPerformStore;
import com.demo.springscheduler.application.TherapyPlanStore;
import com.demo.springscheduler.application.TherapyUserReader;
import com.demo.springscheduler.application.TherapyUserStore;
import com.demo.springscheduler.domain.therapy.TherapyItem;
import com.demo.springscheduler.domain.therapy.TherapyPerform;
import com.demo.springscheduler.domain.therapy.TherapyPlan;
import com.demo.springscheduler.domain.user.TherapyUser;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional(readOnly = true)
    public List<Long> findTargetTherapyUsers() {
        // 특정 조건이 있다고 가정.
        return therapyUserReader.readAll().stream().map(therapyUser -> therapyUser.getId()).toList();
    }

    @Transactional
    public void assignTherapyItemsToUser(String email, List<TherapyItem> therapyItems) {
        TherapyUser therapyUser = therapyUserReader.read(email);
        List<TherapyPlan> therapyPlans = TherapyPlan.assignItemToUser(therapyUser, therapyItems);
        therapyPlanStore.saveAll(therapyPlans);
    }

    @Transactional
    public void performTherapy(String email, Long therapyItemId, Double someData1, Double someData2, Double someData3,
                               Double someData4, Double someData5, Double someData6, Double someData7, Double someData8,
                               Double someData9, Double someData10,
                               LocalDateTime performDateTime) {
        TherapyUser therapyUser = therapyUserReader.read(email);
        // 사용자 therapyPlan 인지 검증
        TherapyItem therapyItem = therapyItemReader.read(therapyItemId);
        TherapyPerform therapyPerform = TherapyPerform.perform(therapyUser, therapyItem, someData1, someData2,
                someData3,
                someData4, someData5, someData6, someData7, someData8, someData9, someData10,
                performDateTime);
        therapyPerformStore.store(therapyPerform);
    }
}
