package com.example.spot.vote.application;

import com.example.spot.common.api.code.status.ErrorStatus;
import com.example.spot.common.api.exception.handler.StudyHandler;
import com.example.spot.common.security.utils.SecurityUtils;
import com.example.spot.member.domain.Member;
import com.example.spot.member.infrastructure.MemberRepository;
import com.example.spot.study.domain.Study;
import com.example.spot.study.domain.StudyRepository;
import com.example.spot.study.domain.enums.StudyApplicationStatus;
import com.example.spot.study.domain.repository.StudyMemberRepository;
import com.example.spot.vote.domain.Vote;
import com.example.spot.vote.domain.VoteRepository;
import com.example.spot.vote.domain.association.VoteOption;
import com.example.spot.vote.domain.repository.VoteParticipantRepository;
import com.example.spot.vote.presentation.dto.response.StudyVoteResponseDTO;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class VoteQueryServiceImpl implements VoteQueryService {

    private final MemberRepository memberRepository;
    private final StudyRepository studyRepository;
    private final StudyMemberRepository studyMemberRepository;
    private final VoteRepository voteRepository;
    private final VoteParticipantRepository voteParticipantRepository;


    /**
     * 스터디에 생성된 모든 투표 목록을 불러옵니다.
     *
     * @param studyId 투표 목록을 불러올 타겟 스터디의 아이디를 입력 받습니다.
     * @return 스터디 아이디와 해당 스터디에서 진행중인 투표 목록, 마감된 투표 목록을 반환합니다.
     */
    @Override
    public StudyVoteResponseDTO.VoteListDTO getAllVotes(Long studyId) {

        //=== Exception ===//
        Long memberId = SecurityUtils.getCurrentUserId();
        SecurityUtils.verifyUserId(memberId);

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new StudyHandler(ErrorStatus._MEMBER_NOT_FOUND));
        Study study = studyRepository.findById(studyId)
                .orElseThrow(() -> new StudyHandler(ErrorStatus._STUDY_NOT_FOUND));

        // 로그인한 회원이 스터디 회원인지 확인
        studyMemberRepository.findByMemberIdAndStudyIdAndStatus(memberId, studyId, StudyApplicationStatus.APPROVED)
                .orElseThrow(() -> new StudyHandler(ErrorStatus._STUDY_MEMBER_NOT_FOUND));

        //=== Feature ===//

        // 진행중인 투표 목록
        List<StudyVoteResponseDTO.VoteInfoDTO> votesInProgress = voteRepository.findAllByStudyIdAndFinishedAtAfter(
                        studyId, LocalDateTime.now()).stream()
                .map(vote -> {
                    boolean isParticipated = isParticipated(vote, member);
                    return StudyVoteResponseDTO.VoteInfoDTO.toDTO(vote, isParticipated);
                })
                .toList();

        // 마감된 투표 목록
        List<StudyVoteResponseDTO.VoteInfoDTO> votesInCompletion = voteRepository.findAllByStudyIdAndFinishedAtBefore(
                        studyId, LocalDateTime.now()).stream()
                .map(vote -> {
                    boolean isParticipated = isParticipated(vote, member);
                    return StudyVoteResponseDTO.VoteInfoDTO.toDTO(vote, isParticipated);
                })
                .toList();

        return StudyVoteResponseDTO.VoteListDTO.toDTO(studyId, votesInProgress, votesInCompletion);
    }

    /**
     * 스터디 회원의 투표 참여 여부를 확인하는 메서드입니다. getAllVotes에서 사용되는 내부 메서드입니다.
     *
     * @param vote        스터디에서 생성한 투표의 아이디를 입력 받습니다.
     * @param loginMember 로그인한 회원의 정보를 입력 받습니다.
     * @return 투표 참여 여부를 true or false로 반환합니다.
     */
    private boolean isParticipated(Vote vote, Member loginMember) {
        // 투표 참여 여부 확인
        boolean isParticipated = false;
        for (VoteOption voteOption : vote.getVoteOptions()) {
            if (voteParticipantRepository.existsByMemberIdAndVoteOptionId(loginMember.getId(), voteOption.getId())) {
                isParticipated = true;
            }
        }
        return isParticipated;
    }

    /**
     * 입력 받은 스터디 투표가 종료되었는지 확인하는 메서드입니다. (클라이언트에서 투표 불러오기 API를 호출할 때 스터디 종료 여부에 따라 Response DTO가 바뀌어야 하기 때문에 필요한
     * 메서드입니다)
     *
     * @param voteId 스터디에서 생성한 투표의 아이디를 입력 받습니다.
     * @return 투표 종료 여부를 true or false로 반환합니다.
     */
    @Override
    public Boolean getIsCompleted(Long voteId) {
        return voteRepository.existsByIdAndFinishedAtBefore(voteId, LocalDateTime.now());
    }

    /**
     * 종료된 투표의 정보를 불러오는 메서드입니다.
     *
     * @param studyId 타겟 스터디의 아이디를 입력 받습니다.
     * @param voteId  스터디에서 생성한 투표의 아이디를 입력 받습니다.
     * @return 종료된 투표의 아이디, 생성자, 제목, 항목별 투표 인원수, 전체 참여자 수, 종료 일시를 반환합니다.
     */
    @Override
    public StudyVoteResponseDTO.CompletedVoteDTO getVoteInCompletion(Long studyId, Long voteId) {

        //=== Exception ===//
        Long memberId = SecurityUtils.getCurrentUserId();
        SecurityUtils.verifyUserId(memberId);

        memberRepository.findById(memberId)
                .orElseThrow(() -> new StudyHandler(ErrorStatus._MEMBER_NOT_FOUND));
        studyRepository.findById(studyId)
                .orElseThrow(() -> new StudyHandler(ErrorStatus._STUDY_NOT_FOUND));
        Vote vote = voteRepository.findById(voteId)
                .orElseThrow(() -> new StudyHandler(ErrorStatus._STUDY_VOTE_NOT_FOUND));

        // 로그인한 회원이 스터디 회원인지 확인
        studyMemberRepository.findByMemberIdAndStudyIdAndStatus(memberId, studyId, StudyApplicationStatus.APPROVED)
                .orElseThrow(() -> new StudyHandler(ErrorStatus._STUDY_MEMBER_NOT_FOUND));

        // 해당 스터디의 투표인지 확인
        voteRepository.findByIdAndStudyId(voteId, studyId)
                .orElseThrow(() -> new StudyHandler(ErrorStatus._STUDY_VOTE_NOT_FOUND));

        //=== Feature ===//
        return StudyVoteResponseDTO.CompletedVoteDTO.toDTO(vote);

    }

    /**
     * 진행중인 투표의 정보를 불러오는 메서드입니다.
     *
     * @param studyId 타겟 스터디의 아이디를 입력 받습니다.
     * @param voteId  스터디에서 생성한 투표의 아이디를 입력 받습니다.
     * @return 진행중인 투표의 아이디, 생성자, 제목, 항목 리스트, 복수 선택 가능 여부, 종료 일시, 로그인한 회원의 참여 여부를 반환합니다.
     */
    @Override
    public StudyVoteResponseDTO.VoteDTO getVoteInProgress(Long studyId, Long voteId) {

        //=== Exception ===//
        Long memberId = SecurityUtils.getCurrentUserId();
        SecurityUtils.verifyUserId(memberId);

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new StudyHandler(ErrorStatus._MEMBER_NOT_FOUND));
        studyRepository.findById(studyId)
                .orElseThrow(() -> new StudyHandler(ErrorStatus._STUDY_NOT_FOUND));
        Vote vote = voteRepository.findById(voteId)
                .orElseThrow(() -> new StudyHandler(ErrorStatus._STUDY_VOTE_NOT_FOUND));

        // 해당 스터디의 투표인지 확인
        voteRepository.findByIdAndStudyId(voteId, studyId)
                .orElseThrow(() -> new StudyHandler(ErrorStatus._STUDY_VOTE_NOT_FOUND));

        // 로그인한 회원이 스터디 회원인지 확인
        studyMemberRepository.findByMemberIdAndStudyIdAndStatus(memberId, studyId, StudyApplicationStatus.APPROVED)
                .orElseThrow(() -> new StudyHandler(ErrorStatus._STUDY_MEMBER_NOT_FOUND));

        //=== Feature ===//
        return StudyVoteResponseDTO.VoteDTO.toDTO(vote, member);
    }

    /**
     * 마감된 투표에 대해 항목별 투표 현황을 불러오는 메서드입니다.
     *
     * @param studyId 타겟 스터디의 아이디를 입력 받습니다.
     * @param voteId  마감된 스터디 투표의 아이디를 입력 받습니다.
     * @return 마감된 투표의 아이디와 제목, 항목별 투표 회원 목록을 반환합니다.
     */
    @Override
    public StudyVoteResponseDTO.CompletedVoteDetailDTO getCompletedVoteDetail(Long studyId, Long voteId) {

        //=== Exception ===//
        Long memberId = SecurityUtils.getCurrentUserId();
        SecurityUtils.verifyUserId(memberId);

        memberRepository.findById(memberId)
                .orElseThrow(() -> new StudyHandler(ErrorStatus._MEMBER_NOT_FOUND));
        Study study = studyRepository.findById(studyId)
                .orElseThrow(() -> new StudyHandler(ErrorStatus._STUDY_NOT_FOUND));
        Vote vote = voteRepository.findById(voteId)
                .orElseThrow(() -> new StudyHandler(ErrorStatus._STUDY_VOTE_NOT_FOUND));

        // 해당 스터디의 투표인지 확인
        voteRepository.findByIdAndStudyId(voteId, studyId)
                .orElseThrow(() -> new StudyHandler(ErrorStatus._STUDY_VOTE_NOT_FOUND));

        // 로그인한 회원이 스터디 회원인지 확인
        studyMemberRepository.findByMemberIdAndStudyIdAndStatus(memberId, studyId, StudyApplicationStatus.APPROVED)
                .orElseThrow(() -> new StudyHandler(ErrorStatus._STUDY_MEMBER_NOT_FOUND));

        // 마감된 투표인지 확인
        if (!voteRepository.existsByIdAndFinishedAtBefore(voteId, LocalDateTime.now())) {
            throw new StudyHandler(ErrorStatus._STUDY_VOTE_NOT_COMPLETED);
        }

        //=== Feature ===//
        return StudyVoteResponseDTO.CompletedVoteDetailDTO.toDTO(vote);
    }
}
