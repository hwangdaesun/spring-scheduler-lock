package com.demo.springscheduler.application;

import com.demo.springscheduler.domain.user.TherapyUser;
import com.demo.springscheduler.domain.user.TherapyUserJdbcRepository;
import com.demo.springscheduler.domain.user.TherapyUserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TherapyUserStore {
    private final TherapyUserRepository therapyUserRepository;
    private final TherapyUserJdbcRepository therapyUserJdbcRepository;

    @Transactional
    public void store(TherapyUser therapyUser) {
        therapyUserRepository.save(therapyUser);
    }

    @Transactional
    public void storeAll(List<String> emails, int batchSize) {
        therapyUserJdbcRepository.batchInsertByEmails(emails, batchSize);
    }
}
