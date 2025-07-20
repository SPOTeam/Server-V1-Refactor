package com.example.spot.story.application.service.impl;

import com.example.spot.common.api.code.status.ErrorStatus;
import com.example.spot.common.api.exception.handler.MemberHandler;
import com.example.spot.common.api.exception.handler.StudyHandler;
import com.example.spot.common.security.utils.SecurityUtils;
import com.example.spot.member.domain.Member;
import com.example.spot.member.infrastructure.MemberRepository;
import com.example.spot.notification.domain.Notification;
import com.example.spot.notification.domain.NotificationRepository;
import com.example.spot.notification.domain.enums.NotifyType;
import com.example.spot.story.application.port.in.command.EditStoryUseCase;
import com.example.spot.story.application.port.in.command.LikeStoryUseCase;
import com.example.spot.story.application.port.in.query.GetStoryUseCase;
import com.example.spot.story.application.service.StoryCommandService;
import com.example.spot.story.domain.dto.request.StoryRequestDTO;
import com.example.spot.story.domain.entity.Story;
import com.example.spot.story.domain.entity.StoryComment;
import com.example.spot.story.domain.entity.LikedStoryComment;
import com.example.spot.story.infrastructure.repository.StoryRepository;
import com.example.spot.story.infrastructure.repository.LikedStoryCommentRepository;
import com.example.spot.story.infrastructure.repository.LikedStoryRepository;
import com.example.spot.story.infrastructure.repository.StoryCommentRepository;
import com.example.spot.story.domain.dto.request.StoryCommentRequestDTO;
import com.example.spot.story.domain.dto.response.StoryCommentResponseDTO;
import com.example.spot.story.domain.dto.response.StoryResponseDTO;
import com.example.spot.study.domain.Study;
import com.example.spot.study.domain.StudyRepository;
import com.example.spot.study.domain.association.StudyMember;
import com.example.spot.study.domain.enums.StudyApplicationStatus;
import com.example.spot.study.domain.repository.StudyMemberRepository;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class StoryCommandServiceImpl implements StoryCommandService {

    @Value("${image.post.anonymous.profile}")
    private String defaultImage;

    private final GetStoryUseCase getStoryUseCase;
    private final EditStoryUseCase editStoryUseCase;
    private final LikeStoryUseCase likeStoryUseCase;

    //TODO repository를 직접 타지 않도록 개선
    private final MemberRepository memberRepository;
    private final StudyRepository studyRepository;
    private final StudyMemberRepository studyMemberRepository;
    private final NotificationRepository notificationRepository;
    private final StoryRepository storyRepository;
    private final StoryCommentRepository storyCommentRepository;
    private final LikedStoryRepository likedStoryRepository;
    private final LikedStoryCommentRepository likedStoryCommentRepository;


/* ----------------------------- 스터디 게시글 관련 API ------------------------------------- */


    @Override
    public StoryResponseDTO.StoryPreviewDTO createPost(Long studyId, StoryRequestDTO.StoryDTO postRequestDTO) {

        Long memberId = SecurityUtils.getCurrentUserId();
        SecurityUtils.verifyUserId(memberId);

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberHandler(ErrorStatus._MEMBER_NOT_FOUND));
        Study study = studyRepository.findById(studyId)
                .orElseThrow(() -> new StudyHandler(ErrorStatus._STUDY_NOT_FOUND));

        validateStudyMember(memberId, studyId);
        validateStudyOwner(memberId, studyId, postRequestDTO.getIsAnnouncement());

        Story story = editStoryUseCase.createStory(postRequestDTO, member, study);

        if (story.getIsAnnouncement()) {
            createNotification(story, member);
        }

        return StoryResponseDTO.StoryPreviewDTO.toDTO(story);
    }


    @Override
    public StoryResponseDTO.StoryPreviewDTO updatePost(Long studyId, Long storyId, StoryRequestDTO.StoryDTO storyDTO) {

        Long memberId = SecurityUtils.getCurrentUserId();
        SecurityUtils.verifyUserId(memberId);

        memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberHandler(ErrorStatus._MEMBER_NOT_FOUND));
        studyRepository.findById(studyId)
                .orElseThrow(() -> new StudyHandler(ErrorStatus._STUDY_NOT_FOUND));

        Story story = getStoryUseCase.findStory(studyId, storyId);

        validateStudyMember(memberId, studyId);
        validateStoryWriter(memberId, story);
        validateStudyOwner(memberId, studyId, storyDTO.getIsAnnouncement());

        return StoryResponseDTO.StoryPreviewDTO.toDTO(editStoryUseCase.updateStory(storyDTO, story));
    }


    @Override
    public StoryResponseDTO.StoryPreviewDTO deletePost(Long studyId, Long storyId) {

        Long memberId = SecurityUtils.getCurrentUserId();
        SecurityUtils.verifyUserId(memberId);

        memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberHandler(ErrorStatus._MEMBER_NOT_FOUND));
        Study study = studyRepository.findById(studyId)
                .orElseThrow(() -> new StudyHandler(ErrorStatus._STUDY_NOT_FOUND));

        Story story = getStoryUseCase.findStory(studyId, storyId);

        validateStudyMember(memberId, studyId);
        validateWriterOrOwner(story, memberId);

        return StoryResponseDTO.StoryPreviewDTO.toDTO(editStoryUseCase.deleteStory(study, story));
    }


    @Override
    public StoryResponseDTO.StoryLikeNumDTO likeStory(Long studyId, Long storyId) {

        Long memberId = SecurityUtils.getCurrentUserId();
        SecurityUtils.verifyUserId(memberId);

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberHandler(ErrorStatus._MEMBER_NOT_FOUND));
        studyRepository.findById(studyId)
                .orElseThrow(() -> new StudyHandler(ErrorStatus._STUDY_NOT_FOUND));

        Story story = getStoryUseCase.findStory(studyId, storyId);

        validateStudyMember(memberId, studyId);
        validateLikedStory(memberId, storyId);

        return StoryResponseDTO.StoryLikeNumDTO.toDTO(likeStoryUseCase.likeStory(story, member));
    }


    @Override
    public StoryResponseDTO.StoryLikeNumDTO cancelStoryLike(Long studyId, Long storyId) {

        Long memberId = SecurityUtils.getCurrentUserId();
        SecurityUtils.verifyUserId(memberId);

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberHandler(ErrorStatus._MEMBER_NOT_FOUND));
        studyRepository.findById(studyId)
                .orElseThrow(() -> new StudyHandler(ErrorStatus._STUDY_NOT_FOUND));

        Story story = getStoryUseCase.findStory(studyId, storyId);

        validateStudyMember(memberId, studyId);

        likeStoryUseCase.cancelStoryLike(story, member);

        return StoryResponseDTO.StoryLikeNumDTO.toDTO(story);
    }

    /* ----------------------------- 스터디 게시글 댓글 관련 API ------------------------------------- */


    @Override
    public StoryCommentResponseDTO.CommentDTO createComment(Long studyId, Long storyId,
                                                            StoryCommentRequestDTO.CommentDTO commentRequestDTO) {

        //=== Exception ===//
        Long memberId = SecurityUtils.getCurrentUserId();
        SecurityUtils.verifyUserId(memberId);

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberHandler(ErrorStatus._MEMBER_NOT_FOUND));
        studyRepository.findById(studyId)
                .orElseThrow(() -> new StudyHandler(ErrorStatus._STUDY_NOT_FOUND));

        Story story = getStoryUseCase.findStory(studyId, storyId);

        validateStudyMember(memberId, studyId);

        //=== Feature ===//
        Integer anonymousNum = getAnonymousNum(storyId, commentRequestDTO, member);

        StoryComment storyComment = StoryComment.builder()
                .story(story)
                .member(member)
                .content(commentRequestDTO.getContent())
                .likeCount(0)
                .dislikeCount(0)
                .isAnonymous(commentRequestDTO.getIsAnonymous())
                .parentComment(null)
                .isDeleted(false)
                .anonymousNum(anonymousNum)
                .build();

        storyCommentRepository.save(storyComment);

        story.setCommentNum(storyCommentRepository.findAllByStoryId(storyId).size());
        storyRepository.save(story);

        story.addComment(storyComment);

        return StoryCommentResponseDTO.CommentDTO.toDTO(storyComment, "익명" + anonymousNum, defaultImage);
    }


    @Override
    public StoryCommentResponseDTO.CommentDTO createReply(Long studyId, Long storyId, Long commentId,
                                                          StoryCommentRequestDTO.CommentDTO commentRequestDTO) {

        //=== Exception ===//
        Long memberId = SecurityUtils.getCurrentUserId();
        SecurityUtils.verifyUserId(memberId);

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberHandler(ErrorStatus._MEMBER_NOT_FOUND));
        studyRepository.findById(studyId)
                .orElseThrow(() -> new StudyHandler(ErrorStatus._STUDY_NOT_FOUND));
        Story story = getStoryUseCase.findStory(studyId, storyId);

        // 로그인한 회원이 스터디 회원인지 확인
        validateStudyMember(memberId, studyId);

        // 부모 댓글이 존재하는지 확인
        StoryComment parentComment = storyCommentRepository.findById(commentId)
                .orElseThrow(() -> new StudyHandler(ErrorStatus._STUDY_POST_COMMENT_NOT_FOUND));

        //=== Feature ===//
        Integer anonymousNum = getAnonymousNum(storyId, commentRequestDTO, member);

        StoryComment storyComment = StoryComment.builder()
                .story(story)
                .member(member)
                .content(commentRequestDTO.getContent())
                .likeCount(0)
                .dislikeCount(0)
                .isAnonymous(commentRequestDTO.getIsAnonymous())
                .anonymousNum(anonymousNum)
                .parentComment(parentComment)
                .isDeleted(false)
                .build();

        storyCommentRepository.save(storyComment);

        story.setCommentNum(storyCommentRepository.findAllByStoryId(storyId).size());
        storyRepository.save(story);

        story.addComment(storyComment);
        parentComment.addChildrenComment(storyComment);

        return StoryCommentResponseDTO.CommentDTO.toDTO(storyComment, "익명" + anonymousNum, defaultImage);
    }

    /**
     * 스터디 게시글 댓글마다 익명 번호를 부여하는 메서드입니다. 회원이 이미 타겟 스터디 게시글에 익명으로 댓글을 작성한 이력이 있는 경우 해당 번호를 반환합니다.
     *
     * @param storyId            댓글을 작성할 타겟 스터디 게시글의 아이디를 입력 받습니다.
     * @param commentRequestDTO 추가할 댓글(내용, 익명 여부)을 입력 받습니다.
     * @param member            댓글 작성자를 입력 받습니다.
     * @return 댓글 작성자의 익명 번호를 반환합니다. 회원이 이미 타겟 스터디 게시글에 익명으로 댓글을 작성한 이력이 있는 경우 해당 번호를 반환합니다.
     */
    private Integer getAnonymousNum(Long storyId, StoryCommentRequestDTO.CommentDTO commentRequestDTO, Member member) {
        Integer anonymousNum = null;

        List<StoryComment> storyComments = storyCommentRepository.findAllByStoryId(storyId);
        List<StoryComment> myStoryComments = storyCommentRepository.findAllByMemberIdAndStoryId(member.getId(), storyId);

        // 회원이 익명 댓글을 요청할 경우 anonymousNum 부여
        if (commentRequestDTO.getIsAnonymous()) {
            // anonymousNum의 (최댓값+1) 계산
            int maxAnonymousNum = 0;
            for (StoryComment storyComment : storyComments) {
                if (storyComment.getAnonymousNum() != null && storyComment.getAnonymousNum() > maxAnonymousNum) {
                    maxAnonymousNum = storyComment.getAnonymousNum();
                }
            }
            anonymousNum = maxAnonymousNum + 1;
            // 회원의 댓글 이력이 존재하는 경우 익명 작성 여부 확인
            // 해당 post에 익명으로 댓글을 남긴 이력이 있으면 해당 번호를 가져옴
            if (!myStoryComments.isEmpty()) {
                for (StoryComment myStoryComment : myStoryComments) {
                    if (myStoryComment.getIsAnonymous()) {
                        anonymousNum = myStoryComment.getAnonymousNum();
                    }
                }
                // 댓글은 있지만 익명으로 댓글을 남긴 이력이 없으면 그대로 최댓값+1 부여
            }
        }
        return anonymousNum;
    }


    @Override
    public StoryCommentResponseDTO.CommentIdDTO deleteComment(Long studyId, Long storyId, Long commentId) {

        //=== Exception ===//
        Long memberId = SecurityUtils.getCurrentUserId();
        SecurityUtils.verifyUserId(memberId);

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberHandler(ErrorStatus._MEMBER_NOT_FOUND));
        studyRepository.findById(studyId)
                .orElseThrow(() -> new StudyHandler(ErrorStatus._STUDY_NOT_FOUND));

        validateStudyMember(memberId, studyId);

        getStoryUseCase.findStory(studyId, storyId);

        // 댓글이 존재하는지 확인
        StoryComment storyComment = storyCommentRepository.findById(commentId)
                .orElseThrow(() -> new StudyHandler(ErrorStatus._STUDY_POST_COMMENT_NOT_FOUND));

        validateCommentWriter(storyComment, member);
        validateExistingComment(storyComment);

        storyComment.deleteComment();

        storyCommentRepository.save(storyComment);
        return new StoryCommentResponseDTO.CommentIdDTO(commentId);
    }


    @Override
    public StoryCommentResponseDTO.CommentPreviewDTO likeComment(Long studyId, Long storyId, Long commentId) {
        StoryComment storyComment = saveStudyPostComment(studyId, storyId, commentId, Boolean.TRUE);
        return StoryCommentResponseDTO.CommentPreviewDTO.toDTO(storyComment);
    }


    @Override
    public StoryCommentResponseDTO.CommentPreviewDTO dislikeComment(Long studyId, Long storyId, Long commentId) {
        StoryComment storyComment = saveStudyPostComment(studyId, storyId, commentId, Boolean.FALSE);
        return StoryCommentResponseDTO.CommentPreviewDTO.toDTO(storyComment);
    }

    /**
     * 댓글 좋아요/싫어요 메서드에서 사용되는 내부 메서드입니다. isLiked = true면 좋아요 정보를, isLiked = false면 싫어요 정보를 DB에 저장합니다.
     *
     * @param studyId   스터디 게시글이 작성된 타겟 스터디의 아이디를 입력 받습니다.
     * @param storyId    타겟이 되는 스터디 게시글의 아이디를 입력 받습니다.
     * @param commentId 좋아요 혹은 싫어요를 누를 타겟 댓글의 아이디를 입력 받습니다.
     * @param isLiked   좋아요 혹은 싫어요 어부를 입력 받습니다.
     * @return SavePostComment 객체를 반환합니다.
     */
    private StoryComment saveStudyPostComment(Long studyId, Long storyId, Long commentId, Boolean isLiked) {

        Long memberId = SecurityUtils.getCurrentUserId();
        SecurityUtils.verifyUserId(memberId);

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberHandler(ErrorStatus._MEMBER_NOT_FOUND));
        studyRepository.findById(studyId)
                .orElseThrow(() -> new StudyHandler(ErrorStatus._STUDY_NOT_FOUND));
        StoryComment storyComment = storyCommentRepository.findById(commentId)
                .orElseThrow(() -> new StudyHandler(ErrorStatus._STUDY_POST_COMMENT_NOT_FOUND));

        // 로그인한 회원이 스터디 회원인지 확인
        validateStudyMember(memberId, studyId);

        // 해당 스터디의 게시글인지 확인
        getStoryUseCase.findStory(studyId, storyId);

        // 이미 좋아요나 싫어요를 눌렀다면 싫어요 할 수 없음
        if (likedStoryCommentRepository.findByMemberIdAndStoryCommentIdAndIsLiked(memberId, commentId, Boolean.TRUE)
                .isPresent()) {
            throw new StudyHandler(ErrorStatus._STUDY_POST_COMMENT_ALREADY_LIKED);
        }
        if (likedStoryCommentRepository.findByMemberIdAndStoryCommentIdAndIsLiked(memberId, commentId, Boolean.FALSE)
                .isPresent()) {
            throw new StudyHandler(ErrorStatus._STUDY_POST_COMMENT_ALREADY_DISLIKED);
        }

        //=== Feature ===//
        LikedStoryComment likedStoryComment = LikedStoryComment.builder()
                .storyComment(storyComment)
                .member(member)
                .isLiked(isLiked)
                .build();

        likedStoryComment = likedStoryCommentRepository.save(likedStoryComment);
        storyComment.addLikedComment(likedStoryComment);

        if (likedStoryComment.getIsLiked()) {
            storyComment.plusLikeCount();
        } else {
            storyComment.plusDislikeCount();
        }

        storyCommentRepository.save(storyComment);
        return storyComment;
    }


    @Override
    public StoryCommentResponseDTO.CommentPreviewDTO cancelCommentLike(Long studyId, Long storyId, Long commentId) {

        Long memberId = SecurityUtils.getCurrentUserId();
        SecurityUtils.verifyUserId(memberId);

        LikedStoryComment likedStoryComment = likedStoryCommentRepository.findByMemberIdAndStoryCommentIdAndIsLiked(
                        memberId, commentId, Boolean.TRUE)
                .orElseThrow(() -> new StudyHandler(ErrorStatus._STUDY_LIKED_COMMENT_NOT_FOUND));

        StoryComment storyComment = deleteStudyLikedComment(studyId, storyId, commentId, memberId, likedStoryComment);
        return StoryCommentResponseDTO.CommentPreviewDTO.toDTO(storyComment);
    }


    @Override
    public StoryCommentResponseDTO.CommentPreviewDTO cancelCommentDislike(Long studyId, Long storyId, Long commentId) {

        Long memberId = SecurityUtils.getCurrentUserId();
        SecurityUtils.verifyUserId(memberId);

        LikedStoryComment likedStoryComment = likedStoryCommentRepository.findByMemberIdAndStoryCommentIdAndIsLiked(
                        memberId, commentId, Boolean.FALSE)
                .orElseThrow(() -> new StudyHandler(ErrorStatus._STUDY_DISLIKED_COMMENT_NOT_FOUND));

        StoryComment storyComment = deleteStudyLikedComment(studyId, storyId, commentId, memberId, likedStoryComment);
        return StoryCommentResponseDTO.CommentPreviewDTO.toDTO(storyComment);
    }

    /**
     * 댓글 좋아요/싫어요 취소 메서드에서 사용되는 내부 메서드입니다.
     *
     * @param studyId           스터디 게시글이 작성된 타겟 스터디의 아이디를 입력 받습니다.
     * @param storyId            타겟이 되는 스터디 게시글의 아이디를 입력 받습니다.
     * @param commentId         좋아요 혹은 싫어요를 취소할 타겟 댓글의 아이디를 입력 받습니다.
     * @param memberId          댓글에 좋아요 혹은 싫어요를 누른 회원의 아이디를 입력 받습니다.
     * @param likedStoryComment DB에서 삭제할 StudyLikedComment 객체를 입력 받습니다.
     * @return 삭제된 StudyLikedComment 객체를 반환합니다.
     */
    private StoryComment deleteStudyLikedComment(Long studyId, Long storyId, Long commentId, Long memberId,
                                                 LikedStoryComment likedStoryComment) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberHandler(ErrorStatus._MEMBER_NOT_FOUND));
        studyRepository.findById(studyId)
                .orElseThrow(() -> new StudyHandler(ErrorStatus._STUDY_NOT_FOUND));

        getStoryUseCase.findStory(studyId, storyId);

        StoryComment storyComment = storyCommentRepository.findById(commentId)
                .orElseThrow(() -> new StudyHandler(ErrorStatus._STUDY_POST_COMMENT_NOT_FOUND));

        validateStudyMember(memberId, studyId);

        // 로그인한 회원이 댓글에 반응한 사람인지 확인
        if (!likedStoryComment.getMember().equals(member)) {
            throw new StudyHandler(ErrorStatus._STUDY_POST_COMMENT_DELETE_INVALID);
        }

        //=== Feature ===//
        storyComment.deleteLikedComment(likedStoryComment);

        if (likedStoryComment.getIsLiked()) {
            storyComment.minusLikeCount();
        } else {
            storyComment.minusDislikeCount();
        }

        likedStoryCommentRepository.delete(likedStoryComment);
        storyCommentRepository.save(storyComment);
        return storyComment;
    }

    private void createNotification(Story story, Member member) {

        // 스터디에 참여중인 회원들에게 알림 전송 위해 회원 조회
        List<Member> members = studyMemberRepository.findAllByStudyIdAndStatus(
                        story.getStudy().getId(), StudyApplicationStatus.APPROVED).stream()
                .map(StudyMember::getMember)
                .toList();

        if (members.isEmpty()) {
            throw new StudyHandler(ErrorStatus._STUDY_MEMBER_NOT_FOUND);
        }

        // 알림 생성
        for (Member studyMember : members) {
            Notification notification = Notification.builder()
                    .study(story.getStudy())
                    .member(studyMember)
                    .studyPostId(story.getId())
                    .notifierName(member.getName()) // 글을 작성한 회원 이름
                    .type(NotifyType.ANNOUNCEMENT)
                    .isChecked(false)
                    .build();
            notificationRepository.save(notification);
        }
    }

    private void validateStudyOwner(Long memberId, Long studyId, Boolean isAnnouncement) {
        StudyMember memberStudy = studyMemberRepository
                .findByMemberIdAndStudyIdAndStatus(memberId, studyId, StudyApplicationStatus.APPROVED)
                .orElseThrow(() -> new StudyHandler(ErrorStatus._STUDY_MEMBER_NOT_FOUND));
        if (!memberStudy.getIsOwned() && isAnnouncement) {
            throw new StudyHandler(ErrorStatus._STUDY_POST_ANNOUNCEMENT_INVALID);
        }
    }

    private void validateStudyMember(Long memberId, Long studyId) {
        studyMemberRepository.findByMemberIdAndStudyIdAndStatus(memberId, studyId,
                        StudyApplicationStatus.APPROVED)
                .orElseThrow(() -> new StudyHandler(ErrorStatus._STUDY_MEMBER_NOT_FOUND));

        // 로그인한 회원이 스터디 회원인지 확인
        studyMemberRepository.findByMemberIdAndStudyIdAndStatus(memberId, studyId,
                        StudyApplicationStatus.APPROVED)
                .orElseThrow(() -> new StudyHandler(ErrorStatus._STUDY_MEMBER_NOT_FOUND));
    }

    private void validateStoryWriter(Long memberId, Story story) {
        if (!story.getMember().getId().equals(memberId)) {
            throw new StudyHandler(ErrorStatus._STUDY_POST_UPDATE_INVALID);
        }
    }

    private void validateLikedStory(Long memberId, Long storyId) {
        if (likedStoryRepository.findByMemberIdAndStoryId(memberId, storyId).isPresent()) {
            throw new StudyHandler(ErrorStatus._STUDY_POST_ALREADY_LIKED);
        }
    }

    private void validateWriterOrOwner(Story story, Long memberId) {
        if (!isWriterOrOwner(story, memberId)) {
            throw new StudyHandler(ErrorStatus._STUDY_POST_DELETION_INVALID);
        }
    }

    private Boolean isWriterOrOwner(Story story, Long memberId) {
        Long ownerId = story.getStudy().getStudyMembers().stream()
                .filter(StudyMember::getIsOwned)
                .map(memberStudy -> memberStudy.getMember().getId())
                .findFirst()
                .orElseThrow(() -> new StudyHandler(ErrorStatus._STUDY_OWNER_NOT_FOUND));
        return story.getMember().getId().equals(memberId) || memberId.equals(ownerId);
    }

    private void validateExistingComment(StoryComment storyComment) {
        if (storyComment.getIsDeleted()) {
            throw new StudyHandler(ErrorStatus._STUDY_POST_COMMENT_ALREADY_DELETED);
        }
    }

    private void validateCommentWriter(StoryComment storyComment, Member member) {
        if (!storyComment.getMember().equals(member)) {
            throw new StudyHandler(ErrorStatus._STUDY_POST_COMMENT_DELETE_INVALID);
        }
    }

}
