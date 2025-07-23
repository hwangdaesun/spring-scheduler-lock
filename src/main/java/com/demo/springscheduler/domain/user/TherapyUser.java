package com.demo.springscheduler.domain.user;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "THERAPY_USER")
@Getter
@NoArgsConstructor
public class TherapyUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "EMAIL", length = 50, unique = true)
    private String email;

    @Builder(access = AccessLevel.PRIVATE)
    private TherapyUser(String email) {
        this.email = email;
    }

    public static TherapyUser create(String email){
        return TherapyUser.builder()
                .email(email)
                .build();
    }
}
