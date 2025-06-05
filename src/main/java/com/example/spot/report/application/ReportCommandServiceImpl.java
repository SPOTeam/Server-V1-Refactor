package com.example.spot.report.application;

import com.example.spot.common.api.code.status.ErrorStatus;
import com.example.spot.common.api.exception.handler.MemberHandler;
import com.example.spot.common.api.exception.handler.PostHandler;
import com.example.spot.common.api.exception.handler.StudyHandler;
import com.example.spot.common.security.utils.SecurityUtils;
import com.example.spot.member.domain.Member;
import com.example.spot.member.domain.MemberRepository;
import com.example.spot.member.presentation.dto.MemberResponseDTO;
import com.example.spot.post.domain.Post;
import com.example.spot.post.domain.PostRepository;
import com.example.spot.post.domain.enums.PostStatus;
import com.example.spot.report.domain.*;
import com.example.spot.report.presentation.dto.PostReportResponse;
import com.example.spot.story.domain.Story;
import com.example.spot.story.domain.StoryRepository;
import com.example.spot.story.web.dto.response.StoryResponseDTO;
import com.example.spot.study.domain.Study;
import com.example.spot.study.domain.StudyRepository;
import com.example.spot.study.domain.enums.StudyApplicationStatus;
import com.example.spot.study.domain.repository.StudyMemberRepository;
import com.example.spot.report.presentation.dto.StudyMemberReportDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ReportCommandServiceImpl implements ReportCommandService {

    private final MemberRepository memberRepository;
    private final PostRepository postRepository;
    private final StudyRepository studyRepository;
    private final StudyMemberRepository studyMemberRepository;
    private final StoryRepository storyRepository;

    private final MemberReportRepository memberReportRepository;
    private final StoryReportRepository storyReportRepository;
    private final PostReportRepository postReportRepository;

    @Override
    public PostReportResponse reportPost(Long postId, Long memberId) {

        // 동일한 게시글에 대한 중복 신고 방지
        if (postReportRepository.existsByPostIdAndMemberId(postId, memberId)) {
            throw new PostHandler(ErrorStatus._POST_ALREADY_REPORTED);
        }

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberHandler(ErrorStatus._MEMBER_NOT_FOUND));

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostHandler(ErrorStatus._POST_NOT_FOUND));

        if (post.getMember().getId().equals(memberId)) {
            throw new PostHandler(ErrorStatus._POST_REPORT_SELF);
        }

        PostReport postReport = PostReport.builder()
                .postStatus(PostStatus.신고접수)
                .post(post)
                .member(member).build();

        postReportRepository.save(postReport);

        return PostReportResponse.toDTO(postId, memberId);
    }

    /**
     * 스터디원을 신고하고 신고 내역을 저장하는 메서드입니다.
     * @param studyId 타겟 스터디의 아이디를 입력 받습니다.
     * @param memberId 신고할 회원의 아이디를 입력 받습니다.
     * @param studyMemberReportDTO 신고 사유를 입력 받습니다.
     * @return 신고를 당한 회원의 아이디와 이름을 반환합니다.
     */
    @Override
    public MemberResponseDTO.ReportedMemberDTO reportStudyMember(Long studyId, Long memberId, StudyMemberReportDTO studyMemberReportDTO) {

        //=== Exception ===//
        Long reporterId = SecurityUtils.getCurrentUserId();
        SecurityUtils.verifyUserId(reporterId);

        Member reporter = memberRepository.findById(reporterId)
                .orElseThrow(() -> new MemberHandler(ErrorStatus._MEMBER_NOT_FOUND));
        Study study = studyRepository.findById(studyId)
                .orElseThrow(() -> new StudyHandler(ErrorStatus._STUDY_NOT_FOUND));
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberHandler(ErrorStatus._MEMBER_NOT_FOUND));

        // 로그인한 회원이 스터디 회원인지 확인
        studyMemberRepository.findByMemberIdAndStudyIdAndStatus(reporterId, studyId, StudyApplicationStatus.APPROVED)
                .orElseThrow(() -> new StudyHandler(ErrorStatus._STUDY_MEMBER_NOT_FOUND));

        // 신고당한 회원이 스터디 회원인지 확인
        studyMemberRepository.findByMemberIdAndStudyIdAndStatus(memberId, studyId, StudyApplicationStatus.APPROVED)
                .orElseThrow(() -> new StudyHandler(ErrorStatus._STUDY_MEMBER_NOT_FOUND));

        // 자기 자신을 신고할 수 없음
        if (reporterId.equals(memberId)) {
            throw new StudyHandler(ErrorStatus._STUDY_MEMBER_REPORT_INVALID);
        }


        //=== Feature ===//
        MemberReport memberReport = MemberReport.builder()
                .content(studyMemberReportDTO.getContent())
                .member(member)
                .build();

        memberReport = memberReportRepository.save(memberReport);
        member.addMemberReport(memberReport);

        return MemberResponseDTO.ReportedMemberDTO.toDTO(member);
    }

    /**
     * 스터디 게시글을 신고하고 신고 내역을 저장하는 메서드입니다.
     * @param studyId 타겟 스터디의 아이디를 입력합니다.
     * @param postId 신고할 게시글의 아이디를 입력합니다.
     * @return 신고를 당한 스터디 게시글의 아이디와 제목을 반환합니다.
     */
    @Override
    public StoryResponseDTO.StoryPreviewDTO reportStudyPost(Long studyId, Long postId) {

        //=== Exception ===//
        Long reporterId = SecurityUtils.getCurrentUserId();
        SecurityUtils.verifyUserId(reporterId);

        memberRepository.findById(reporterId)
                .orElseThrow(() -> new MemberHandler(ErrorStatus._MEMBER_NOT_FOUND));
        studyRepository.findById(studyId)
                .orElseThrow(() -> new StudyHandler(ErrorStatus._STUDY_NOT_FOUND));
        Story story = storyRepository.findById(postId)
                .orElseThrow(() -> new StudyHandler(ErrorStatus._STUDY_NOT_FOUND));

        // 로그인한 회원이 스터디 회원인지 확인
        studyMemberRepository.findByMemberIdAndStudyIdAndStatus(reporterId, studyId, StudyApplicationStatus.APPROVED)
                .orElseThrow(() -> new StudyHandler(ErrorStatus._STUDY_MEMBER_NOT_FOUND));

        // 해당 스터디의 게시글인지 확인
        storyRepository.findByIdAndStudyId(postId, studyId)
                .orElseThrow(() -> new StudyHandler(ErrorStatus._STUDY_POST_NOT_FOUND));

        //=== Feature ===//
        StoryReport storyReport = StoryReport.builder()
                .story(story)
                .build();

        storyReport = storyReportRepository.save(storyReport);
        story.addStudyPostReport(storyReport);

        return StoryResponseDTO.StoryPreviewDTO.toDTO(story);
    }
}
