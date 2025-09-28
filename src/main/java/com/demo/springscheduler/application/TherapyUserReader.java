package com.demo.springscheduler.application;

import com.demo.springscheduler.domain.user.TherapyUser;
import com.demo.springscheduler.domain.user.TherapyUserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TherapyUserReader {
    private final TherapyUserRepository therapyUserRepository;

    @Transactional(readOnly = true)
    public TherapyUser read(String email) {
        return therapyUserRepository.findByEmail(email);
    }

    @Transactional(readOnly = true)
    public TherapyUser read(Long id) {
        return therapyUserRepository.findById(id).orElseThrow(RuntimeException::new);
    }

    @Transactional(readOnly = true)
    public List<TherapyUser> readAll(List<String> emails) {
        return therapyUserRepository.findAllByEmailIn(emails);
    }

    @Transactional(readOnly = true)
    public List<TherapyUser> readAllByIds(List<Long> ids) {
        return therapyUserRepository.findAllById(ids);
    }

    @Transactional(readOnly = true)
    public List<TherapyUser> readAll() {
        return therapyUserRepository.findAll();
    }
}
