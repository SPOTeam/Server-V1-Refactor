package com.example.spot.story.application.service.impl;

import com.example.spot.common.api.code.status.ErrorStatus;
import com.example.spot.common.api.exception.GeneralException;
import com.example.spot.common.api.exception.handler.MemberHandler;
import com.example.spot.common.api.exception.handler.StudyHandler;
import com.example.spot.common.security.utils.SecurityUtils;
import com.example.spot.member.domain.Member;
import com.example.spot.member.infrastructure.MemberRepository;
import com.example.spot.story.application.service.StoryQueryService;
import com.example.spot.story.domain.entity.Story;
import com.example.spot.story.infrastructure.repository.StoryRepository;
import com.example.spot.story.domain.entity.StoryComment;
import com.example.spot.story.domain.enums.StoryCategory;
import com.example.spot.story.domain.enums.StoryCategoryQuery;
import com.example.spot.story.infrastructure.repository.LikedStoryRepository;
import com.example.spot.story.infrastructure.repository.StoryCommentRepository;
import com.example.spot.story.domain.dto.response.StoryCommentResponseDTO;
import com.example.spot.story.domain.dto.response.StoryResponseDTO;
import com.example.spot.study.domain.Study;
import com.example.spot.study.domain.StudyRepository;
import com.example.spot.study.domain.enums.StudyApplicationStatus;
import com.example.spot.study.domain.repository.StudyMemberRepository;
import com.example.spot.study.presentation.dto.response.StudyImageResponseDTO;
import java.util.Comparator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class StoryQueryServiceImpl implements StoryQueryService {

    private final StoryCommentRepository storyCommentRepository;
    private final LikedStoryRepository likedStoryRepository;
    @Value("${image.post.anonymous.profile}")
    private String defaultImage;

    private final MemberRepository memberRepository;
    private final StudyRepository studyRepository;
    private final StoryRepository storyRepository;
    private final StudyMemberRepository studyMemberRepository;

    /* ----------------------------- 스터디 게시글 관련 API ------------------------------------- */

    /**
     * 특정 테마(카테고리)에 속한 스터디 게시글 목록을 조회하는 메서드입니다. 오프셋 기반 페이징이 적용되어 있습니다.
     *
     * @param pageRequest        페이징에 필요한 페이지 번호와 페이지 사이즈 정보를 입력 받습니다.
     * @param studyId            게시글 목록을 조회할 타겟 스터디의 아이디를 입력 받습니다.
     * @param storyCategoryQuery 게시글 테마를 입력 받습니다. themeQuery는 null일 수 있습니다.
     * @return 조건에 맞는 스터디 게시글 목록을 반환합니다. 1. themeQuery가 있는 경우 해당 테마의 게시글 목록을 반환합니다. 2. themeQuery가 null인 경우 필터링 없이 게시글
     * 목록을 반환합니다.
     */
    @Override
    public StoryResponseDTO.StoryListDTO getAllPosts(PageRequest pageRequest, Long studyId,
                                                     StoryCategoryQuery storyCategoryQuery) {

        Long memberId = SecurityUtils.getCurrentUserId();
        SecurityUtils.verifyUserId(memberId);

        memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberHandler(ErrorStatus._MEMBER_NOT_FOUND));
        studyRepository.findById(studyId)
                .orElseThrow(() -> new StudyHandler(ErrorStatus._STUDY_NOT_FOUND));

        studyMemberRepository.findByMemberIdAndStudyIdAndStatus(memberId, studyId, StudyApplicationStatus.APPROVED)
                .orElseThrow(() -> new StudyHandler(ErrorStatus._STUDY_MEMBER_NOT_FOUND));

        List<Story> stories;
        Long totalPosts;

        // query가 없는 경우
        if (storyCategoryQuery == null) {
            stories = storyRepository.findAllByStudyId(studyId, pageRequest);
            totalPosts = storyRepository.countByStudyId(studyId);
        }
        // query가 ANNOUNCEMENT인 경우
        else if (storyCategoryQuery.equals(StoryCategoryQuery.ANNOUNCEMENT)) {
            stories = storyRepository.findAnnouncementsByStudyId(studyId, pageRequest);
            totalPosts = storyRepository.countByStudyIdAndIsAnnouncement(studyId, Boolean.TRUE);
        }
        // query가 스터디 테마인 경우
        else {
            StoryCategory storyCategory = storyCategoryQuery.toCategory();
            stories = storyRepository.findAllByStudyIdAndTheme(studyId, storyCategory, pageRequest);
            totalPosts = storyRepository.countByStudyIdAndStoryCategory(studyId, storyCategory);
        }

        return StoryResponseDTO.StoryListDTO.builder()
                .studyId(studyId)
                .posts(stories.stream()
                        .map(studyPost -> {
                            if (likedStoryRepository.existsByMemberIdAndStoryId(memberId, studyPost.getId())) {
                                return StoryResponseDTO.StoryDTO.toDTO(studyPost, true);
                            } else {
                                return StoryResponseDTO.StoryDTO.toDTO(studyPost, false);
                            }
                        })
                        .toList())
                // (페이지 수) = ceil((전체 게시글 수) / (페이지별 게시글 수))
                .totalPages((long) Math.ceil((double) totalPosts / pageRequest.getPageSize()))
                .build();

    }

    /**
     * 스터디 게시판의 특정 게시글을 조회하는 메서드입니다.
     *
     * @param studyId     게시글을 조회할 타겟 스터디의 아이디를 입력 받습니다.
     * @param postId      조회할 타겟 게시글의 아이디를 입력 받습니다.
     * @param likeOrScrap
     * @return 스터디 게시글의 정보를 반환합니다.
     */
    @Override
    @Transactional(readOnly = false)
    public StoryResponseDTO.StoryDetailDTO getPost(Long studyId, Long postId, Boolean likeOrScrap) {

        //=== Exception ===//
        Long memberId = SecurityUtils.getCurrentUserId();
        SecurityUtils.verifyUserId(memberId);

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberHandler(ErrorStatus._MEMBER_NOT_FOUND));
        Study study = studyRepository.findById(studyId)
                .orElseThrow(() -> new StudyHandler(ErrorStatus._STUDY_NOT_FOUND));
        Story story = storyRepository.findById(postId)
                .orElseThrow(() -> new StudyHandler(ErrorStatus._STUDY_POST_NOT_FOUND));

        // 해당 스터디의 게시글인지 확인
        storyRepository.findByIdAndStudyId(postId, studyId)
                .orElseThrow(() -> new StudyHandler(ErrorStatus._STUDY_POST_NOT_FOUND));

        // 로그인한 회원이 스터디 회원인지 확인
        studyMemberRepository.findByMemberIdAndStudyIdAndStatus(memberId, studyId, StudyApplicationStatus.APPROVED)
                .orElseThrow(() -> new StudyHandler(ErrorStatus._STUDY_MEMBER_NOT_FOUND));

        //=== Feature ===//

        // 조회수 증가는 일반 조회 시에만 실행
        if (!likeOrScrap) {
            story.plusHitNum();
        }

        story = storyRepository.save(story);
        memberRepository.save(member);
        studyRepository.save(study);

        Integer commentNum = storyCommentRepository.findAllByStoryId(postId).size();
        boolean isLiked = likedStoryRepository.existsByMemberIdAndStoryId(memberId, story.getId());
        boolean isWriter = story.getMember().getId().equals(memberId);
        return StoryResponseDTO.StoryDetailDTO.toDTO(story, commentNum, isLiked, isWriter);
    }

    /**
     * 스터디 최근 공지사항을 1개 조회합니다.
     *
     * @param studyId 스터디 ID
     * @return 제목과 내용을 반환합니다.
     * @throws GeneralException 스터디 공지사항이 존재하지 않는 경우
     * @throws GeneralException 스터디 멤버가 아닌 경우
     */
    @Override
    public StoryResponseDTO.StoryContentDTO findStudyAnnouncementPost(Long studyId) {

        // 로그인한 회원이 해당 스터디 회원인지 확인
        if (!isMember(SecurityUtils.getCurrentUserId(), studyId)) {
            throw new GeneralException(ErrorStatus._ONLY_STUDY_MEMBER_CAN_ACCESS_ANNOUNCEMENT_POST);
        }

        // 스터디 공지사항 조회
        Story story = storyRepository.findByStudyIdAndIsAnnouncement(
                studyId, true).orElseThrow(() -> new GeneralException(ErrorStatus._STUDY_POST_NOT_FOUND));

        // DTO로 변환하여 반환
        return StoryResponseDTO.StoryContentDTO
                .builder()
                .title(story.getTitle())
                .content(story.getContent()).build();
    }

    /**
     * 회원이 스터디 구성원인지 확인합니다.
     *
     * @param memberId 확인 하려는 회원 ID
     * @param studyId  확인 하려는 스터디 ID
     * @return 스터디 참여 여부를 반환합니다.
     */
    private boolean isMember(Long memberId, Long studyId) {
        return studyMemberRepository.findByMemberIdAndStudyIdAndStatus(memberId, studyId,
                StudyApplicationStatus.APPROVED).isPresent();
    }

    /* ----------------------------- 스터디 게시글 댓글 관련 API ------------------------------------- */

    /**
     * 특정 스터디 게시글의 모든 댓글을 조회하는 메서드입니다.
     *
     * @param studyId 게시글이 작성된 타겟 스터디의 아이디를 입력 받습니다.
     * @param postId  댓글이 작성된 게시글의 아이디를 입력 받습니다.
     * @return 스터디 게시글에 작성된 댓글의 목록을 반환합니다. 하나의 댓글에는 해당 댓글에 대한 답글 목록이 포함되어 있습니다.
     */
    @Override
    public StoryCommentResponseDTO.ReplyListDTO getAllComments(Long studyId, Long postId) {

        //=== Exception ===//
        Long memberId = SecurityUtils.getCurrentUserId();
        SecurityUtils.verifyUserId(memberId);

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberHandler(ErrorStatus._MEMBER_NOT_FOUND));
        studyRepository.findById(studyId)
                .orElseThrow(() -> new StudyHandler(ErrorStatus._STUDY_NOT_FOUND));
        Story story = storyRepository.findById(postId)
                .orElseThrow(() -> new StudyHandler(ErrorStatus._STUDY_POST_NOT_FOUND));

        // 로그인한 회원이 스터디 회원인지 확인
        studyMemberRepository.findByMemberIdAndStudyIdAndStatus(memberId, studyId, StudyApplicationStatus.APPROVED)
                .orElseThrow(() -> new StudyHandler(ErrorStatus._STUDY_MEMBER_NOT_FOUND));

        // 해당 스터디의 게시글인지 확인
        storyRepository.findByIdAndStudyId(postId, studyId)
                .orElseThrow(() -> new StudyHandler(ErrorStatus._STUDY_POST_NOT_FOUND));

        //=== Feature ===//
        List<StoryComment> storyComments = storyCommentRepository.findAllByStoryId(story.getId()).stream()
                .filter(studyPostComment -> studyPostComment.getParentComment() == null)
                .sorted(Comparator.comparing(StoryComment::getCreatedAt))
                .toList();

        return StoryCommentResponseDTO.ReplyListDTO.toDTO(story.getId(), storyComments, member, defaultImage);
    }


    /* ----------------------------- 스터디 갤러리 관련 API ------------------------------------- */

    /**
     * 스터디 게시판에 업로드한 이미지 목록을 불러오는 메서드입니다.
     *
     * @param studyId     타겟 스터디의 아이디를 입력 받습니다.
     * @param pageRequest 페이징에 필요한 페이지 번호와 크기를 입력 받습니다.
     * @return 스터디 아이디와 해당 스터디에 업로드된 이미지 목록을 반환합니다.
     */
    @Override
    public StudyImageResponseDTO.ImageListDTO getAllStudyImages(Long studyId, PageRequest pageRequest) {

        //=== Exception ===//
        Long memberId = SecurityUtils.getCurrentUserId();
        SecurityUtils.verifyUserId(memberId);
        studyRepository.findById(studyId)
                .orElseThrow(() -> new StudyHandler(ErrorStatus._STUDY_NOT_FOUND));
        memberRepository.findById(memberId)
                .orElseThrow(() -> new StudyHandler(ErrorStatus._MEMBER_NOT_FOUND));

        // 로그인한 회원이 스터디 회원인지 확인
        studyMemberRepository.findByMemberIdAndStudyIdAndStatus(memberId, studyId, StudyApplicationStatus.APPROVED)
                .orElseThrow(() -> new StudyHandler(ErrorStatus._STUDY_MEMBER_NOT_FOUND));

        //=== Feature ===//
        List<StudyImageResponseDTO.ImageDTO> images = storyRepository.findAllByStudyId(studyId, pageRequest)
                .stream()
                .sorted(Comparator.comparing(Story::getCreatedAt).reversed())
                .flatMap(studyPost -> studyPost.getImages().stream())
                .map(StudyImageResponseDTO.ImageDTO::toDTO)
                .toList();

        return StudyImageResponseDTO.ImageListDTO.toDTO(studyId, images);

    }

}
