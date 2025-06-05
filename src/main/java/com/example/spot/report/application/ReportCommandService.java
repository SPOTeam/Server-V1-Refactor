package com.example.spot.report.application;

import com.example.spot.member.presentation.dto.MemberResponseDTO;
import com.example.spot.report.presentation.dto.PostReportResponse;
import com.example.spot.story.web.dto.response.StoryResponseDTO;
import com.example.spot.report.presentation.dto.StudyMemberReportDTO;
import jakarta.validation.Valid;

public interface ReportCommandService {

    // 게시글 신고
    PostReportResponse reportPost(Long postId, Long memberId);

    // 스터디 회원 신고
    MemberResponseDTO.ReportedMemberDTO reportStudyMember(Long studyId, Long memberId, @Valid StudyMemberReportDTO studyMemberReportDTO);

    // 스터디 게시글 신고
    StoryResponseDTO.StoryPreviewDTO reportStudyPost(Long studyId, Long postId);
}
