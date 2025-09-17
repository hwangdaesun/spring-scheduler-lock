package com.demo.springscheduler.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "shedlock")
@Getter
@NoArgsConstructor
public class ShedLock {

    @Id
    private String name;

    @Column(name = "LOCK_UNTIL")
    private LocalDateTime lock_until;

    @Column(name = "LOCKED_AT")
    private LocalDateTime locked_at;

    @Column(name = "LOCKED_BY")
    private String locked_by;
}
