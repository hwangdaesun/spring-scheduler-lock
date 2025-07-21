package com.demo.springscheduler.domain.user;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TherapyUserRepository extends JpaRepository<TherapyUser, Long> {
    TherapyUser findByEmail(String email);

    List<TherapyUser> findAllByEmailIn(List<String> emails);
}
