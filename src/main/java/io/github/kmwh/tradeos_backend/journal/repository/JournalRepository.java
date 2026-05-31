package io.github.kmwh.tradeos_backend.journal.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import io.github.kmwh.tradeos_backend.journal.entity.Journal;
import java.util.List;

@Repository
public interface JournalRepository extends JpaRepository<Journal, Long> {
  Page<Journal> findAllByUserIdOrderByEntryTimeDesc(Long userId, Pageable pageable);

  List<Journal> findAllByUserIdOrderByEntryTimeDesc(Long userId);

  List<Journal> findByUserIdAndIsReportedFalseOrderByEntryTimeAsc(Long userId);

  List<Journal> findByUserIdAndIsReportedTrue(Long userId);

  int countByUserIdAndIsReportedFalse(Long userId);
}
