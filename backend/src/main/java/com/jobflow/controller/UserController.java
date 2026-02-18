package com.jobflow.controller;

import com.jobflow.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Tag(name = "Users", description = "User sync and profile")
public class UserController {

    private final UserService userService;

    /**
     * Called by the frontend after Clerk login to create/sync the user.
     * The JWT sub is extracted automatically from the SecurityContext.
     */
    @PostMapping("/sync")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Sync Clerk user into the database (call after login)")
    public void sync(@RequestParam String email) {
        userService.getOrCreateCurrentUser(email);
    }
}
