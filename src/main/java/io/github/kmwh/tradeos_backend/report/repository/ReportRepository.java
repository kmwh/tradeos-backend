package io.github.kmwh.tradeos_backend.report.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import io.github.kmwh.tradeos_backend.report.entity.Report;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {
  Optional<Report> findTopByUserIdOrderByGeneratedAtDesc(Long userId);

  List<Report> findAllByUserIdOrderByGeneratedAtDesc(Long userId);
}
