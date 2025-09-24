package com.demo.springscheduler.application.v1;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TherapyPerformCommand {
    private Long therapyItemId;
    private Double someData1;
    private Double someData2;
    private Double someData3;
    private Double someData4;
    private Double someData5;
    private Double someData6;
    private Double someData7;
    private Double someData8;
    private Double someData9;
    private Double someData10;
    private LocalDateTime performDateTime;
}
