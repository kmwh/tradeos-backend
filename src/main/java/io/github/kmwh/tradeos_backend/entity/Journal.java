package io.github.kmwh.tradeos_backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "journals")
public class Journal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private LocalDateTime entryTime;

    @Column(nullable = false)
    private LocalDateTime exitTime;

    @Column(nullable = false, length = 10)
    private String position;

    @Column(nullable = false)
    private Double entryPrice;

    @Column(nullable = false)
    private Double exitPrice;

    @Column(columnDefinition = "DOUBLE DEFAULT 1.0")
    private Double leverage = 1.0;

    private Double realizedPnl;

    private Double roi;

    @Column(columnDefinition = "TEXT")
    private String entryReason;

    @Column(columnDefinition = "TEXT")
    private String exitReason;

    @Column(length = 50)
    private String emotionTag;

    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean isAnalyzed = false;

    @Column(updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}