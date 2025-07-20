package com.demo.springscheduler.domain.user;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TherapyUserRepository extends JpaRepository<TherapyUser, Long> {
    TherapyUser findByEmail(String email);
}
