package com.jobflow.service;

import com.jobflow.dto.request.CreateApplicationRequest;
import com.jobflow.dto.response.ApplicationResponse;
import com.jobflow.entity.JobApplication;
import com.jobflow.entity.User;
import com.jobflow.entity.enums.Stage;
import com.jobflow.entity.enums.Tier;
import com.jobflow.exception.ResourceNotFoundException;
import com.jobflow.repository.JobApplicationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("null")
class ApplicationServiceTest {

    @Mock
    private JobApplicationRepository applicationRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private ApplicationService applicationService;

    private User mockUser;

    @BeforeEach
    void setUp() {
        mockUser = User.builder()
                .id(UUID.randomUUID())
                .clerkId("clerk_test_123")
                .email("test@example.com")
                .tier(Tier.FREE)
                .build();
    }

    @Test
    void listApplications_returnsPagedResults() {
        when(userService.getCurrentUser()).thenReturn(mockUser);
        JobApplication app = buildApplication();
        when(applicationRepository.findByUserIdAndDeletedAtIsNull(
                mockUser.getId(), PageRequest.of(0, 10)))
                .thenReturn(new PageImpl<>(List.of(app)));

        var result = applicationService.listApplications(null, PageRequest.of(0, 10));

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).company()).isEqualTo("Acme Corp");
    }

    @Test
    void create_savesAndReturnsApplication() {
        when(userService.getCurrentUser()).thenReturn(mockUser);
        JobApplication saved = buildApplication();
        when(applicationRepository.save(any(JobApplication.class))).thenReturn(saved);

        CreateApplicationRequest request = new CreateApplicationRequest(
                "Acme Corp", "Backend Engineer",
                "We use Java", null, null, Stage.APPLIED, null, null, null, null);

        ApplicationResponse response = applicationService.create(request);

        assertThat(response.company()).isEqualTo("Acme Corp");
        assertThat(response.stage()).isEqualTo(Stage.APPLIED);
        verify(applicationRepository).save(any(JobApplication.class));
    }

    @Test
    void getById_notFound_throwsException() {
        when(userService.getCurrentUser()).thenReturn(mockUser);
        UUID id = UUID.randomUUID();
        when(applicationRepository.findByIdAndUserIdAndDeletedAtIsNull(id, mockUser.getId()))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> applicationService.getById(id))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Application");
    }

    @Test
    void delete_setsDeletedAt() {
        when(userService.getCurrentUser()).thenReturn(mockUser);
        UUID id = UUID.randomUUID();
        JobApplication app = buildApplication();
        when(applicationRepository.findByIdAndUserIdAndDeletedAtIsNull(id, mockUser.getId()))
                .thenReturn(Optional.of(app));
        when(applicationRepository.save(any(JobApplication.class))).thenReturn(app);

        applicationService.delete(id);

        assertThat(app.getDeletedAt()).isNotNull();
        verify(applicationRepository).save(app);
    }

    private JobApplication buildApplication() {
        return JobApplication.builder()
                .id(UUID.randomUUID())
                .user(mockUser)
                .company("Acme Corp")
                .role("Backend Engineer")
                .stage(Stage.APPLIED)
                .build();
    }
}
