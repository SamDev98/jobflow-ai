package com.jobflow.controller;

import com.jobflow.dto.request.UpdateProfileRequest;
import com.jobflow.dto.response.ProfileResponse;
import com.jobflow.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/profile")
@RequiredArgsConstructor
@Tag(name = "Profile", description = "User job profile")
public class ProfileController {

    private final UserService userService;

    @GetMapping
    @Operation(summary = "Get current user's job profile")
    public ProfileResponse get() {
        return userService.getProfile();
    }

    @PutMapping
    @Operation(summary = "Create or update current user's job profile")
    public ProfileResponse update(@Valid @RequestBody UpdateProfileRequest request) {
        return userService.updateProfile(request);
    }
}
