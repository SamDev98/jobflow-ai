package com.jobflow.service;

import com.jobflow.dto.request.UpdateProfileRequest;
import com.jobflow.dto.response.ProfileResponse;
import com.jobflow.entity.User;
import com.jobflow.entity.UserProfile;
import com.jobflow.entity.enums.Tier;
import com.jobflow.repository.UserProfileRepository;
import com.jobflow.repository.UserRepository;
import com.jobflow.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@SuppressWarnings("null")
public class UserService {

    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;

    @Transactional
    public User getOrCreateCurrentUser(String email) {
        String clerkId = SecurityUtils.getCurrentClerkId();
        return userRepository.findByClerkId(clerkId)
                .orElseGet(() -> userRepository.save(
                        User.builder()
                                .clerkId(clerkId)
                                .email(email)
                                .tier(Tier.FREE)
                                .build()));
    }

    public User getCurrentUser() {
        String clerkId = SecurityUtils.getCurrentClerkId();
        return userRepository.findByClerkId(clerkId)
                .orElseThrow(() -> new IllegalStateException(
                        "User not found for clerkId: " + clerkId +
                                ". Call POST /users/sync first."));
    }

    @Transactional(readOnly = true)
    public ProfileResponse getProfile() {
        User user = getCurrentUser();
        return ProfileResponse.from(user.getProfile());
    }

    @Transactional
    public ProfileResponse updateProfile(UpdateProfileRequest request) {
        User user = getCurrentUser();
        UserProfile profile = userProfileRepository.findById(user.getId())
                .orElseGet(() -> {
                    UserProfile p = new UserProfile();
                    p.setUser(user);
                    return p;
                });

        if (request.yearsExperience() != null)
            profile.setYearsExperience(request.yearsExperience());
        if (request.techStack() != null)
            profile.setTechStack(request.techStack());
        if (request.location() != null)
            profile.setLocation(request.location());
        if (request.workMode() != null)
            profile.setWorkMode(request.workMode());
        if (request.salaryMinUsd() != null)
            profile.setSalaryMinUsd(request.salaryMinUsd());

        return ProfileResponse.from(userProfileRepository.save(profile));
    }
}
