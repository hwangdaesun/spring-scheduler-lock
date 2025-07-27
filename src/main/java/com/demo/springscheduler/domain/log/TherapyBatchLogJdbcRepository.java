package com.demo.springscheduler.domain.log;

import java.sql.Timestamp;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class TherapyBatchLogJdbcRepository {

    private final JdbcTemplate jdbcTemplate;

    private static final String SQL = """
        INSERT INTO therapy_batch_log
            (therapy_user_id, year, month, start_time, end_time, status, error_message, created_at, updated_at)
        VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;

    public void batchInsert(List<TherapyBatchLog> logs, int batchSize) {
        jdbcTemplate.batchUpdate(
                SQL,
                logs,
                batchSize,
                (ps, log) -> {
                    ps.setLong(1, log.getTherapyUserId());
                    ps.setInt(2, log.getYear());
                    ps.setInt(3, log.getMonth());
                    ps.setTimestamp(4, Timestamp.valueOf(log.getStartTime()));
                    ps.setTimestamp(5, log.getEndTime() != null ? Timestamp.valueOf(log.getEndTime()) : null);
                    ps.setString(6, log.getStatus().name());
                    ps.setString(7, log.getErrorMessage());
                    ps.setTimestamp(8, Timestamp.valueOf(log.getCreatedAt()));
                    ps.setTimestamp(9, Timestamp.valueOf(log.getUpdatedAt()));
                }
        );
    }
}

