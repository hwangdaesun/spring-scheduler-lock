package com.demo.springscheduler.domain.therapy;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class TherapyPerformJdbcRepository {

    private static final String SQL = """
            INSERT INTO therapy_perform
                (therapy_item_id, therapy_user_id, perform_date_time,
                 some_data_1, some_data_2, some_data_3, some_data_4, some_data_5,
                 some_data_6, some_data_7, some_data_8, some_data_9, some_data_10,
                 created_at, updated_at)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;
    private final JdbcTemplate jdbcTemplate;

    public void batchInsert(List<TherapyPerform> performs, int batchSize) {
        final LocalDateTime now = LocalDateTime.now();
        jdbcTemplate.batchUpdate(
                SQL,
                performs,
                batchSize,
                (ps, p) -> {
                    ps.setLong(1, p.getTherapyItem().getId());
                    ps.setLong(2, p.getTherapyUser().getId());
                    ps.setTimestamp(3,
                            p.getPerformDateTime() != null ? Timestamp.valueOf(p.getPerformDateTime()) : null);

                    if (p.getSomeData1() != null) {
                        ps.setDouble(4, p.getSomeData1());
                    } else {
                        ps.setObject(4, null);
                    }
                    if (p.getSomeData2() != null) {
                        ps.setDouble(5, p.getSomeData2());
                    } else {
                        ps.setObject(5, null);
                    }
                    if (p.getSomeData3() != null) {
                        ps.setDouble(6, p.getSomeData3());
                    } else {
                        ps.setObject(6, null);
                    }
                    if (p.getSomeData4() != null) {
                        ps.setDouble(7, p.getSomeData4());
                    } else {
                        ps.setObject(7, null);
                    }
                    if (p.getSomeData5() != null) {
                        ps.setDouble(8, p.getSomeData5());
                    } else {
                        ps.setObject(8, null);
                    }
                    if (p.getSomeData6() != null) {
                        ps.setDouble(9, p.getSomeData6());
                    } else {
                        ps.setObject(9, null);
                    }
                    if (p.getSomeData7() != null) {
                        ps.setDouble(10, p.getSomeData7());
                    } else {
                        ps.setObject(10, null);
                    }
                    if (p.getSomeData8() != null) {
                        ps.setDouble(11, p.getSomeData8());
                    } else {
                        ps.setObject(11, null);
                    }
                    if (p.getSomeData9() != null) {
                        ps.setDouble(12, p.getSomeData9());
                    } else {
                        ps.setObject(12, null);
                    }
                    if (p.getSomeData10() != null) {
                        ps.setDouble(13, p.getSomeData10());
                    } else {
                        ps.setObject(13, null);
                    }

                    ps.setTimestamp(14, Timestamp.valueOf(now));
                    ps.setTimestamp(15, Timestamp.valueOf(now));
                }
        );
    }
}
