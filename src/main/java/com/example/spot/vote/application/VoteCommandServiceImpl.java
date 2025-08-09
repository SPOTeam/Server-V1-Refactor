package com.example.spot.vote.application;

import com.example.spot.common.api.code.status.ErrorStatus;
import com.example.spot.common.api.exception.handler.MemberHandler;
import com.example.spot.common.api.exception.handler.StudyHandler;
import com.example.spot.common.security.utils.SecurityUtils;
import com.example.spot.member.domain.Member;
import com.example.spot.member.infrastructure.jpa.MemberRepository;
import com.example.spot.study.domain.Study;
import com.example.spot.study.domain.StudyRepository;
import com.example.spot.study.domain.enums.StudyApplicationStatus;
import com.example.spot.study.domain.repository.StudyMemberRepository;
import com.example.spot.vote.domain.Vote;
import com.example.spot.vote.domain.VoteRepository;
import com.example.spot.vote.domain.association.VoteOption;
import com.example.spot.vote.domain.association.VoteParticipant;
import com.example.spot.vote.domain.repository.VoteOptionRepository;
import com.example.spot.vote.domain.repository.VoteParticipantRepository;
import com.example.spot.vote.presentation.dto.request.StudyVoteRequestDTO;
import com.example.spot.vote.presentation.dto.response.StudyVoteResponseDTO;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
@Transactional
public class VoteCommandServiceImpl implements VoteCommandService {

    private final MemberRepository memberRepository;
    private final StudyRepository studyRepository;
    private final VoteRepository voteRepository;
    private final VoteOptionRepository voteOptionRepository;
    private final StudyMemberRepository studyMemberRepository;
    private final VoteParticipantRepository voteParticipantRepository;


    /**
     * 스터디 투표를 생성하는 메서드입니다.
     *
     * @param studyId 타겟 스터디의 아이디를 입력 받습니다.
     * @param voteDTO 생성할 투표의 제목, 항목 목록, 중복 선택 가능 여부, 종료 일시를 입력 받습니다.
     * @return 생성된 투표의 아이디와 제목을 반환합니다.
     */
    @Override
    public StudyVoteResponseDTO.VotePreviewDTO createVote(Long studyId, StudyVoteRequestDTO.VoteDTO voteDTO) {

        //=== Exception ===//
        Long memberId = SecurityUtils.getCurrentUserId();
        SecurityUtils.verifyUserId(memberId);

        Member loginMember = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberHandler(ErrorStatus._MEMBER_NOT_FOUND));
        Study study = studyRepository.findById(studyId)
                .orElseThrow(() -> new StudyHandler(ErrorStatus._STUDY_NOT_FOUND));

        // 로그인한 회원이 스터디 회원인지 확인
        studyMemberRepository.findByMemberIdAndStudyIdAndStatus(memberId, studyId, StudyApplicationStatus.APPROVED)
                .orElseThrow(() -> new StudyHandler(ErrorStatus._STUDY_MEMBER_NOT_FOUND));

        //=== Feature ===//
        Vote vote = Vote.builder()
                .study(study)
                .member(loginMember)
                .title(voteDTO.getTitle())
                .isMultipleChoice(voteDTO.getIsMultipleChoice())
                .finishedAt(voteDTO.getFinishedAt())
                .build();

        // Vote 저장
        vote = voteRepository.save(vote);
        // Option 저장
        vote = createOption(vote, voteDTO);
        // 연관관계 매핑
        study.addVote(vote);

        return StudyVoteResponseDTO.VotePreviewDTO.toDTO(vote);
    }

    /**
     * 스터디 투표의 항목을 생성하는 메서드입니다. createVote 메서드 내부에서 사용되는 메서드입니다.
     *
     * @param vote    항목을 생성할 타겟 투표를 입력 받습니다.
     * @param voteDTO 생성할 투표의 제목, 항목 목록, 중복 선택 가능 여부, 종료 일시를 입력 받습니다.
     * @return 투표 객체를 반환합니다.
     */
    private Vote createOption(Vote vote, StudyVoteRequestDTO.VoteDTO voteDTO) {
        voteDTO.getOptions()
                .forEach(stringOption -> {
                    VoteOption voteOption = VoteOption.builder()
                            .vote(vote)
                            .content(stringOption)
                            .build();
                    voteOption = voteOptionRepository.save(voteOption);
                    vote.addOption(voteOption);
                });
        return voteRepository.save(vote);
    }

    /**
     * 특정 항목에 투표하기 위한 메서드입니다.
     *
     * @param studyId        타겟 스터디의 아이디를 입력 받습니다.
     * @param voteId         타겟 투표의 아이디를 입력 받습니다.
     * @param votedOptionDTO 회원이 투표한 항목의 아이디 목록을 입력 받습니다.
     * @return 투표 아이디, 회원 아이디, 투표한 항목 목록을 반환합니다.
     */
    @Override
    public StudyVoteResponseDTO.VotedOptionDTO vote(Long studyId, Long voteId,
                                                    StudyVoteRequestDTO.VotedOptionDTO votedOptionDTO) {

        //=== Exception ===//
        Long memberId = SecurityUtils.getCurrentUserId();
        SecurityUtils.verifyUserId(memberId);

        Member loginMember = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberHandler(ErrorStatus._MEMBER_NOT_FOUND));
        studyRepository.findById(studyId)
                .orElseThrow(() -> new StudyHandler(ErrorStatus._STUDY_NOT_FOUND));
        Vote vote = voteRepository.findById(voteId)
                .orElseThrow(() -> new StudyHandler(ErrorStatus._STUDY_VOTE_NOT_FOUND));
        voteRepository.findByIdAndStudyId(voteId, studyId)
                .orElseThrow(() -> new StudyHandler(ErrorStatus._STUDY_VOTE_NOT_FOUND));

        // 로그인한 회원이 스터디 회원인지 확인
        studyMemberRepository.findByMemberIdAndStudyIdAndStatus(memberId, studyId, StudyApplicationStatus.APPROVED)
                .orElseThrow(() -> new StudyHandler(ErrorStatus._STUDY_MEMBER_NOT_FOUND));

        // 중복 선택이 허용되지 않는 투표는 여러 개의 option을 선택할 수 없음
        if (!vote.getIsMultipleChoice() && votedOptionDTO.getOptionIdList().size() > 1) {
            throw new StudyHandler(ErrorStatus._STUDY_VOTE_MULTIPLE_CHOICE_NOT_VALID);
        }

        // 한 번 참여한 투표는 다시 참여할 수 없음
        voteOptionRepository.findAllByVoteId(voteId)
                .forEach(option -> {
                    if (voteParticipantRepository.existsByMemberIdAndVoteOptionId(loginMember.getId(),
                            option.getId())) {
                        throw new StudyHandler(ErrorStatus._STUDY_VOTE_RE_PARTICIPATION_INVALID);
                    }
                });

        //=== Feature ===//
        List<VoteParticipant> voteParticipants = votedOptionDTO.getOptionIdList().stream()
                .map(optionId -> {
                    VoteOption votedVoteOption = voteOptionRepository.findById(optionId)
                            .orElseThrow(() -> new StudyHandler(ErrorStatus._STUDY_VOTE_OPTION_NOT_FOUND));
                    voteOptionRepository.findByIdAndVoteId(optionId, voteId)
                            .orElseThrow(() -> new StudyHandler(ErrorStatus._STUDY_VOTE_OPTION_NOT_FOUND));

                    VoteParticipant voteParticipant = VoteParticipant.builder()
                            .member(loginMember)
                            .voteOption(votedVoteOption)
                            .build();

                    voteParticipant = voteParticipantRepository.save(voteParticipant);
                    votedVoteOption.addMemberVote(voteParticipant);

                    return voteParticipant;
                })
                .toList();

        return StudyVoteResponseDTO.VotedOptionDTO.toDTO(vote, loginMember, voteParticipants);
    }

    /**
     * 투표를 편집하는 메서드입니다.
     *
     * @param studyId 타겟 스터디의 아이디를 입력 받습니다.
     * @param voteId  편집할 투표의 아이디를 입력 받습니다.
     * @param voteDTO 편집된 투표의 제목, 항목 목록, 복수 선택 가능 여부, 종료 일시를 입력 받습니다.
     * @return 편집된 투표의 아이디와 제목을 반환합니다.
     */
    @Override
    public StudyVoteResponseDTO.VotePreviewDTO updateVote(Long studyId, Long voteId,
                                                          StudyVoteRequestDTO.VoteUpdateDTO voteDTO) {

        //=== Exception ===//
        Long memberId = SecurityUtils.getCurrentUserId();
        SecurityUtils.verifyUserId(memberId);

        Member loginMember = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberHandler(ErrorStatus._MEMBER_NOT_FOUND));
        Study study = studyRepository.findById(studyId)
                .orElseThrow(() -> new StudyHandler(ErrorStatus._STUDY_NOT_FOUND));
        Vote vote = voteRepository.findById(voteId)
                .orElseThrow(() -> new StudyHandler(ErrorStatus._STUDY_VOTE_NOT_FOUND));

        // 로그인한 회원이 스터디 회원인지 확인
        studyMemberRepository.findByMemberIdAndStudyIdAndStatus(memberId, studyId, StudyApplicationStatus.APPROVED)
                .orElseThrow(() -> new StudyHandler(ErrorStatus._STUDY_MEMBER_NOT_FOUND));

        // 로그인한 회원이 투표 생성자인지 확인
        if (!loginMember.equals(vote.getMember())) {
            throw new StudyHandler(ErrorStatus._STUDY_VOTE_CREATOR_NOT_AUTHORIZED);
        }

        // 한 명이라도 투표에 참여했으면 투표 편집 불가
        voteOptionRepository.findAllByVoteId(voteId)
                .forEach(option -> {
                    if (voteParticipantRepository.existsByVoteOptionId(option.getId())) {
                        throw new StudyHandler(ErrorStatus._STUDY_VOTE_IS_IN_PROGRESS);
                    }
                });

        //=== Feature ===//
        for (StudyVoteRequestDTO.OptionDTO optionDTO : voteDTO.getOptions()) {
            VoteOption voteOption = voteOptionRepository.findById(optionDTO.getOptionId())
                    .orElseThrow(() -> new StudyHandler(ErrorStatus._STUDY_VOTE_OPTION_NOT_FOUND));
            voteOption.setContent(optionDTO.getContent());
            voteOption = voteOptionRepository.save(voteOption);
            vote.updateOption(voteOption);
        }

        vote.updateVote(voteDTO.getTitle(), voteDTO.getIsMultipleChoice(), voteDTO.getFinishedAt());
        vote = voteRepository.save(vote);
        study.updateVote(vote);

        return StudyVoteResponseDTO.VotePreviewDTO.toDTO(vote);
    }

    /**
     * 투표를 삭제하는 메서드입니다.
     *
     * @param studyId 타겟 스터디의 아이디를 입력 받습니다.
     * @param voteId  삭제할 투표의 아이디를 입력 받습니다.
     * @return 삭제된 투표의 아이디와 제목을 반환합니다.
     */
    @Override
    public StudyVoteResponseDTO.VotePreviewDTO deleteVote(Long studyId, Long voteId) {

        //=== Exception ===//
        Long memberId = SecurityUtils.getCurrentUserId();
        SecurityUtils.verifyUserId(memberId);

        Member loginMember = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberHandler(ErrorStatus._MEMBER_NOT_FOUND));
        Study study = studyRepository.findById(studyId)
                .orElseThrow(() -> new StudyHandler(ErrorStatus._STUDY_NOT_FOUND));
        Vote vote = voteRepository.findById(voteId)
                .orElseThrow(() -> new StudyHandler(ErrorStatus._STUDY_VOTE_NOT_FOUND));
        voteRepository.findByIdAndStudyId(voteId, studyId)
                .orElseThrow(() -> new StudyHandler(ErrorStatus._STUDY_VOTE_NOT_FOUND));

        // 로그인한 회원이 스터디 회원인지 확인
        studyMemberRepository.findByMemberIdAndStudyIdAndStatus(memberId, studyId, StudyApplicationStatus.APPROVED)
                .orElseThrow(() -> new StudyHandler(ErrorStatus._STUDY_MEMBER_NOT_FOUND));

        // 로그인한 회원이 투표 생성자인지 확인
        if (!loginMember.equals(vote.getMember())) {
            throw new StudyHandler(ErrorStatus._STUDY_VOTE_CREATOR_NOT_AUTHORIZED);
        }

        //=== Feature ===//
        deleteOptions(voteId);
        study.deleteVote(vote);
        voteRepository.delete(vote);

        return StudyVoteResponseDTO.VotePreviewDTO.toDTO(vote);
    }

    /**
     * 모든 투표 항목을 삭제하는 메서드입니다. deleteVote 메서드 내부에서 호출되는 메서드입니다.
     *
     * @param voteId 항목을 삭제할 타겟 투표의 아이디를 입력 받습니다.
     */
    private void deleteOptions(Long voteId) {
        List<VoteOption> voteOptions = voteOptionRepository.findAllByVoteId(voteId);
        voteOptions.forEach(option -> {
            option.deleteAllMemberVotes();
            voteParticipantRepository.deleteAll(voteParticipantRepository.findAllByVoteOptionId(option.getId()));
            voteOptionRepository.delete(option);
        });
    }

}
