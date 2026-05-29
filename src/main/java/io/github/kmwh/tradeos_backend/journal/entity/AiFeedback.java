package io.github.kmwh.tradeos_backend.journal.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "ai_reports")
public class AiFeedback {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "journal_id", nullable = false, unique = true)
  private Journal journal;

  @Column(nullable = false, columnDefinition = "TEXT")
  private String feedbackText;

  @Column(updatable = false)
  private LocalDateTime createdAt;

  @Builder
  public AiFeedback(Journal journal, String feedbackText) {
    this.journal = journal;
    this.feedbackText = feedbackText;
    this.createdAt = LocalDateTime.now();
  }
}
