package com.demo.springscheduler.domain.log;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class TherapyBatchLogJdbcRepository {

    private final JdbcTemplate jdbcTemplate;

    private static final String INSERT_SQL = """
        INSERT INTO therapy_batch_log
            (therapy_user_id, year, month, start_time, end_time, status, error_message, created_at, updated_at)
        VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;

    private static final String UPDATE_SUCCESS_SQL = """
            UPDATE therapy_batch_log
               SET status = ?, end_time = ?, updated_at = ?
             WHERE therapy_user_id = ? AND year = ? AND month = ?
            """;

    private static final String UPDATE_FAIL_SQL = """
            UPDATE therapy_batch_log
               SET status = ?, end_time = ?, error_message = ?, updated_at = ?
             WHERE therapy_user_id = ? AND year = ? AND month = ?
            """;

    public void batchInsert(List<TherapyBatchLog> logs) {
        jdbcTemplate.batchUpdate(
                INSERT_SQL,
                logs,
                logs.size(),
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

    public void batchMarkSuccess(List<Long> therapyUserIds, Integer year, Integer month, LocalDateTime finishedAt) {
        jdbcTemplate.batchUpdate(
                UPDATE_SUCCESS_SQL,
                therapyUserIds,
                therapyUserIds.size(),
                (ps, therapyUserId) -> {
                    ps.setString(1, Status.SUCCESS.name());
                    ps.setTimestamp(2, Timestamp.valueOf(finishedAt));
                    ps.setTimestamp(3, Timestamp.valueOf(finishedAt)); // updated_at
                    ps.setLong(4, therapyUserId);
                    ps.setInt(5, year);
                    ps.setInt(6, month);
                }
        );
    }

    public void batchMarkFail(List<Long> therapyUserIds, Integer year, Integer month, LocalDateTime finishedAt,
                              String errorMessage) {
        jdbcTemplate.batchUpdate(
                UPDATE_FAIL_SQL,
                therapyUserIds,
                therapyUserIds.size(),
                (ps, therapyUserId) -> {
                    ps.setString(1, Status.FAIL.name());
                    ps.setTimestamp(2, Timestamp.valueOf(finishedAt));
                    ps.setString(3, errorMessage);
                    ps.setTimestamp(4, Timestamp.valueOf(finishedAt)); // updated_at
                    ps.setLong(5, therapyUserId);
                    ps.setInt(6, year);
                    ps.setInt(7, month);
                }
        );
    }
}

