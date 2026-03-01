package com.jobflow.dto.response;

import com.jobflow.entity.UserProfile;

import java.util.List;

public record ProfileResponse(
        Integer yearsExperience,
        List<String> techStack,
        String location,
        String workMode,
        Integer salaryMinUsd
) {
    public static ProfileResponse from(UserProfile profile) {
        if (profile == null) return new ProfileResponse(null, null, null, null, null);
        return new ProfileResponse(
                profile.getYearsExperience(),
                profile.getTechStack(),
                profile.getLocation(),
                profile.getWorkMode(),
                profile.getSalaryMinUsd()
        );
    }
}
