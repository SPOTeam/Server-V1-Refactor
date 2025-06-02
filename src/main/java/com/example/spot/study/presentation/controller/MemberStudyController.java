package com.example.spot.study.presentation.controller;

import com.example.spot.common.api.ApiResponse;
import com.example.spot.common.api.code.status.SuccessStatus;
import com.example.spot.study.application.StudyMemberCommandService;
import com.example.spot.study.application.StudyMemberQueryService;
import com.example.spot.member.domain.validation.annotation.ExistMember;
import com.example.spot.study.domain.validation.annotation.ExistStudy;
import com.example.spot.story.domain.validation.annotation.ExistStory;
import com.example.spot.vote.domain.validation.annotation.ExistVote;
import com.example.spot.common.presentation.validator.TextLength;
import com.example.spot.study.presentation.dto.request.StudyHostWithdrawRequestDTO;
import com.example.spot.study.presentation.dto.request.StudyMemberReportDTO;
import com.example.spot.vote.presentation.dto.request.StudyVoteRequestDTO;
import com.example.spot.study.presentation.dto.response.StudyImageResponseDTO;
import com.example.spot.study.presentation.dto.response.StudyMemberResDTO;
import com.example.spot.story.web.dto.response.StoryResDTO;
import com.example.spot.study.presentation.dto.response.StudyTerminationResponseDTO;
import com.example.spot.vote.presentation.dto.response.StudyVoteResponseDTO;
import com.example.spot.study.presentation.dto.response.StudyWithdrawalResponseDTO;
import com.example.spot.study.presentation.dto.response.StudyApplyResponseDTO;
import com.example.spot.story.web.dto.response.StoryResponseDTO;
import com.example.spot.member.presentation.dto.MemberResponseDTO;
import com.example.spot.study.presentation.dto.response.StudyMemberResponseDTO;
import com.example.spot.study.presentation.dto.response.StudyMemberResponseDTO.StudyApplicantDTO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/spot")
@Validated
public class MemberStudyController {

    private final StudyMemberQueryService studyMemberQueryService;
    private final StudyMemberCommandService studyMemberCommandService;

    /* ----------------------------- 진행중인 스터디 관련 API ------------------------------------- */


    @Tag(name = "진행중인 스터디")
    @Operation(summary = "[진행중인 스터디] 스터디 탈퇴하기", description = """ 
            ## [진행중인 스터디] 마이페이지 > 진행중 > 진행중인 스터디의 메뉴 클릭, 로그인한 회원이 현재 진행중인 스터디에서 탈퇴합니다.
            로그인한 회원이 참여하는 특정 스터디에 대해 member_study 튜플을 삭제합니다.
            """)
    @DeleteMapping("/studies/{studyId}/withdrawal")
    public ApiResponse<StudyWithdrawalResponseDTO.WithdrawalDTO> withdrawFromStudy(@PathVariable Long studyId) {
        StudyWithdrawalResponseDTO.WithdrawalDTO withdrawalDTO = studyMemberCommandService.withdrawFromStudy(studyId);
        return ApiResponse.onSuccess(SuccessStatus._STUDY_MEMBER_DELETED, withdrawalDTO);
    }

    @Tag(name = "진행중인 스터디")
    @Operation(summary = "[진행중인 스터디] 스터디 호스트 탈퇴",
            description = """
                        ## [진행중인 스터디] 특정 스터디의 호스트가 해당 스터디에서 탈퇴합니다.
                        탈퇴 시, 호스트 권한이 회수되며 스터디에서 제외됩니다. 
                        요청 시, 새로운 호스트의 아이디와 임명 사유를 입력해야 합니다.
                    """)
    @DeleteMapping("/studies/{studyId}/hosts/withdrawal")
    public ApiResponse<StudyWithdrawalResponseDTO.WithdrawalDTO> withdrawHostFromStudy(
            @PathVariable Long studyId,
            @RequestBody StudyHostWithdrawRequestDTO requestDTO) {
        StudyWithdrawalResponseDTO.WithdrawalDTO withdrawalDTO =
                studyMemberCommandService.withdrawHostFromStudy(studyId, requestDTO);
        return ApiResponse.onSuccess(SuccessStatus._STUDY_MEMBER_DELETED, withdrawalDTO);
    }


    @Tag(name = "진행중인 스터디")
    @Operation(summary = "[진행중인 스터디] 스터디 끝내기", description = """ 
            ## [진행중인 스터디] 마이페이지 > 진행중 > 진행중인 스터디의 메뉴 클릭, 로그인한 회원이 운영중인 스터디를 끝냅니다.
            * 로그인한 회원이 운영하는 특정 스터디에 대해 study status OFF로 전환합니다.
            * 스터디 성과를 입력받아 DB에 저장합니다.
            """)
    @PatchMapping("/studies/{studyId}/termination")
    public ApiResponse<StudyTerminationResponseDTO.TerminationDTO> terminateStudy(
            @PathVariable @ExistStudy Long studyId,
            @RequestParam @TextLength(min = 1, max = 30) String performance
    ) {
        StudyTerminationResponseDTO.TerminationDTO terminationDTO = studyMemberCommandService.terminateStudy(studyId, performance);
        return ApiResponse.onSuccess(SuccessStatus._STUDY_TERMINATED, terminationDTO);
    }


    /* ----------------------------- 모집중인 스터디 관련 API ------------------------------------- */

    @Tag(name = "모집중인 스터디")
    @Operation(summary = "[모집중인 스터디] 스터디 별 신청 여부 조회하기", description = """ 
            ## [모집중인 스터디] 로그인한 회원이 모집중인 스터디에 대해 신청 여부를 조회합니다.
            로그인한 회원이 참여하는 특정 스터디에 대해 member_study의 application_status가 APPLIED인지 확인합니다.
            반환 값은 boolean으로, 신청 여부를 나타냅니다.
            true: 신청한 상태, false: 신청하지 않은 상태
            """)
    @GetMapping("/studies/{studyId}/is-applied")
    @Parameter(name = "studyId", description = "모집중인 스터디의 ID를 입력 받습니다.", required = true)
    public ApiResponse<StudyApplicantDTO> getIsApplied(@PathVariable @ExistStudy Long studyId) {
        return ApiResponse.onSuccess(SuccessStatus._STUDY_APPLICANT_FOUND,
                studyMemberQueryService.isApplied(studyId));
    }

    @Tag(name = "모집중인 스터디")
    @Operation(summary = "[모집중인 스터디] 스터디별 신청 회원 목록 불러오기", description = """ 
            ## [모집중인 스터디] 마이페이지 > 모집중 > 스터디 클릭, 로그인한 회원이 모집중인 스터디에 신청한 회원 목록을 불러옵니다.
            로그인한 회원이 모집중인 특정 스터디에 대해 member_study의 application_status가 APPLIED인 회원 목록이 반환됩니다.
            """)
    @GetMapping("/studies/{studyId}/applicants")
    @Parameter(name = "studyId", description = "모집중인 스터디의 ID를 입력 받습니다.", required = true)
    public ApiResponse<StudyMemberResponseDTO> getAllApplicants(@PathVariable @ExistStudy Long studyId) {
        return ApiResponse.onSuccess(SuccessStatus._STUDY_APPLICANT_FOUND,
                studyMemberQueryService.findStudyApplicants(studyId));
    }

    @Tag(name = "모집중인 스터디")
    @Operation(summary = "[모집중인 스터디] 스터디 신청 정보(이름, 자기소개) 불러오기", description = """ 
            ## [모집중인 스터디] 마이페이지 > 모집중 > 스터디 > 신청 회원 클릭, 로그인한 회원이 모집중인 스터디에 신청한 회원의 정보를 불러옵니다.
            로그인한 회원이 모집중인 특정 스터디에 신청한 회원의 정보(member.name & member_study.introduction)가 반환됩니다.
            """)
    @GetMapping("/studies/{studyId}/applicants/{applicantId}")
    @Parameter(name = "studyId", description = "모집중인 스터디의 ID를 입력 받습니다.", required = true)
    @Parameter(name = "applicantId", description = "신청자의 ID를 입력 받습니다.", required = true)
    public ApiResponse<StudyMemberResponseDTO.StudyApplyMemberDTO> getApplicantInfo(
            @PathVariable @ExistStudy Long studyId,
            @PathVariable @ExistMember Long applicantId) {
        return ApiResponse.onSuccess(SuccessStatus._STUDY_APPLICANT_FOUND,
                studyMemberQueryService.findStudyApplication(studyId, applicantId));
    }

    @Tag(name = "모집중인 스터디")
    @Operation(summary = "[모집중인 스터디] 스터디 신청 처리하기", description = """ 
            ## [모집중인 스터디] 마이페이지 > 모집중 > 스터디 > 신청 회원 > 거절 클릭, 로그인한 회원이 모집중인 스터디에 신청한 회원을 처리합니다.
            isAccept가 true인 경우 member_study에서 application_status를 AWAITING_SELF_APPROVAL 수정합니다. -> 참가 희망하는 회원이 알림을 통해 스스로 승인 해야 스터디 참여가 완료됩니다.
            isAccept가 false인 경우 member_study에서 application_status를 REJECTED로 수정합니다.
            스터디 신청 처리 결과를 응답으로 반환합니다. 
            """)
    @PostMapping("/studies/{studyId}/applicants/{applicantId}")
    @Parameter(name = "studyId", description = "모집중인 스터디의 ID를 입력 받습니다.", required = true)
    @Parameter(name = "applicantId", description = "신청자의 ID를 입력 받습니다.", required = true)
    public ApiResponse<StudyApplyResponseDTO> rejectApplicant(
            @PathVariable @ExistStudy Long studyId,
            @PathVariable @ExistMember Long applicantId,
            @RequestParam boolean isAccept) {
        return ApiResponse.onSuccess(SuccessStatus._STUDY_APPLICANT_UPDATED,
                studyMemberCommandService.acceptAndRejectStudyApply(applicantId, studyId, isAccept));
    }

    @Tag(name = "테스트 용 API", description = "테스트 용 API")
    @Operation(summary = "!테스트 용! [모집중인 스터디] 스터디 신청 처리하기", description = """ 
            ## [모집중인 스터디] 빠른 API 적용를 위해 스터디 신청 처리를 즉시 수행합니다.
            위 API와 동일한 기능을 수행하지만, 실제 알림이 발송되지 않습니다.
            또한 스터디 신청 승인 시,  회원의 상태가 AWAITING_SELF_APPROVAL가 아닌 APPROVED로 변경됩니다.
            
            즉, 바로 스터디 참여가 완료됩니다. 스터디 회원 조회 시, 바로 승인된 회원으로 조회됩니다.
            
            isAccept가 true인 경우 member_study에서 application_status를 APPROVED로 수정합니다.
            isAccept가 false인 경우 member_study에서 application_status를 REJECTED로 수정합니다.
            스터디 신청 처리 결과를 응답으로 반환합니다.
            """)
    @PostMapping("/studies/{studyId}/applicants/{applicantId}/test")
    @Parameter(name = "studyId", description = "모집중인 스터디의 ID를 입력 받습니다.", required = true)
    @Parameter(name = "applicantId", description = "신청자의 ID를 입력 받습니다.", required = true)
    public ApiResponse<StudyApplyResponseDTO> rejectApplicantForTest(
            @PathVariable @ExistStudy Long studyId,
            @PathVariable @ExistMember Long applicantId,
            @RequestParam boolean isAccept) {
        return ApiResponse.onSuccess(SuccessStatus._STUDY_APPLICANT_UPDATED,
                studyMemberCommandService.acceptAndRejectStudyApplyForTest(applicantId, studyId, isAccept));
    }


    /* ----------------------------- 스터디 상세 정보 관련 API ------------------------------------- */

    @Tag(name = "스터디 상세 정보")
    @Operation(summary = "[스터디 상세 정보] 스터디에 참여하는 회원 목록 불러오기", description = """ 
            ## [스터디 상세 정보] 로그인한 회원이 참여하는 특정 스터디의 회원 목록을 전체 조회 합니다.
            member_study에서 application_status=APPROVED인 회원의 목록(이름, 프로필 사진 포함)이 반환됩니다.
            """)
    @GetMapping("/studies/{studyId}/members")
    public ApiResponse<StudyMemberResponseDTO> getStudyMembers(
            @PathVariable @ExistStudy Long studyId) {
        StudyMemberResponseDTO studyMemberResponseDTO = studyMemberQueryService.findStudyMembers(studyId);
        return ApiResponse.onSuccess(SuccessStatus._STUDY_MEMBER_FOUND, studyMemberResponseDTO);
    }

    @Tag(name = "스터디 상세 정보")
    @Operation(summary = "[스터디 상세 정보] 스터디 호스트 정보 불러오기", description = """ 
            ## [스터디 상세 정보] 로그인한 회원이 참여하는 특정 스터디의 호스트 정보를 조회합니다.
            * isOwned : 로그인한 회원이 호스트인지 true or false로 반환
            * host : 호스트의 id와 nickname 반환
            """)
    @GetMapping("/studies/{studyId}/host")
    public ApiResponse<StudyMemberResDTO.StudyHostDTO> getStudyHost(
            @PathVariable @ExistStudy Long studyId) {
        StudyMemberResDTO.StudyHostDTO studyHostDTO = studyMemberQueryService.getStudyHost(studyId);
        return ApiResponse.onSuccess(SuccessStatus._STUDY_HOST_FOUND, studyHostDTO);
    }

}