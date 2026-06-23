package com.ratnesh.buildly.mapper;

import com.ratnesh.buildly.dto.auth.SignupRequest;
import com.ratnesh.buildly.dto.auth.UserProfileResponse;
import com.ratnesh.buildly.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    User toEntity(SignupRequest signupRequest);
    UserProfileResponse toUserProfileResponse(User user);
}
