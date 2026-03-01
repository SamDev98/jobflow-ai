package com.jobflow.service;

import com.jobflow.dto.request.UpdateProfileRequest;
import com.jobflow.dto.response.ProfileResponse;
import com.jobflow.entity.User;
import com.jobflow.entity.UserProfile;
import com.jobflow.entity.enums.Tier;
import com.jobflow.repository.UserProfileRepository;
import com.jobflow.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("null")
class UserServiceTest {

  @Mock
  private UserRepository userRepository;

  @Mock
  private UserProfileRepository userProfileRepository;

  @InjectMocks
  private UserService userService;

  @AfterEach
  void tearDown() {
    SecurityContextHolder.clearContext();
  }

  @Test
  void getCurrentUser_returnsUserWhenExists() {
    setAuth("clerk_123");
    User user = User.builder()
        .id(UUID.randomUUID())
        .clerkId("clerk_123")
        .email("user@example.com")
        .tier(Tier.FREE)
        .build();

    when(userRepository.findByClerkId("clerk_123")).thenReturn(Optional.of(user));

    User result = userService.getCurrentUser();

    assertSame(user, result);
  }

  @Test
  void getCurrentUser_throwsWhenMissing() {
    setAuth("clerk_missing");
    when(userRepository.findByClerkId("clerk_missing")).thenReturn(Optional.empty());

    assertThrows(IllegalStateException.class, () -> userService.getCurrentUser());
  }

  @Test
  void updateProfile_createsAndSavesWhenMissing() {
    UUID userId = UUID.randomUUID();
    setAuth("clerk_456");
    User user = User.builder()
        .id(userId)
        .clerkId("clerk_456")
        .email("user@example.com")
        .tier(Tier.FREE)
        .build();

    when(userRepository.findByClerkId("clerk_456")).thenReturn(Optional.of(user));
    when(userProfileRepository.findById(userId)).thenReturn(Optional.empty());
    when(userProfileRepository.save(any(UserProfile.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    UpdateProfileRequest request = new UpdateProfileRequest(
        5,
        List.of("Java", "Spring"),
        "Remote",
        "Hybrid",
        120000);

    ProfileResponse response = userService.updateProfile(request);

    assertEquals(5, response.yearsExperience());
    assertEquals("Remote", response.location());
    assertEquals("Hybrid", response.workMode());
    assertEquals(120000, response.salaryMinUsd());

    ArgumentCaptor<UserProfile> captor = ArgumentCaptor.forClass(UserProfile.class);
    verify(userProfileRepository).save(captor.capture());
    assertSame(user, captor.getValue().getUser());
    assertEquals(List.of("Java", "Spring"), captor.getValue().getTechStack());
  }

  private void setAuth(String clerkId) {
    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
        clerkId,
        null,
        AuthorityUtils.NO_AUTHORITIES);
    SecurityContextHolder.getContext().setAuthentication(authentication);
  }
}
