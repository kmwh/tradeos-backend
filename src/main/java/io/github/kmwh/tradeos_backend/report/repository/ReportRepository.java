package io.github.kmwh.tradeos_backend.report.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import io.github.kmwh.tradeos_backend.report.entity.Report;
import java.util.Optional;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {
  Optional<Report> findTopByUserIdOrderByGeneratedAtDesc(Long userId);

  Page<Report> findAllByUserIdOrderByGeneratedAtDesc(Long userId, Pageable pageable);
}
