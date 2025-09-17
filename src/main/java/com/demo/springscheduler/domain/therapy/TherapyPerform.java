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

    @Column(name = "SOME_DATA_3")
    private Double someData3;

    @Column(name = "SOME_DATA_4")
    private Double someData4;

    @Column(name = "SOME_DATA_5")
    private Double someData5;

    @Column(name = "SOME_DATA_6")
    private Double someData6;

    @Column(name = "SOME_DATA_7")
    private Double someData7;

    @Column(name = "SOME_DATA_8")
    private Double someData8;

    @Column(name = "SOME_DATA_9")
    private Double someData9;

    @Column(name = "SOME_DATA_10")
    private Double someData10;


    @Builder(access = AccessLevel.PRIVATE)
    private TherapyPerform(TherapyItem therapyItem, TherapyUser therapyUser, LocalDateTime performDateTime,
                           Double someData1,
                           Double someData2, Double someData3, Double someData4, Double someData5, Double someData6,
                           Double someData7, Double someData8, Double someData9, Double someData10) {
        this.therapyItem = therapyItem;
        this.therapyUser = therapyUser;
        this.performDateTime = performDateTime;
        this.someData1 = someData1;
        this.someData2 = someData2;
        this.someData3 = someData3;
        this.someData4 = someData4;
        this.someData5 = someData5;
        this.someData6 = someData6;
        this.someData7 = someData7;
        this.someData8 = someData8;
        this.someData9 = someData9;
        this.someData10 = someData10;
    }

    public static TherapyPerform perform(TherapyUser therapyUser, TherapyItem therapyItem, Double someData1,
                                         Double someData2, Double someData3, Double someData4, Double someData5,
                                         Double someData6, Double someData7, Double someData8, Double someData9,
                                         Double someData10, LocalDateTime performDateTime) {
        return TherapyPerform.builder()
                .therapyUser(therapyUser)
                .therapyItem(therapyItem)
                .performDateTime(performDateTime)
                .someData1(someData1)
                .someData2(someData2)
                .someData3(someData3)
                .someData4(someData4)
                .someData5(someData5)
                .someData6(someData6)
                .someData7(someData7)
                .someData8(someData8)
                .someData9(someData9)
                .someData10(someData10)
                .build();
    }
}
