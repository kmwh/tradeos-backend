package io.github.kmwh.tradeos_backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "ai_reports")
public class AiReport {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "journal_id", nullable = false, unique = true)
    private Journal journal;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String feedbackText;

    @Column(updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}