package com.demo.springscheduler.application;

import com.demo.springscheduler.domain.therapy.TherapyItemJdbcRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TherapyItemInitializer {

    private final TherapyItemJdbcRepository therapyItemJdbcRepository;

    @Transactional
    public void setUp(List<String> titles, int batchSize) {
        therapyItemJdbcRepository.saveAll(titles, batchSize);
    }
}
