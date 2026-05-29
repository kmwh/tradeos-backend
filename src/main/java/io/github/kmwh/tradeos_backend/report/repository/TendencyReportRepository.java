package io.github.kmwh.tradeos_backend.report.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import io.github.kmwh.tradeos_backend.report.entity.TendencyReport;
import java.util.List;
import java.util.Optional;

@Repository
public interface TendencyReportRepository extends JpaRepository<TendencyReport, Long> {
  Optional<TendencyReport> findTopByUserIdAndPeriodOrderByGeneratedAtDesc(Long userId,
      String period);

  List<TendencyReport> findAllByUserIdOrderByGeneratedAtDesc(Long userId);
}
