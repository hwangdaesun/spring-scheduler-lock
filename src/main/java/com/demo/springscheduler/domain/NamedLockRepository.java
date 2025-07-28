package com.demo.springscheduler.domain;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class NamedLockRepository {

    private final JdbcTemplate jdbcTemplate;

    public boolean acquireLock(String lockName) {
        String sql = "SELECT GET_LOCK(?, 1)";
        Boolean result = jdbcTemplate.queryForObject(sql, Boolean.class, lockName);
        return Boolean.TRUE.equals(result);
    }

    public void releaseLock(String lockName) {
        jdbcTemplate.update("SELECT RELEASE_LOCK(?)", lockName);
    }
}
