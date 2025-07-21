package com.demo.springscheduler.domain.therapy;

import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class TherapyCalculator {
    /**
     * 계산 추상화
     *
     * @param therapyPerforms 특정 기간 동안 치료 수행 결과
     * @return 대단한 결과
     */
    public Double calculate(List<TherapyPerform> therapyPerforms) {

        /**
         * 데이터 추상화
         */
        double metrics1 = therapyPerforms.stream().mapToDouble(therapyPerform -> therapyPerform.getSomeData1()).sum();
        double metrics2 = therapyPerforms.stream().mapToDouble(therapyPerform -> therapyPerform.getSomeData2()).sum();
        return metrics1 + metrics2;
    }
}
