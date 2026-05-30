package io.github.kmwh.tradeos_backend.journal.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import io.github.kmwh.tradeos_backend.journal.entity.Journal;
import java.util.List;

@Repository
public interface JournalRepository extends JpaRepository<Journal, Long> {
  List<Journal> findAllByUserIdOrderByEntryTimeDesc(Long userId);

  // 리포트 발행 대기 중인 일지 조회
  List<Journal> findByUserIdAndIsReportedFalseOrderByEntryTimeAsc(Long userId);

  // 과거 통계용 일지 조회
  List<Journal> findByUserIdAndIsReportedTrue(Long userId);
}
