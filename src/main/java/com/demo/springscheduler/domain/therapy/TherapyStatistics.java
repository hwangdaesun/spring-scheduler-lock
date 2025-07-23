package com.demo.springscheduler.domain.therapy;

import com.demo.springscheduler.domain.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
        name = "THERAPY_STATISTICS",
        uniqueConstraints = @UniqueConstraint(columnNames = {"therapyUserId", "year", "month"})
)
@Getter
@NoArgsConstructor
public class TherapyStatistics extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long therapyUserId;

    /**
     * 민감 데이터 추상화
     */

    private Integer year;
    private Integer month;

    private Double metric;

    @Builder(access = AccessLevel.PRIVATE)
    private TherapyStatistics(Long therapyUserId, Integer year, Integer month, Double metric) {
        this.year = year;
        this.month = month;
        this.therapyUserId = therapyUserId;
        this.metric = metric;
    }

    public static TherapyStatistics create(Long therapyUserId, Integer year, Integer month, Double metric) {
        return TherapyStatistics.builder()
                .therapyUserId(therapyUserId)
                .year(year)
                .month(month)
                .metric(metric)
                .build();
    }

    public void update(Double metric) {
        this.metric = metric;
    }

}
