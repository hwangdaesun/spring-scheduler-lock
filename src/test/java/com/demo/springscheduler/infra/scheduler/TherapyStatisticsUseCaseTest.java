package com.demo.springscheduler.infra.scheduler;

import static org.assertj.core.api.Assertions.assertThat;

import com.demo.springscheduler.DatabaseClearExtension;
import com.demo.springscheduler.SetupMockData;
import com.demo.springscheduler.application.TherapyPerformReader;
import com.demo.springscheduler.application.TherapyStatisticsReader;
import com.demo.springscheduler.application.TherapyStatisticsUseCase;
import com.demo.springscheduler.application.TherapyUserReader;
import com.demo.springscheduler.domain.NamedLockRepository;
import com.demo.springscheduler.domain.therapy.TherapyCalculator;
import com.demo.springscheduler.domain.user.TherapyUser;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

@SpringBootTest
@ExtendWith(DatabaseClearExtension.class)
@AutoConfigureTestDatabase(replace = Replace.NONE)
class TherapyStatisticsUseCaseTest {

    @Autowired
    PlatformTransactionManager transactionManager;
    @Autowired
    private SetupMockData setupMockData;
    @Autowired
    private TherapyStatisticsUseCase therapyStatisticsUsecase;
    @Autowired
    private TherapyUserReader therapyUserReader;
    @Autowired
    private TherapyStatisticsReader therapyStaticReader;
    @Autowired
    private TherapyPerformReader therapyPerformReader;
    @Autowired
    private TherapyCalculator therapyCalculator;

    @Autowired
    private NamedLockRepository namedLockRepository;

    @BeforeEach
    void beforeEach() {
        setupMockData.execute();
    }

    @Test
    void concurrent_aggregation_should_fail_without_lock() throws InterruptedException {
        TherapyUser therapyUser = therapyUserReader.read(1L);
        LocalDateTime start = LocalDateTime.now().minusDays(10);
        LocalDateTime end = LocalDateTime.now().plusDays(10);

        int threadCount = 3;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        AtomicInteger exceptionCount = new AtomicInteger(0);

        for(int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                TransactionStatus status = transactionManager.getTransaction(new DefaultTransactionDefinition());
                try {
                    therapyStatisticsUsecase.aggregateTherapyStatics(
                            therapyUser.getId(), YearMonth.from(start), start, end);
                    transactionManager.commit(status);
                } catch(DataIntegrityViolationException e) {
                    exceptionCount.incrementAndGet();
                    transactionManager.rollback(status);
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executor.shutdown();

        System.out.println("예외 발생 횟수: " + exceptionCount.get());

        // 예외가 1개 이상 발생해야 테스트 통과
        assertThat(exceptionCount.get()).isGreaterThan(0);
    }

    @Test
    void concurrent_aggregation_should_success_with_lock() throws InterruptedException {
        TherapyUser therapyUser = therapyUserReader.read(1L);
        LocalDateTime start = LocalDateTime.now().minusDays(10);
        LocalDateTime end = LocalDateTime.now().plusDays(10);

        int threadCount = 3;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        AtomicInteger exceptionCount = new AtomicInteger(0);

        for(int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                namedLockRepository.acquireLock("batch-lock");
                TransactionStatus status = transactionManager.getTransaction(new DefaultTransactionDefinition());
                try {
                    therapyStatisticsUsecase.aggregateTherapyStatics(
                            therapyUser.getId(), YearMonth.from(start), start, end);
                    transactionManager.commit(status);
                } catch(DataIntegrityViolationException e) {
                    exceptionCount.incrementAndGet();
                    transactionManager.rollback(status);
                } finally {
                    latch.countDown();
                    namedLockRepository.releaseLock("batch-lock");
                }
            });
        }

        latch.await();
        executor.shutdown();

        System.out.println("예외 발생 횟수: " + exceptionCount.get());

        // 예외가 발생하지 않아야 통과
        assertThat(exceptionCount.get()).isEqualTo(0);
    }

}
