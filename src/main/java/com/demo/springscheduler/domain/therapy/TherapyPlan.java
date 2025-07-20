package com.demo.springscheduler.domain.therapy;

import com.demo.springscheduler.domain.user.TherapyUser;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "THERAPY_PLAN")
@Getter
@NoArgsConstructor
public class TherapyPlan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "THERAPY_ITEM_ID", nullable = false)
    private TherapyItem therapyItem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "THERAPY_USER_ID", nullable = false)
    private TherapyUser therapyUser;

    @Builder(access = AccessLevel.PRIVATE)
    private TherapyPlan(TherapyItem therapyItem, TherapyUser therapyUser) {
        this.therapyItem = therapyItem;
        this.therapyUser = therapyUser;
    }

    public static List<TherapyPlan> assignItemToUser(TherapyUser therapyUser, List<TherapyItem> therapyItems){
        return therapyItems.stream().map(item -> TherapyPlan.builder()
                .therapyUser(therapyUser)
                .therapyItem(item)
                .build()).toList();
    }


}
