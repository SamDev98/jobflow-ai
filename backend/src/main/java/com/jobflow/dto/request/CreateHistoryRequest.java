package com.jobflow.dto.request;

import com.jobflow.entity.enums.HistoryType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateHistoryRequest {
  @NotNull
  private HistoryType type;
  @NotBlank
  private String title;
  @NotBlank
  private String content;
  private String metadata;
}
