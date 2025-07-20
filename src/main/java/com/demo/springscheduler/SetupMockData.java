package com.demo.springscheduler;

import com.demo.springscheduler.application.TherapyItemInitializer;
import com.demo.springscheduler.application.TherapyItemReader;
import com.demo.springscheduler.application.TherapyUserUseCase;
import com.demo.springscheduler.domain.therapy.TherapyItem;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class SetupMockData {
    private final TherapyItemInitializer therapyItemInitializer;
    private final TherapyUserUseCase therapyUserUseCase;
    private final TherapyItemReader therapyItemReader;
    @Transactional
    public void execute(){
        // 샘플 데이터
        String email = "hds650588@gmail.com";
        List<String> titles = List.of("치료 항목 1", "치료 항목 2", "치료 항목 3", "치료 항목 4", "치료 항목 5");

        // 사용자 생성
        therapyUserUseCase.createTherapyUser(email);
        // 샘플 치료 아이템 생성
        therapyItemInitializer.setUp(titles);

        // 샘플 치료 아이템 조회
        List<TherapyItem> therapyItems = therapyItemReader.readAll(List.of(1L, 2L));

        // 사용자에게 샘플 치료 아이템 할당
        therapyUserUseCase.assignTherapyItemsToUser(email, therapyItems);

        // 사용자가 치료 수행
        therapyUserUseCase.performTherapy(email, 1L, 2F, 4F);
    }
}
