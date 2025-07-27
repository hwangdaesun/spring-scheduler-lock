package com.demo.springscheduler.domain.log;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "THERAPY_BATCH_LOG")
@Getter
@NoArgsConstructor
public class TherapyBatchLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long therapyUserId;

    private Integer year;

    private Integer month;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private Status status;

    @Lob
    private String errorMessage;

    @Column(name = "CREATED_AT")
    private LocalDateTime createdAt;


    @Column(name = "UPDATED_AT")
    private LocalDateTime updatedAt;

    @Builder
    private TherapyBatchLog(
            Long therapyUserId, Integer year, Integer month, LocalDateTime startTime, LocalDateTime endTime,
            Status status, String errorMessage, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.therapyUserId = therapyUserId;
        this.year = year;
        this.month = month;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = status;
        this.errorMessage = errorMessage;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
