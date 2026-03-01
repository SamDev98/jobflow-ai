package com.jobflow.controller;

import com.jobflow.dto.request.CreateHistoryRequest;
import com.jobflow.dto.response.HistoryResponse;
import com.jobflow.entity.enums.HistoryType;
import com.jobflow.service.HistoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/history")
@RequiredArgsConstructor
@Tag(name = "History", description = "Management of user activity history (JD, resumes, etc.)")
public class HistoryController {

  private final HistoryService historyService;

  @GetMapping
  @Operation(summary = "Get user history")
  public Page<HistoryResponse> getHistory(
      @RequestParam(required = false) HistoryType type,
      Pageable pageable) {
    return historyService.getAllHistory(type, pageable);
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  @Operation(summary = "Create history entry")
  public HistoryResponse createHistory(@RequestBody @Valid CreateHistoryRequest request) {
    return historyService.createHistory(request);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @Operation(summary = "Delete history entry")
  public void deleteHistory(@PathVariable UUID id) {
    historyService.deleteHistory(id);
  }
}
