package io.github.kmwh.tradeos_backend.quant.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import io.github.kmwh.tradeos_backend.quant.entity.HmmHistory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface HmmHistoryRepository extends JpaRepository<HmmHistory, LocalDateTime> {
  Optional<HmmHistory> findTopByTimestampLessThanEqualOrderByTimestampDesc(LocalDateTime timestamp);

  List<HmmHistory> findByTimestampBetweenOrderByTimestampAsc(LocalDateTime start,
      LocalDateTime end);
}
