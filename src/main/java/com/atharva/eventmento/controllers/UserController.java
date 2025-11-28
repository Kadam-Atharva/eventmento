package com.atharva.eventmento.controllers;

import com.atharva.eventmento.domain.dtos.UpdateUserProfileRequestDto;
import com.atharva.eventmento.domain.dtos.UserResponseDto;
import com.atharva.eventmento.domain.entities.User;
import com.atharva.eventmento.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import static com.atharva.eventmento.util.JwtUtil.parseUserId;

@RestController
@RequestMapping(path = "/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PatchMapping("/me")
    public ResponseEntity<UserResponseDto> updateProfile(
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody UpdateUserProfileRequestDto request) {

        User updatedUser = userService.updateUserProfile(
                parseUserId(jwt),
                request.getProfileImage()
        );

        return ResponseEntity.ok(UserResponseDto.builder()
                .id(updatedUser.getId())
                .name(updatedUser.getName())
                .email(updatedUser.getEmail())
                .profileImage(updatedUser.getProfileImage())
                .build());
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponseDto> getCurrentUser(@AuthenticationPrincipal Jwt jwt) {
        User user = userService.getUserById(parseUserId(jwt));
        return ResponseEntity.ok(UserResponseDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .profileImage(user.getProfileImage())
                .build());
    }
}
