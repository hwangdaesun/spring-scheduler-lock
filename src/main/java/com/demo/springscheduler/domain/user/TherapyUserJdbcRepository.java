package com.demo.springscheduler.domain.user;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class TherapyUserJdbcRepository {

    private static final String SQL = """
            INSERT INTO therapy_user
                (email)
            VALUES (?)
            """;
    private final JdbcTemplate jdbcTemplate;

    public void batchInsertByEmails(List<String> emails, int batchSize) {
        jdbcTemplate.batchUpdate(
                SQL,
                emails,
                batchSize,
                (ps, email) -> ps.setString(1, email)
        );
    }
}
