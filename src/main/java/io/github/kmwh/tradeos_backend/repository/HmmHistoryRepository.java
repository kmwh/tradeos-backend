package io.github.kmwh.tradeos_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import io.github.kmwh.tradeos_backend.entity.HmmHistory;

@Repository
public interface HmmHistoryRepository extends JpaRepository<HmmHistory, Long> {
}