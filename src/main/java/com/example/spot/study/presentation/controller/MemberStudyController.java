package com.example.spot.study.presentation.controller;

import com.example.spot.common.api.ApiResponse;
import com.example.spot.common.api.code.status.SuccessStatus;
import com.example.spot.study.application.StudyMemberCommandService;
import com.example.spot.study.application.StudyMemberQueryService;
import com.example.spot.member.domain.validation.annotation.ExistMember;
import com.example.spot.study.domain.validation.annotation.ExistStudy;
import com.example.spot.story.domain.validation.annotation.ExistStory;
import com.example.spot.todo.domain.validation.annotation.ExistToDo;
import com.example.spot.vote.domain.validation.annotation.ExistVote;
import com.example.spot.common.presentation.validator.TextLength;
import com.example.spot.study.presentation.dto.request.StudyHostWithdrawRequestDTO;
import com.example.spot.study.presentation.dto.request.StudyMemberReportDTO;
import com.example.spot.study.presentation.dto.request.StudyVoteRequestDTO;
import com.example.spot.study.presentation.dto.response.StudyImageResponseDTO;
import com.example.spot.study.presentation.dto.response.StudyMemberResDTO;
import com.example.spot.story.web.dto.response.StoryResDTO;
import com.example.spot.study.presentation.dto.response.StudyTerminationResponseDTO;
import com.example.spot.study.presentation.dto.response.StudyVoteResponseDTO;
import com.example.spot.study.presentation.dto.response.StudyWithdrawalResponseDTO;
import com.example.spot.study.presentation.dto.response.StudyApplyResponseDTO;
import com.example.spot.story.web.dto.response.StoryResponseDTO;
import com.example.spot.member.presentation.dto.MemberResponseDTO;
import com.example.spot.todo.presentation.dto.request.ToDoListRequestDTO;
import com.example.spot.todo.presentation.dto.response.ToDoListResponseDTO.ToDoListCreateResponseDTO;
import com.example.spot.todo.presentation.dto.response.ToDoListResponseDTO.ToDoListSearchResponseDTO;
import com.example.spot.todo.presentation.dto.response.ToDoListResponseDTO.ToDoListUpdateResponseDTO;
import com.example.spot.study.presentation.dto.response.StudyMemberResponseDTO;
import com.example.spot.study.presentation.dto.response.StudyMemberResponseDTO.StudyApplicantDTO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import java.time.LocalDate;

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
    @Operation(summary = "[스터디 상세 정보] 스터디 최근 공지 1개 불러오기", description = """ 
            ## [스터디 상세 정보] 내 스터디 > 스터디 클릭, 로그인한 회원이 참여하는 특정 스터디의 최근 공지 1개를 불러옵니다.
            study_post의 announced_at이 가장 최근인 공지 1개가 반환됩니다.
            """)
    @GetMapping("/studies/{studyId}/announce")
    public ApiResponse<StoryResponseDTO> getRecentAnnouncement(@PathVariable @ExistStudy Long studyId) {
        StoryResponseDTO storyResponseDTO = studyMemberQueryService.findStudyAnnouncementPost(studyId);
        return ApiResponse.onSuccess(SuccessStatus._STUDY_POST_FOUND, storyResponseDTO);
    }

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

    /* ----------------------------- 스터디 투표 관련 API ------------------------------------- */

    @Tag(name = "스터디 투표")
    @Operation(summary = "[스터디 투표] 투표 생성하기", description = """ 
            ## [스터디 투표] 내 스터디 > 스터디 > 투표 > 작성 버튼 클릭, 로그인한 회원이 참여하는 특정 스터디에서 새로운 투표를 등록합니다.
            스터디에 참여하는 회원이 생성한 투표를 vote에 저장합니다.
            """)
    @Parameter(name = "studyId", description = "투표를 생성할 스터디의 id를 입력합니다.", required = true)
    @PostMapping("/studies/{studyId}/votes")
    public ApiResponse<StudyVoteResponseDTO.VotePreviewDTO> createVote(
            @PathVariable @ExistStudy Long studyId,
            @RequestBody @Valid StudyVoteRequestDTO.VoteDTO voteDTO) {
        StudyVoteResponseDTO.VotePreviewDTO votePreviewDTO = studyMemberCommandService.createVote(studyId, voteDTO);
        return ApiResponse.onSuccess(SuccessStatus._STUDY_VOTE_CREATED, votePreviewDTO);
    }

    @Tag(name = "스터디 투표")
    @Operation(summary = "[스터디 투표] 투표하기", description = """ 
            ## [스터디 투표] 내 스터디 > 스터디 > 투표 > 특정 투표 클릭, 로그인한 회원이 참여하는 스터디에서 특정 항목에 투표합니다.
            member_vote에 투표 정보를 저장합니다.
            """)
    @Parameter(name = "studyId", description = "스터디의 id를 입력합니다.", required = true)
    @Parameter(name = "voteId", description = "참여할 스터디 투표의 id를 입력합니다.")
    @PostMapping("/studies/{studyId}/votes/{voteId}/options")
    public ApiResponse<StudyVoteResponseDTO.VotedOptionDTO> vote(
            @PathVariable @ExistStudy Long studyId,
            @PathVariable @ExistVote Long voteId,
            @RequestBody @Valid StudyVoteRequestDTO.VotedOptionDTO votedOptionDTO) {
        StudyVoteResponseDTO.VotedOptionDTO votedOptionResDTO = studyMemberCommandService.vote(studyId, voteId, votedOptionDTO);
        return ApiResponse.onSuccess(SuccessStatus._STUDY_VOTE_PARTICIPATED, votedOptionResDTO);
    }

    @Tag(name = "스터디 투표")
    @Operation(summary = "[스터디 투표] 투표 편집하기", description = """ 
            ## [스터디 투표] 내 스터디 > 스터디 > 투표 > 편집하기 버튼 클릭, 로그인한 회원이 참여하는 특정 스터디에서 투표 정보를 수정합니다.
            스터디에 참여하는 회원이 생성한 투표를 vote에 저장합니다.
            """)
    @Parameter(name = "studyId", description = "스터디의 id를 입력합니다.", required = true)
    @Parameter(name = "voteId", description = "편집할 스터디 투표의 id를 입력합니다.")
    @PatchMapping("/studies/{studyId}/votes/{voteId}")
    public ApiResponse<StudyVoteResponseDTO.VotePreviewDTO> updateVote(
            @PathVariable @ExistStudy Long studyId,
            @PathVariable @ExistVote Long voteId,
            @RequestBody @Valid StudyVoteRequestDTO.VoteUpdateDTO voteDTO) {
        StudyVoteResponseDTO.VotePreviewDTO votePreviewDTO = studyMemberCommandService.updateVote(studyId, voteId, voteDTO);
        return ApiResponse.onSuccess(SuccessStatus._STUDY_VOTE_UPDATED, votePreviewDTO);
    }

    @Tag(name = "스터디 투표")
    @Operation(summary = "[스터디 투표] 투표 삭제하기", description = """ 
            ## [스터디 투표] 내 스터디 > 스터디 > 투표 > 삭제하기 버튼 클릭, 로그인한 회원이 참여하는 특정 스터디에서 투표를 삭제합니다.
            스터디에 참여하는 회원이 생성한 투표를 vote에 저장합니다.
            """)
    @Parameter(name = "studyId", description = "스터디의 id를 입력합니다.", required = true)
    @Parameter(name = "voteId", description = "삭제할 스터디 투표의 id를 입력합니다.")
    @DeleteMapping("/studies/{studyId}/votes/{voteId}")
    public ApiResponse<StudyVoteResponseDTO.VotePreviewDTO> deleteVote(
            @PathVariable @ExistStudy Long studyId,
            @PathVariable @ExistVote Long voteId) {
        StudyVoteResponseDTO.VotePreviewDTO votePreviewDTO = studyMemberCommandService.deleteVote(studyId, voteId);
        return ApiResponse.onSuccess(SuccessStatus._STUDY_VOTE_DELETED, votePreviewDTO);
    }

    @Tag(name = "스터디 투표")
    @Operation(summary = "[스터디 투표] 투표 목록 불러오기", description = """
            ## [스터디 투표] 내 스터디 > 스터디 > 투표 클릭, 로그인한 회원이 참여하는 특정 스터디의 투표 목록을 불러옵니다.
            진행 중(finished_at 이전)인 투표 목록과 마감(finished_at 이후)된 투표 목록을 구분하여 반환합니다.
            """)
    @Parameter(name = "studyId", description = "투표 목록을 불러올 스터디의 id를 입력합니다.", required = true)
    @GetMapping("/studies/{studyId}/votes")
    public ApiResponse<StudyVoteResponseDTO.VoteListDTO> getAllVotes(
            @PathVariable @ExistStudy Long studyId) {
        StudyVoteResponseDTO.VoteListDTO voteListDTO = studyMemberQueryService.getAllVotes(studyId);
        return ApiResponse.onSuccess(SuccessStatus._STUDY_VOTE_FOUND, voteListDTO);
    }

    @Tag(name = "스터디 투표")
    @Operation(summary = "[스터디 투표] 투표 불러오기", description = """ 
            ## [스터디 투표] 내 스터디 > 스터디 > 투표 > 특정 투표 클릭, 로그인한 회원이 참여하는 특정 스터디의 투표를 불러옵니다.
            진행중인 투표 : 진행중인 투표에 대한 항목 및 기본 정보가 반환됩니다.
            마감된 투표 : 마감된 투표에 대한 항목과 투표 인원수가 반환됩니다.
            (진행중인 투표인지 마감된 투표인지에 따라 Response DTO가 서로 다릅니다.)
            """)
    @Parameter(name = "studyId", description = "스터디의 id를 입력합니다.", required = true)
    @Parameter(name = "voteId", description = "불러올 스터디 투표의 id를 입력합니다.")
    @GetMapping("/studies/{studyId}/votes/{voteId}")
    public ApiResponse<?> getVote(
            @PathVariable @ExistStudy Long studyId,
            @PathVariable @ExistVote Long voteId) {
        Boolean isCompleted = studyMemberQueryService.getIsCompleted(voteId);
        if (isCompleted) {
            StudyVoteResponseDTO.CompletedVoteDTO completedVoteDTO = studyMemberQueryService.getVoteInCompletion(studyId, voteId);
            return ApiResponse.onSuccess(SuccessStatus._STUDY_VOTE_FOUND, completedVoteDTO);
        } else {
            StudyVoteResponseDTO.VoteDTO voteDTO = studyMemberQueryService.getVoteInProgress(studyId, voteId);
            return ApiResponse.onSuccess(SuccessStatus._STUDY_VOTE_FOUND, voteDTO);
        }
    }

    @Tag(name = "스터디 투표")
    @Operation(summary = "[스터디 투표] 마감된 투표 현황 불러오기", description = """ 
            ## [스터디 투표] 내 스터디 > 스터디 > 투표 > 마감된 투표 > n명 참여 클릭, 로그인한 회원이 참여하는 특정 스터디의 투표를 불러옵니다.
            마감된 투표에 대하여 항목별 투표 회원 목록을 반환합니다.
            """)
    @Parameter(name = "studyId", description = "스터디의 id를 입력합니다.", required = true)
    @Parameter(name = "voteId", description = "마감된 스터디 투표의 id를 입력합니다.")
    @GetMapping("/studies/{studyId}/votes/{voteId}/details")
    public ApiResponse<StudyVoteResponseDTO.CompletedVoteDetailDTO> getCompletedVoteDetail(
            @PathVariable @ExistStudy Long studyId,
            @PathVariable @ExistVote Long voteId) {
        StudyVoteResponseDTO.CompletedVoteDetailDTO completedVoteDetailDTO = studyMemberQueryService.getCompletedVoteDetail(studyId, voteId);
        return ApiResponse.onSuccess(SuccessStatus._STUDY_VOTE_DETAIL_STATUS_FOUND, completedVoteDetailDTO);
    }

    /* ----------------------------- 스터디 갤러리 관련 API ------------------------------------- */
    @Tag(name = "스터디 이미지")
    @Operation(summary = "[스터디 갤러리] 스터디 이미지 목록 불러오기", description = """ 
            ## [스터디 갤러리] 내 스터디 > 스터디 > 갤러리 클릭, 로그인한 회원이 참여하는 스터디의 이미지 목록을 불러옵니다.
            study_post에 존재하는 모든 게시글의 이미지를 최신순으로 반환합니다.
            """)
    @Parameter(name = "studyId", description = "이미지 목록을 불러올 스터디의 id를 입력합니다.", required = true)
    @GetMapping("/studies/{studyId}/images")
    public ApiResponse<StudyImageResponseDTO.ImageListDTO> getAllStudyImages(
            @PathVariable @ExistStudy Long studyId,
            @RequestParam @Min(0) Integer offset,
            @RequestParam @Min(1) Integer limit) {
        StudyImageResponseDTO.ImageListDTO imageListDTO = studyMemberQueryService.getAllStudyImages(studyId, PageRequest.of(offset, limit));
        return ApiResponse.onSuccess(SuccessStatus._STUDY_POST_IMAGES_FOUND, imageListDTO);
    }

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
        MemberResponseDTO.ReportedMemberDTO reportedMemberDTO = studyMemberCommandService.reportStudyMember(studyId, memberId, studyMemberReportDTO);
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
        StoryResDTO.PostPreviewDTO postPreviewDTO = studyMemberCommandService.reportStudyPost(studyId, postId);
        return ApiResponse.onSuccess(SuccessStatus._STUDY_POST_REPORTED, postPreviewDTO);
    }
    
}