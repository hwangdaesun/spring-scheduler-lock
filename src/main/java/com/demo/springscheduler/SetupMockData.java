package com.demo.springscheduler;

import com.demo.springscheduler.application.TherapyItemInitializer;
import com.demo.springscheduler.application.TherapyItemReader;
import com.demo.springscheduler.application.v1.TherapyPerformCommand;
import com.demo.springscheduler.application.v1.TherapyUserUseCase;
import com.demo.springscheduler.domain.therapy.TherapyItem;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SetupMockData {
    private final TherapyItemInitializer therapyItemInitializer;
    private final TherapyUserUseCase therapyUserUseCase;
    private final TherapyItemReader therapyItemReader;

    public void execute() {
        int batchSize = 1000;
        // 1. 치료 항목 제목 10개 생성
        List<String> titles = IntStream.rangeClosed(1, 10000)
                .mapToObj(i -> "치료 항목 " + i)
                .collect(Collectors.toList());

        // 2. 치료 항목 DB에 저장
        therapyItemInitializer.setUp(titles, batchSize);

        // 3. 전체 치료 항목 조회
        List<Long> allItemIds = LongStream.rangeClosed(1, 10000)
                .boxed()
                .collect(Collectors.toList());

        // 4. 사용자 수
        setUp(allItemIds, batchSize);
    }

    @Transactional
    public void setUp(List<Long> allItemIds, int batchSize) {
        int userCount = 10000000;
        Random random = new Random();

        // 5. 사용자 반복 생성 및 랜덤 치료 항목 할당 + 수행 (batchSize 활용)
        for (int start = 1; start <= userCount; start += batchSize) {
            int end = Math.min(start + batchSize - 1, userCount);

            // 5-1. 이번 배치의 사용자 이메일 리스트 생성
            List<String> emails = new ArrayList<>(end - start + 1);
            for (int i = start; i <= end; i++) {
                emails.add("user" + i + "@example.com");
            }

            // 5-2. 사용자 배치 생성 (JDBC batch)
            therapyUserUseCase.createTherapyUsers(emails, batchSize);

            // 5-3. 배치 내 각 사용자에 대해 아이템 할당 및 수행
            for (String email : emails) {
                // 치료 항목 중 랜덤하게 100개 선택
                List<Long> randomIds = new ArrayList<>(allItemIds);
                Collections.shuffle(randomIds);
                List<TherapyItem> selectedItems = therapyItemReader.readAll(randomIds.subList(0, 100));

                // 할당 (TherapyPlanStore.saveAll 사용으로 내부적으로 묶어서 저장)
                therapyUserUseCase.assignTherapyItemsToUser(email, selectedItems);

                // 각 항목 수행 (배치로 수행)
                List<TherapyPerformCommand> commands = new ArrayList<>(selectedItems.size());
                for (int j = 0; j < selectedItems.size(); j++) {
                    double metricA = 50.0 + random.nextDouble() * 50;
                    double metricB = 100.0 + random.nextDouble() * 100;
                    double metricC = 150.0 + random.nextDouble() * 100;
                    double metricD = 200.0 + random.nextDouble() * 100;
                    double metricE = 300.0 + random.nextDouble() * 100;
                    double metricF = 400.0 + random.nextDouble() * 100;
                    double metricG = 500.0 + random.nextDouble() * 100;
                    double metricH = 600.0 + random.nextDouble() * 100;
                    double metricI = 700.0 + random.nextDouble() * 100;
                    double metricJ = 800.0 + random.nextDouble() * 100;

                    commands.add(
                            TherapyPerformCommand.builder()
                                    .therapyItemId(selectedItems.get(j).getId())
                                    .someData1(metricA)
                                    .someData2(metricB)
                                    .someData3(metricC)
                                    .someData4(metricD)
                                    .someData5(metricE)
                                    .someData6(metricF)
                                    .someData7(metricG)
                                    .someData8(metricH)
                                    .someData9(metricI)
                                    .someData10(metricJ)
                                    .performDateTime(LocalDateTime.now().minusDays(101 - j))
                                    .build()
                    );
                }
                therapyUserUseCase.performTherapies(email, commands, batchSize);
            }
        }
    }
}
