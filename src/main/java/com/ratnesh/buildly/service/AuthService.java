package com.ratnesh.buildly.service;

import com.ratnesh.buildly.dto.auth.AuthResponse;
import com.ratnesh.buildly.dto.auth.LoginRequest;
import com.ratnesh.buildly.dto.auth.SignupRequest;

public interface AuthService {
    AuthResponse signup(SignupRequest request);

    AuthResponse login(LoginRequest request);

}
