package com.example.spot.report.presentation.controller;

import com.example.spot.common.api.ApiResponse;
import com.example.spot.common.api.code.status.SuccessStatus;
import com.example.spot.member.domain.validation.annotation.ExistMember;
import com.example.spot.member.presentation.dto.MemberResponseDTO;
import com.example.spot.report.application.ReportCommandService;
import com.example.spot.story.domain.validation.annotation.ExistStory;
import com.example.spot.story.web.dto.response.StoryResDTO;
import com.example.spot.study.domain.validation.annotation.ExistStudy;
import com.example.spot.study.presentation.dto.request.StudyMemberReportDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/spot")
@Validated
public class ReportController {

    private final ReportCommandService reportCommandService;

/* ----------------------------- 스터디 회원 신고 관련 API ------------------------------------- */

    @Tag(name = "스터디 신고")
    @Operation(summary = "[스터디 신고] 스터디원 신고하기", description = """ 
            ## [스터디 신고] 로그인한 회원이 참여하는 스터디의 다른 회원을 신고합니다.
            신고당한 회원의 id와 이름이 반환됩니다.
            """)
    @Parameter(name = "studyId", description = "스터디의 id를 입력합니다.", required = true)
    @Parameter(name = "memberId", description = "신고할 스터디원의 id를 입력합니다.", required = true)
    @PostMapping("/studies/{studyId}/members/{memberId}/reports")
    public ApiResponse<MemberResponseDTO.ReportedMemberDTO> reportStudyMember(
            @PathVariable @ExistStudy Long studyId, @PathVariable @ExistMember Long memberId,
            @RequestBody @Valid StudyMemberReportDTO studyMemberReportDTO) {
        MemberResponseDTO.ReportedMemberDTO reportedMemberDTO = reportCommandService.reportStudyMember(studyId, memberId, studyMemberReportDTO);
        return ApiResponse.onSuccess(SuccessStatus._STUDY_MEMBER_REPORTED, reportedMemberDTO);
    }

    @Tag(name = "스터디 신고")
    @Operation(summary = "[스터디 신고] 스터디 게시글 신고하기", description = """ 
            ## [스터디 신고] 로그인한 회원이 참여하는 스터디의 게시글을 신고합니다.
            신고당한 스터디 게시글의 id와 제목이 반환됩니다.
            """)
    @Parameter(name = "studyId", description = "스터디의 id를 입력합니다.", required = true)
    @Parameter(name = "postId", description = "신고할 스터디 게시글의 id를 입력합니다.", required = true)
    @PostMapping("/studies/{studyId}/posts/{postId}/reports")
    public ApiResponse<StoryResDTO.PostPreviewDTO> reportStudyPost(
            @PathVariable @ExistStudy Long studyId,
            @PathVariable @ExistStory Long postId) {
        StoryResDTO.PostPreviewDTO postPreviewDTO = reportCommandService.reportStudyPost(studyId, postId);
        return ApiResponse.onSuccess(SuccessStatus._STUDY_POST_REPORTED, postPreviewDTO);
    }
}
