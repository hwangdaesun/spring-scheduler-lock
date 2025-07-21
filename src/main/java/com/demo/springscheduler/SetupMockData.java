package com.demo.springscheduler;

import com.demo.springscheduler.application.TherapyItemInitializer;
import com.demo.springscheduler.application.TherapyItemReader;
import com.demo.springscheduler.application.TherapyUserUseCase;
import com.demo.springscheduler.domain.therapy.TherapyItem;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
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
    public void execute() {
        // 1. 치료 항목 제목 10개 생성
        List<String> titles = IntStream.rangeClosed(1, 10)
                .mapToObj(i -> "치료 항목 " + i)
                .collect(Collectors.toList());

        // 2. 치료 항목 DB에 저장
        therapyItemInitializer.setUp(titles);

        // 3. 전체 치료 항목 조회 (ID 1~10이라고 가정)
        List<Long> allItemIds = LongStream.rangeClosed(1, 10)
                .boxed()
                .collect(Collectors.toList());

        // 4. 사용자 수
        int userCount = 10;
        Random random = new Random();

        // 5. 사용자 반복 생성 및 랜덤 치료 항목 할당 + 수행
        for (int i = 1; i <= userCount; i++) {
            String email = "user" + i + "@example.com";

            // 사용자 생성
            therapyUserUseCase.createTherapyUser(email);

            // 치료 항목 중 랜덤하게 2개 선택
            List<Long> randomIds = new ArrayList<>(allItemIds);
            Collections.shuffle(randomIds);
            List<TherapyItem> selectedItems = therapyItemReader.readAll(randomIds.subList(0, 2));

            // 할당
            therapyUserUseCase.assignTherapyItemsToUser(email, selectedItems);

            // 각 항목 수행
            for (TherapyItem item : selectedItems) {
                double metricA = 50.0 + random.nextDouble() * 50;
                double metricB = 100.0 + random.nextDouble() * 100;

                therapyUserUseCase.performTherapy(email, item.getId(), metricA, metricB);
            }
        }
    }
}
