package io.github.kmwh.tradeos_backend.journal.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import io.github.kmwh.tradeos_backend.journal.entity.Journal;
import java.util.List;

@Repository
public interface JournalRepository extends JpaRepository<Journal, Long> {
  List<Journal> findAllByUserIdOrderByEntryTimeDesc(Long userId);
}
