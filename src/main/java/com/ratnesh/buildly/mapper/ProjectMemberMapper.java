package com.ratnesh.buildly.mapper;

import com.ratnesh.buildly.dto.MemberResponse.MemberResponse;
import com.ratnesh.buildly.entity.ProjectMember;
import com.ratnesh.buildly.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProjectMemberMapper {

    @Mapping(target = "userId" , source = "id")
    @Mapping(target = "projectRole" , constant ="OWNER")
    MemberResponse toProjectMemberResponseFromOwner(User owner);


     @Mapping(target = "userId",source = "user.id")
     @Mapping(target = "username",source = "user.username")
     @Mapping(target = "name",source = "user.name")

    MemberResponse toProjectMemberResponseFromMember(ProjectMember member);
}
