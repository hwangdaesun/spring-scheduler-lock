package com.demo.springscheduler.application;

import com.demo.springscheduler.domain.therapy.TherapyItem;
import com.demo.springscheduler.domain.therapy.TherapyItemRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TherapyItemReader {
    private final TherapyItemRepository therapyItemRepository;

    @Transactional(readOnly = true)
    public List<TherapyItem> readAll(List<Long> ids){
        return therapyItemRepository.findAllById(ids);
    }

    @Transactional(readOnly = true)
    public TherapyItem read(Long id){
        return therapyItemRepository.findById(id).orElseThrow(RuntimeException::new);
    }
}
