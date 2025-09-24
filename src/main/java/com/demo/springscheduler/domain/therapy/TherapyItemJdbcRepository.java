package com.demo.springscheduler.domain.therapy;

import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class TherapyItemJdbcRepository {

    private static final String SQL = """
            INSERT INTO therapy_item
                (title)
            VALUES (?)
            """;
    private final JdbcTemplate jdbcTemplate;

    public void saveAll(List<String> titles, int batchSize) {
        LocalDateTime now = LocalDateTime.now();
        jdbcTemplate.batchUpdate(
                SQL,
                titles,
                batchSize,
                (ps, title) -> {
                    ps.setString(1, title);
                }
        );
    }
}
