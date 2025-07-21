package com.demo.springscheduler.application;

import com.demo.springscheduler.domain.user.TherapyUser;
import com.demo.springscheduler.domain.user.TherapyUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TherapyUserReader {
    private final TherapyUserRepository therapyUserRepository;

    @Transactional(readOnly = true)
    public TherapyUser read(String email){
        return therapyUserRepository.findByEmail(email);
    }

    @Transactional(readOnly = true)
    public TherapyUser read(Long id) {
        return therapyUserRepository.findById(id).orElseThrow(RuntimeException::new);
    }
}
