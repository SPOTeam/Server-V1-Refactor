package com.example.spot.vote.presentation.controller;

import com.example.spot.common.api.ApiResponse;
import com.example.spot.common.api.code.status.SuccessStatus;
import com.example.spot.study.domain.validation.annotation.ExistStudy;
import com.example.spot.vote.application.VoteCommandService;
import com.example.spot.vote.application.VoteQueryService;
import com.example.spot.vote.domain.validation.annotation.ExistVote;
import com.example.spot.vote.presentation.dto.request.StudyVoteRequestDTO;
import com.example.spot.vote.presentation.dto.response.StudyVoteResponseDTO;
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
public class VoteController {

    private final VoteQueryService voteQueryService;
    private final VoteCommandService voteCommandService;


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
        StudyVoteResponseDTO.VotePreviewDTO votePreviewDTO = voteCommandService.createVote(studyId, voteDTO);
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
        StudyVoteResponseDTO.VotedOptionDTO votedOptionResDTO = voteCommandService.vote(studyId, voteId, votedOptionDTO);
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
        StudyVoteResponseDTO.VotePreviewDTO votePreviewDTO = voteCommandService.updateVote(studyId, voteId, voteDTO);
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
        StudyVoteResponseDTO.VotePreviewDTO votePreviewDTO = voteCommandService.deleteVote(studyId, voteId);
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
        StudyVoteResponseDTO.VoteListDTO voteListDTO = voteQueryService.getAllVotes(studyId);
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
        Boolean isCompleted = voteQueryService.getIsCompleted(voteId);
        if (isCompleted) {
            StudyVoteResponseDTO.CompletedVoteDTO completedVoteDTO = voteQueryService.getVoteInCompletion(studyId, voteId);
            return ApiResponse.onSuccess(SuccessStatus._STUDY_VOTE_FOUND, completedVoteDTO);
        } else {
            StudyVoteResponseDTO.VoteDTO voteDTO = voteQueryService.getVoteInProgress(studyId, voteId);
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
        StudyVoteResponseDTO.CompletedVoteDetailDTO completedVoteDetailDTO = voteQueryService.getCompletedVoteDetail(studyId, voteId);
        return ApiResponse.onSuccess(SuccessStatus._STUDY_VOTE_DETAIL_STATUS_FOUND, completedVoteDetailDTO);
    }

}