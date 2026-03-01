package com.jobflow.entity;

import com.jobflow.entity.enums.HistoryType;
import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "history_items", indexes = {
    @Index(name = "idx_history_user_type", columnList = "user_id, type")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HistoryItem {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private HistoryType type;

  @Column(nullable = false)
  private String title;

  @Column(columnDefinition = "TEXT")
  private String content;

  // Optional metadata stored as JSON in TEXT or separate columns
  @Column(name = "metadata", columnDefinition = "TEXT")
  private String metadata;

  @Builder.Default
  @Column(name = "created_at", nullable = false, updatable = false)
  private Instant createdAt = Instant.now();

  @Column(name = "deleted_at")
  private Instant deletedAt;
}
