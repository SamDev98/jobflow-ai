package com.jobflow.service;

import com.jobflow.dto.request.CreateHistoryRequest;
import com.jobflow.dto.response.HistoryResponse;
import com.jobflow.entity.HistoryItem;
import com.jobflow.entity.User;
import com.jobflow.entity.enums.HistoryType;
import com.jobflow.repository.HistoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("null")
class HistoryServiceTest {

  @Mock
  private HistoryRepository historyRepository;

  @Mock
  private UserService userService;

  @InjectMocks
  private HistoryService historyService;

  private User mockUser;

  @BeforeEach
  void setUp() {
    mockUser = User.builder()
        .id(UUID.randomUUID())
        .clerkId("clerk_123")
        .build();
  }

  @Test
  void getAllHistory_returnsPagedResults() {
    when(userService.getCurrentUser()).thenReturn(mockUser);
    HistoryItem item = HistoryItem.builder().title("Test").build();
    when(historyRepository.findAllByUserId(eq(mockUser.getId()), any()))
        .thenReturn(new PageImpl<>(List.of(item)));

    var result = historyService.getAllHistory(null, PageRequest.of(0, 10));

    assertThat(result.getContent()).hasSize(1);
  }

  @Test
  void createHistory_savesItem() {
    when(userService.getCurrentUser()).thenReturn(mockUser);
    HistoryItem item = HistoryItem.builder().id(UUID.randomUUID()).title("Created").build();
    when(historyRepository.save(any())).thenReturn(item);

    CreateHistoryRequest request = CreateHistoryRequest.builder()
        .title("Created")
        .type(HistoryType.OTHER)
        .content("Content")
        .build();

    HistoryResponse response = historyService.createHistory(request);

    assertThat(response.getTitle()).isEqualTo("Created");
    verify(historyRepository).save(any());
  }
}
