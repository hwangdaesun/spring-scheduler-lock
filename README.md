# 배치 시스템에서 데이터 정합성과 장애 복구 체계 확보

## 목적

분산 환경에서 데이터 정합성을 보장하고, 장애 발생 시에도 복구가 가능한 안정적인 배치 시스템을 구축하는 것을 목표로 한다.

## 기능 개요

치료 수행 데이터를 바탕으로 사용자의 월간 치료 통계를 매일 정기적으로 집계하고 저장하는 배치 기능을 제공한다.

## 시스템 구성도 (Flow)

1. 사용자 치료 수행  
   ↓
2. 치료 데이터 저장 (`therapy_perform`) 테이블에 저장  
   ↓
3. 스케줄러 동작 (매일 자정 실행)  
   ↓
4. 사용자 필터링  
   ↓
5. 사용자별 통계 집계 수행

   - 사용자별 치료 수행 데이터를 조회
   - 통계 지표 계산
   - 결과를 therapy_statistics 테이블에 저장하거나 갱신
   - 집계 상태를 therapy_batch_log 테이블에 성공(SUCCESS), 진행 중(IN_PROGRESS), 실패(FAIL) 로 기록

   ↓
6. 통계 데이터 제공 (API에서 사용)

## 기술적 고민

### 분산 환경에서 데이터 정합성 보장

배치 작업은 여러 사용자에 대해 반복적으로 수행되기 때문에, 하나의 작업이 실패하더라도 전체 작업이 롤백되지 않도록 트랜잭션 범위를 작게 관리해야한다.
이를 위해 스케줄러 전체에 트랜잭션을 적용하는 대신, 개별 집계 메서드 내부에만 트랜잭션을 적용하여 부분 실패가 전체에 영향을 주지 않도록 구성하였다.

그리고, 분산 환경에서 동일한 사용자의 통계를 동시에 계산하려 할 경우, therapyUserId + year + month 복합 Unique 제약 조건을 위반하는 충돌이 발생할 수 있다.
이를 방지하기 위해 트랜잭션이 시작되기 전에 Named Lock을 선점하여, 하나의 인스턴스만 해당 사용자에 대한 집계를 수행하도록 제어하고 있다.

마지막으로, 집계 메서드 외부에서 Named Lock을 휙득하여 트랜잭션이 시작되기 전에 Named Lock을 획득함으로써, Repeatable Read 격리 수준에서 발생할 수 있는 데이터 정합성 문제를 방지했다.

```java
@Scheduled(cron = "0 0 0 * * *")
public void aggregateDaily(){

        List<Long> targetTherapyUserIds=therapyUserUseCase.findTargetTherapyUsers();

        // 특정 기간 설정 (오늘 ~ 달의 마지막 날)

        // 오늘
        LocalDateTime startDateTime=LocalDateTime.now();

        // 이번 달의 마지막 날
        YearMonth currentMonth=YearMonth.from(startDateTime);
        LocalDate lastDayOfMonth=currentMonth.atEndOfMonth();
        LocalDateTime endDateTime=lastDayOfMonth.atTime(LocalTime.MAX);

        for(Long therapyUserId:targetTherapyUserIds){
        log.info("[Therapy Statistics Batch] 사용자 ID {} - 통계 집계 시작",therapyUserId);
        namedLockRepository.acquireLock("batch-lock");
        try{
        statsUseCase.aggregateTherapyStatics(therapyUserId,startDateTime,endDateTime);
        log.info("[Therapy Statistics Batch] 사용자 ID {} - 통계 집계 완료",therapyUserId);
        }catch(Exception e){
        log.error("[Therapy Statistics Batch] 사용자 ID {} - 통계 집계 실패: {}",therapyUserId,e.getMessage(),e);
        }finally{
        namedLockRepository.releaseLock("batch-lock");
        }
        }

        }
```

### 장애 발생 시에도 복구가 가능한 안정적인 배치 시스템

배치 시스템은 네트워크 장애, 서비스 다운 등 다양한 원인으로 인해 실패할 수 있다. 이를 대비하여 (`therapy_user_id + year + month`) 단위로 집계 실행 결과를 기록하는
therapy_batch_log 테이블을 설계하였다.

이 테이블은 사용자 ID, 연월, 처리 시간, 실패 원인 등을 포함하며, 배치 상태를 다음 세 가지로 구분해 관리한다:

- IN_PROGRESS : 집계 시작 시점에 초기 기록
- SUCCESS : 집계 성공 시 상태 및 완료 시간 업데이트
- FAIL : 예외 발생 시 실패 상태로 변경하고 오류 메시지 저장

이 구조를 통해 배치 실패 시에도 수동 복구 API로 재처리가 가능하며, 실패 항목은 별도 스케줄러에 의해 자동 재시도할 수 있다.

수동 복구 API)

```java

@PostMapping("/v1/therapy-statistics/recover")
public ResponseEntity<Void> recoverTherapyStatistics(
@RequestBody RecoverTherapyStatistics recoverTherapyStatistics){
        statsUseCase.recoverTherapyStatistics(recoverTherapyStatistics.therapyUserId(),
        recoverTherapyStatistics.yearMonth(),recoverTherapyStatistics.startDateTime(),
        recoverTherapyStatistics.endDateTime());
        return new ResponseEntity<>(HttpStatus.CREATED);
        }

```

실패 항목 재처리 스케줄러)

```java
@Scheduled(cron = "0 0 5 * * *") // 매일 새벽 5시에 실행
public void recover(){
        List<TherapyBatchLog> failedLogs=therapyBatchLogUseCase.findFailedLogs(Status.FAIL);

        for(TherapyBatchLog batchLog:failedLogs){
        Long userId=batchLog.getTherapyUserId();
        log.info("[Therapy Statistics Batch] 사용자 ID {} - 통계 집계 시작",userId);
        YearMonth yearMonth=YearMonth.of(batchLog.getYear(),batchLog.getMonth());
        LocalDateTime start=batchLog.getStartTime();
        LocalDateTime end=batchLog.getEndTime();

        namedLockRepository.acquireLock("batch-lock");

        try{
        therapyStatisticsUseCase.recoverTherapyStatistics(userId,yearMonth,start,end);
        batchLog.markSuccess(LocalDateTime.now());
        log.info("[Therapy Statistics Batch] 사용자 ID {} - 통계 집계 완료",userId);
        }catch(Exception e){
        batchLog.markFail(LocalDateTime.now(),e.getMessage());
        log.error("[Therapy Statistics Batch] 사용자 ID {} - 통계 집계 실패: {}",userId,e.getMessage(),e);
        }finally{
        namedLockRepository.releaseLock("batch-lock");
        }
        }
        }
```
