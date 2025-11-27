package com.atharva.eventmento.services;

import com.atharva.eventmento.domain.entities.User;

import java.util.UUID;

public interface UserService {
    User getUserById(UUID id);
    User updateUserProfile(UUID userId, String profileImage);
}
