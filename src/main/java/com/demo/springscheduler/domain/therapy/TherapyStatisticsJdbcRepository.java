package com.demo.springscheduler.domain.therapy;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class TherapyStatisticsJdbcRepository {

    private static final String INSERT_SQL = """
            INSERT INTO therapy_statistics
                (therapy_user_id, year, month, metric, created_at, updated_at)
            VALUES (?, ?, ?, ?, ?, ?)
            """;
    private static final String UPDATE_SQL = """
            UPDATE therapy_statistics
               SET metric = ?, updated_at = ?
             WHERE therapy_user_id = ? AND year = ? AND month = ?
            """;
    private final JdbcTemplate jdbcTemplate;

    public void batchInsert(List<TherapyStatistics> stats) {
        if (stats == null || stats.isEmpty()) {
            return;
        }
        LocalDateTime now = LocalDateTime.now();
        jdbcTemplate.batchUpdate(
                INSERT_SQL,
                stats,
                stats.size(),
                (ps, s) -> {
                    ps.setLong(1, s.getTherapyUserId());
                    ps.setInt(2, s.getYear());
                    ps.setInt(3, s.getMonth());
                    if (s.getMetric() != null) {
                        ps.setDouble(4, s.getMetric());
                    } else {
                        ps.setObject(4, null);
                    }
                    ps.setTimestamp(5, Timestamp.valueOf(now));
                    ps.setTimestamp(6, Timestamp.valueOf(now));
                }
        );
    }

    public void batchUpdate(List<TherapyStatistics> stats) {
        if (stats == null || stats.isEmpty()) {
            return;
        }
        LocalDateTime now = LocalDateTime.now();
        jdbcTemplate.batchUpdate(
                UPDATE_SQL,
                stats,
                stats.size(),
                (ps, s) -> {
                    if (s.getMetric() != null) {
                        ps.setDouble(1, s.getMetric());
                    } else {
                        ps.setObject(1, null);
                    }
                    ps.setTimestamp(2, Timestamp.valueOf(now));
                    ps.setLong(3, s.getTherapyUserId());
                    ps.setInt(4, s.getYear());
                    ps.setInt(5, s.getMonth());
                }
        );
    }
}
