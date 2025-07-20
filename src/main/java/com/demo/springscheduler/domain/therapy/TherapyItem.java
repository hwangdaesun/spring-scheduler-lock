package com.demo.springscheduler.domain.therapy;

import com.demo.springscheduler.domain.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "THERAPY_ITEM")
@Getter
@NoArgsConstructor
public class TherapyItem extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Builder(access = AccessLevel.PRIVATE)
    private TherapyItem(String title) {
        this.title = title;
    }
    public static List<TherapyItem> createAll(List<String> titles) {
        return titles.stream().map(title -> TherapyItem.builder()
                .title(title)
                .build()).toList();
    }

}
