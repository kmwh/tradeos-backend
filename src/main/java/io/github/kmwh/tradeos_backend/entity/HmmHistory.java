package io.github.kmwh.tradeos_backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "hmm_history")
public class HmmHistory {
    @Id
    private LocalDateTime timestamp;

    @Column(nullable = false, length = 20)
    private String symbol;

    @Column(nullable = false)
    private Integer trendScore;
}