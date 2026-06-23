package com.ratnesh.buildly.service;

import com.ratnesh.buildly.dto.MemberResponse.InviteMemberRequest;
import com.ratnesh.buildly.dto.MemberResponse.MemberResponse;
import com.ratnesh.buildly.dto.MemberResponse.UpdateMemberRoleRequest;

import java.util.List;

public interface ProjectMemberService {
    List<MemberResponse> getProjectMembers(Long projectId);

    MemberResponse inviteMember(Long projectId, InviteMemberRequest request);

    MemberResponse updateMemberRole(Long projectId, Long memberId, UpdateMemberRoleRequest request);

    void removeProjectMember(Long projectId, Long memberId);
}
