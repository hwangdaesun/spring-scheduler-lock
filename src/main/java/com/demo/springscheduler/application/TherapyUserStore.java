package com.demo.springscheduler.application;

import com.demo.springscheduler.domain.user.TherapyUser;
import com.demo.springscheduler.domain.user.TherapyUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TherapyUserStore {
    private final TherapyUserRepository therapyUserRepository;

    @Transactional
    public void store(TherapyUser therapyUser) {
        therapyUserRepository.save(therapyUser);
    }
}
