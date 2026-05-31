package io.github.kmwh.tradeos_backend.journal.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import io.github.kmwh.tradeos_backend.journal.entity.Journal;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface JournalRepository extends JpaRepository<Journal, Long> {
  Page<Journal> findAllByUserIdOrderByEntryTimeDesc(Long userId, Pageable pageable);

  List<Journal> findAllByUserIdOrderByEntryTimeDesc(Long userId);

  List<Journal> findByUserIdAndIsReportedFalseOrderByEntryTimeAsc(Long userId);

  int countByUserIdAndIsReportedFalse(Long userId);

  long countByUserId(Long userId);

  // 차트용 데이터 50개만 DB에서 잘라서 가져오기
  List<Journal> findTop50ByUserIdOrderByEntryTimeDesc(Long userId);

  // 리포트 과거 누적 성과용 쿼리
  @Query("SELECT COUNT(j) FROM Journal j WHERE j.user.id = :userId AND j.isReported = true")
  long countHistoricalTotal(@Param("userId") Long userId);

  @Query("SELECT COUNT(j) FROM Journal j WHERE j.user.id = :userId AND j.isReported = true AND j.realizedPnl > 0")
  long countHistoricalWins(@Param("userId") Long userId);

  @Query("SELECT AVG(j.realizedPnl) FROM Journal j WHERE j.user.id = :userId AND j.isReported = true")
  Double getHistoricalAvgPnl(@Param("userId") Long userId);

  // 대시보드 통계용 쿼리
  @Query("SELECT MIN(j.entryTime) FROM Journal j WHERE j.user.id = :userId")
  LocalDateTime getEarliestTradeTime(@Param("userId") Long userId);

  @Query("SELECT MAX(j.entryTime) FROM Journal j WHERE j.user.id = :userId")
  LocalDateTime getLatestTradeTime(@Param("userId") Long userId);

  @Query("SELECT COUNT(j) FROM Journal j WHERE j.user.id = :userId AND j.realizedPnl > 0")
  long countTotalWins(@Param("userId") Long userId);

  @Query("SELECT AVG(j.leverage) FROM Journal j WHERE j.user.id = :userId")
  Double getAverageLeverage(@Param("userId") Long userId);

  @Query("SELECT AVG(j.durationSeconds) FROM Journal j WHERE j.user.id = :userId")
  Double getAverageDuration(@Param("userId") Long userId);

  @Query("SELECT COUNT(j) FROM Journal j WHERE j.user.id = :userId AND j.marketHmmScore > 60")
  long countTrendTrades(@Param("userId") Long userId);

  @Query("SELECT COUNT(j) FROM Journal j WHERE j.user.id = :userId AND j.marketHmmScore > 60 AND j.realizedPnl > 0")
  long countTrendWins(@Param("userId") Long userId);

  @Query("SELECT COUNT(j) FROM Journal j WHERE j.user.id = :userId AND j.marketHmmScore <= 60")
  long countNonTrendTrades(@Param("userId") Long userId);

  @Query("SELECT COUNT(j) FROM Journal j WHERE j.user.id = :userId AND j.marketHmmScore <= 60 AND j.realizedPnl > 0")
  long countNonTrendWins(@Param("userId") Long userId);
}
