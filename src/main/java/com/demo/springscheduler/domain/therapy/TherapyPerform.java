package com.demo.springscheduler.domain.therapy;

import com.demo.springscheduler.domain.BaseEntity;
import com.demo.springscheduler.domain.user.TherapyUser;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "THERAPY_PERFORM")
@Getter
@NoArgsConstructor
public class TherapyPerform extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "THERAPY_ITEM_ID", nullable = false)
    private TherapyItem therapyItem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "THERAPY_USER_ID", nullable = false)
    private TherapyUser therapyUser;

    @Column(name = "PERFORM_DATE_TIME")
    private LocalDateTime performDateTime;

    /**
     * 민감 정보 추상화
     */

    @Column(name = "SOME_DATA_1")
    private Double someData1;

    @Column(name = "SOME_DATA_2")
    private Double someData2;


    @Builder(access = AccessLevel.PRIVATE)
    private TherapyPerform(TherapyItem therapyItem, TherapyUser therapyUser, LocalDateTime performDateTime,
                           Double someData1,
                           Double someData2) {
        this.therapyItem = therapyItem;
        this.therapyUser = therapyUser;
        this.performDateTime = performDateTime;
        this.someData1 = someData1;
        this.someData2 = someData2;
    }

    public static TherapyPerform perform(TherapyUser therapyUser, TherapyItem therapyItem, Double someData1,
                                         Double someData2) {
        return TherapyPerform.builder()
                .therapyUser(therapyUser)
                .therapyItem(therapyItem)
                .performDateTime(LocalDateTime.now())
                .someData1(someData1)
                .someData2(someData2)
                .build();
    }
}
