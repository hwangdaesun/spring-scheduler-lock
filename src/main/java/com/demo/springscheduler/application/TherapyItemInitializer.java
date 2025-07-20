package com.demo.springscheduler.application;

import com.demo.springscheduler.domain.therapy.TherapyItem;
import com.demo.springscheduler.domain.therapy.TherapyItemRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TherapyItemInitializer {

    private final TherapyItemRepository therapyItemRepository;
    @Transactional
    public void setUp(List<String> titles) {
        List<TherapyItem> therapyItems = TherapyItem.createAll(titles);
        therapyItemRepository.saveAll(therapyItems);
    }
}
