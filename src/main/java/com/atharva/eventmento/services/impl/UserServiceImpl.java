package com.atharva.eventmento.services.impl;

import com.atharva.eventmento.domain.entities.User;
import com.atharva.eventmento.exceptions.UserNotFoundException;
import com.atharva.eventmento.repositories.UserRepository;
import com.atharva.eventmento.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public User getUserById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
    }

    @Override
    public User updateUserProfile(UUID userId, String profileImage) {
        User user = getUserById(userId);
        user.setProfileImage(profileImage);
        return userRepository.save(user);
    }
}
