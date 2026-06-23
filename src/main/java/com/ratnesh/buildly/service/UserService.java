package com.ratnesh.buildly.service;

import com.ratnesh.buildly.dto.auth.UserProfileResponse;

public interface UserService {
    UserProfileResponse getProfile(Long userId);
}
