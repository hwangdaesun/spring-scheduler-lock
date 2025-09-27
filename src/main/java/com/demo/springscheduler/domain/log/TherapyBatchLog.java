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
import java.util.List;
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

    public static List<TherapyBatchLog> markAllProgress(
            List<Long> therapyUserIds, Integer year, Integer month, LocalDateTime startTime, LocalDateTime endTime) {
        return therapyUserIds.stream()
                .map(therapyUserId -> TherapyBatchLog.builder().therapyUserId(therapyUserId).year(year).month(month)
                        .startTime(startTime).endTime(endTime).status(Status.IN_PROGRESS).createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now()).build()).toList();
    }

    public void markSuccess(LocalDateTime endTime) {
        this.status = Status.SUCCESS;
        this.endTime = endTime;
        this.updatedAt = LocalDateTime.now();
    }

    public void markFail(LocalDateTime endTime, String errorMessage) {
        this.status = Status.FAIL;
        this.endTime = endTime;
        this.errorMessage = errorMessage;
        this.updatedAt = LocalDateTime.now();
    }
}
