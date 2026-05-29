package io.github.kmwh.tradeos_backend.journal.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import io.github.kmwh.tradeos_backend.journal.entity.AiFeedback;

@Repository
public interface AiFeedbackRepository extends JpaRepository<AiFeedback, Long> {
}
