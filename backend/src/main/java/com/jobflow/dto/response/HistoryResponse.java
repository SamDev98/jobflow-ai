package com.jobflow.dto.response;

import com.jobflow.entity.enums.HistoryType;
import lombok.Builder;
import lombok.Data;
import java.time.Instant;
import java.util.UUID;

@Data
@Builder
public class HistoryResponse {
  private UUID id;
  private HistoryType type;
  private String title;
  private String content;
  private String metadata;
  private Instant createdAt;
}
