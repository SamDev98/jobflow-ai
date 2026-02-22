package com.jobflow.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jobflow.dto.request.GenerateInterviewPrepRequest;
import com.jobflow.dto.response.InterviewPrepResponse;
import com.jobflow.entity.InterviewPrep;
import com.jobflow.entity.User;
import com.jobflow.entity.enums.Tier;
import com.jobflow.exception.ResourceNotFoundException;
import com.jobflow.repository.InterviewPrepRepository;
import com.jobflow.repository.JobApplicationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("null")
class InterviewPrepServiceTest {

  @Mock
  private LLMOrchestrator llm;
  @Mock
  private InterviewPrepRepository interviewPrepRepository;
  @Mock
  private JobApplicationRepository jobApplicationRepository;
  @Mock
  private UserService userService;
  @Mock
  private HistoryService historyService;

  private InterviewPrepService interviewPrepService;

  @BeforeEach
  void setUp() {
    interviewPrepService = new InterviewPrepService(
        llm,
        interviewPrepRepository,
        jobApplicationRepository,
        userService,
        new ObjectMapper(),
        historyService);
  }

  @Test
  void generate_withoutApplication_persistsAndReturnsQuestions() {
    User user = User.builder()
        .id(UUID.randomUUID())
        .tier(Tier.FREE)
        .clerkId("clerk_1")
        .email("user@example.com")
        .build();

    when(userService.getCurrentUser()).thenReturn(user);
    when(llm.completeAsJson(anyString(), eq(Tier.FREE), eq(List.class)))
        .thenReturn(List.of(Map.of(
            "question", "Q1",
            "answer_outline", "A1",
            "difficulty", "easy")));
    when(interviewPrepRepository.save(any(InterviewPrep.class)))
        .thenAnswer(invocation -> {
          InterviewPrep entity = invocation.getArgument(0);
          entity.setId(UUID.randomUUID());
          return entity;
        });

    GenerateInterviewPrepRequest request = new GenerateInterviewPrepRequest(
        null,
        "Backend Engineer",
        null,
        null,
        List.of("Java"));

    InterviewPrepResponse response = interviewPrepService.generate(request);

    assertEquals(1, response.questions().size());
    assertEquals("Q1", response.questions().get(0).question());
    verify(interviewPrepRepository).save(any(InterviewPrep.class));
  }

  @Test
  void generate_whenApplicationMissing_throwsResourceNotFoundException() {
    UUID applicationId = UUID.randomUUID();
    User user = User.builder()
        .id(UUID.randomUUID())
        .tier(Tier.FREE)
        .clerkId("clerk_1")
        .email("user@example.com")
        .build();

    when(userService.getCurrentUser()).thenReturn(user);
    when(jobApplicationRepository.findByIdAndUserIdAndDeletedAtIsNull(applicationId, user.getId()))
        .thenReturn(Optional.empty());

    GenerateInterviewPrepRequest request = new GenerateInterviewPrepRequest(
        applicationId,
        "Backend Engineer",
        null,
        null,
        List.of("Java"));

    assertThrows(ResourceNotFoundException.class, () -> interviewPrepService.generate(request));
    verify(interviewPrepRepository, never()).save(any(InterviewPrep.class));
  }

  @Test
  void getById_whenOwnerMismatch_throwsResourceNotFoundException() {
    UUID prepId = UUID.randomUUID();
    User currentUser = User.builder()
        .id(UUID.randomUUID())
        .tier(Tier.FREE)
        .clerkId("clerk_current")
        .email("current@example.com")
        .build();
    User anotherUser = User.builder()
        .id(UUID.randomUUID())
        .tier(Tier.FREE)
        .clerkId("clerk_other")
        .email("other@example.com")
        .build();
    InterviewPrep entity = InterviewPrep.builder()
        .id(prepId)
        .user(anotherUser)
        .questions("[]")
        .build();

    when(userService.getCurrentUser()).thenReturn(currentUser);
    when(interviewPrepRepository.findById(prepId)).thenReturn(Optional.of(entity));

    assertThrows(ResourceNotFoundException.class, () -> interviewPrepService.getById(prepId));
  }
}
