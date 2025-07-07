package com.example.spot.story.application;

import com.example.spot.common.api.code.status.ErrorStatus;
import com.example.spot.common.api.exception.handler.MemberHandler;
import com.example.spot.common.api.exception.handler.StudyHandler;
import com.example.spot.common.application.s3.S3ImageService;
import com.example.spot.common.security.utils.SecurityUtils;
import com.example.spot.member.domain.Member;
import com.example.spot.member.domain.MemberRepository;
import com.example.spot.notification.domain.Notification;
import com.example.spot.notification.domain.NotificationRepository;
import com.example.spot.notification.domain.enums.NotifyType;
import com.example.spot.report.domain.StoryReportRepository;
import com.example.spot.story.domain.Story;
import com.example.spot.story.domain.StoryRepository;
import com.example.spot.story.domain.association.LikedStory;
import com.example.spot.story.domain.association.LikedStoryComment;
import com.example.spot.story.domain.association.StoryComment;
import com.example.spot.story.domain.association.StoryImage;
import com.example.spot.story.domain.repository.LikedStoryCommentRepository;
import com.example.spot.story.domain.repository.LikedStoryRepository;
import com.example.spot.story.domain.repository.StoryCommentRepository;
import com.example.spot.story.domain.repository.StoryImageRepository;
import com.example.spot.story.web.dto.request.StoryCommentRequestDTO;
import com.example.spot.story.web.dto.request.StoryRequestDTO;
import com.example.spot.story.web.dto.response.StoryCommentResponseDTO;
import com.example.spot.story.web.dto.response.StoryResponseDTO;
import com.example.spot.study.domain.Study;
import com.example.spot.study.domain.StudyRepository;
import com.example.spot.study.domain.association.StudyMember;
import com.example.spot.study.domain.enums.StudyApplicationStatus;
import com.example.spot.study.domain.repository.StudyMemberRepository;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@Transactional
@RequiredArgsConstructor
public class StoryCommandServiceImpl implements StoryCommandService {

    @Value("${image.post.anonymous.profile}")
    private String defaultImage;

    private final MemberRepository memberRepository;
    private final StudyRepository studyRepository;

    private final StudyMemberRepository studyMemberRepository;
    private final StoryRepository storyRepository;
    private final StoryImageRepository storyImageRepository;
    private final StoryCommentRepository storyCommentRepository;
    private final LikedStoryRepository likedStoryRepository;
    private final LikedStoryCommentRepository likedStoryCommentRepository;
    private final StoryReportRepository storyReportRepository;
    private final NotificationRepository notificationRepository;

    // S3 Service
    private final S3ImageService s3ImageService;

    /* ----------------------------- 스터디 게시글 관련 API ------------------------------------- */

    /**
     * 스터디 내부 게시판에 게시글을 작성하는 메서드입니다.
     *
     * @param studyId        게시글을 작성할 타겟 스터디의 아이디를 입력 받습니다.
     * @param postRequestDTO 게시글의 입력 형식(StudyPostRequestDTO.PostDTO)에 맞추어 게시글 정보를 입력 받습니다.
     * @return 작성된 스터디 게시글의 Preview(게시글 아이디, 제목)를 반환합니다.
     */
    @Override
    public StoryResponseDTO.StoryPreviewDTO createPost(Long studyId, StoryRequestDTO.StoryDTO postRequestDTO) {

        //=== Exception ===//
        Long memberId = SecurityUtils.getCurrentUserId();
        SecurityUtils.verifyUserId(memberId);

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberHandler(ErrorStatus._MEMBER_NOT_FOUND));
        Study study = studyRepository.findById(studyId)
                .orElseThrow(() -> new StudyHandler(ErrorStatus._STUDY_NOT_FOUND));
        studyMemberRepository.findByMemberIdAndStudyIdAndStatus(member.getId(), studyId,
                        StudyApplicationStatus.APPROVED)
                .orElseThrow(() -> new StudyHandler(ErrorStatus._STUDY_MEMBER_NOT_FOUND));

        // 로그인한 회원이 스터디 회원인지 확인
        studyMemberRepository.findByMemberIdAndStudyIdAndStatus(member.getId(), studyId,
                        StudyApplicationStatus.APPROVED)
                .orElseThrow(() -> new StudyHandler(ErrorStatus._STUDY_MEMBER_NOT_FOUND));

        // 스터디장만 공지 가능
        StudyMember memberStudy = studyMemberRepository.findByMemberIdAndStudyIdAndStatus(member.getId(), studyId,
                        StudyApplicationStatus.APPROVED)
                .orElseThrow(() -> new StudyHandler(ErrorStatus._STUDY_MEMBER_NOT_FOUND));
        if (!memberStudy.getIsOwned() && postRequestDTO.getIsAnnouncement()) {
            throw new StudyHandler(ErrorStatus._STUDY_POST_ANNOUNCEMENT_INVALID);
        }

        //=== Feature ===//
        Story story = Story.builder()
                .isAnnouncement(postRequestDTO.getIsAnnouncement())
                .storyCategory(postRequestDTO.getStoryCategory())
                .title(postRequestDTO.getTitle())
                .content(postRequestDTO.getContent())
                .likeNum(0)
                .hitNum(0)
                .commentNum(0)
                .member(member)
                .study(study)
                .build();

        // 공지면 announcedAt 설정
        if (story.getIsAnnouncement()) {
            story.setAnnouncedAt(LocalDateTime.now());
        }

        story = storyRepository.save(story);
        member.addStudyPost(story);
        study.addStudyPost(story);

        // 이미지가 있는 경우 이미지 저장
        if (postRequestDTO.getImage() != null) {
            String imageUrl = s3ImageService.upload(postRequestDTO.getImage());
            StoryImage storyImage = StoryImage.builder()
                    .url(imageUrl)
                    .story(story)
                    .build();
            storyImage = storyImageRepository.save(storyImage);
            story.addImage(storyImage);
        }

        if (story.getIsAnnouncement()) {

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

        member.updateStudyPost(story);
        study.updateStudyPost(story);

        return StoryResponseDTO.StoryPreviewDTO.toDTO(story);
    }

    @Override
    public StoryResponseDTO.StoryPreviewDTO updatePost(Long studyId, Long postId, StoryRequestDTO.StoryDTO storyDTO) {

        Long memberId = SecurityUtils.getCurrentUserId();
        SecurityUtils.verifyUserId(memberId);

        memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberHandler(ErrorStatus._MEMBER_NOT_FOUND));
        studyRepository.findById(studyId)
                .orElseThrow(() -> new StudyHandler(ErrorStatus._STUDY_NOT_FOUND));
        Story story = storyRepository.findById(postId)
                .orElseThrow(() -> new StudyHandler(ErrorStatus._STUDY_POST_NOT_FOUND));

        // 로그인한 회원이 스터디 회원인지 확인
        StudyMember studyMember = studyMemberRepository.findByMemberIdAndStudyIdAndStatus(memberId, studyId,
                        StudyApplicationStatus.APPROVED)
                .orElseThrow(() -> new StudyHandler(ErrorStatus._STUDY_MEMBER_NOT_FOUND));

        // 로그인한 회원이 게시글 작성자인지 확인
        storyRepository.findByIdAndMemberId(postId, memberId)
                .orElseThrow(() -> new StudyHandler(ErrorStatus._STUDY_POST_UPDATE_INVALID));

        // 스터디장만 공지 가능
        if (!studyMember.getIsOwned() && storyDTO.getIsAnnouncement()) {
            throw new StudyHandler(ErrorStatus._STUDY_POST_ANNOUNCEMENT_INVALID);
        }

        // 스터디 게시글 이미지 업데이트
        updateStudyPostImage(storyDTO, story);

        // 스터디 게시글 업데이트
        story.updatePost(storyDTO);

        return StoryResponseDTO.StoryPreviewDTO.toDTO(story);
    }

    private void updateStudyPostImage(StoryRequestDTO.StoryDTO storyDTO, Story story) {
        List<StoryImage> storyImages = story.getImages();
        // 기존 이미지가 존재하는 경우 이미지 유지
        if (!StringUtils.hasText(storyDTO.getExistingImage())) {
            // 기존 이미지가 없고 새로운 이미지를 등록한 경우 이미지 url 변경
            if (storyDTO.getImage() != null) {
                String imageUrl = s3ImageService.upload(storyDTO.getImage());
                storyImages.forEach(studyPostImage -> {
                    studyPostImage.setUrl(imageUrl);
                    story.updateImage(studyPostImage);
                });
            }
        }
    }

    /**
     * 스터디 내부 게시판에 작성된 게시글을 삭제합니다.
     *
     * @param studyId 게시글을 삭제할 타겟 스터디의 아이디를 입력 받습니다.
     * @param postId  삭제할 스터디 게시글의 아이디를 입력 받습니다.
     * @return 삭제된 스터디 게시글의 Preview(게시글 아이디, 제목)를 반환합니다.
     */
    @Override
    public StoryResponseDTO.StoryPreviewDTO deletePost(Long studyId, Long postId) {

        //=== Exception ===//
        Long memberId = SecurityUtils.getCurrentUserId();
        SecurityUtils.verifyUserId(memberId);

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberHandler(ErrorStatus._MEMBER_NOT_FOUND));
        Study study = studyRepository.findById(studyId)
                .orElseThrow(() -> new StudyHandler(ErrorStatus._STUDY_NOT_FOUND));
        Story story = storyRepository.findById(postId)
                .orElseThrow(() -> new StudyHandler(ErrorStatus._STUDY_POST_NOT_FOUND));

        // 로그인한 회원이 스터디 회원인지 확인
        studyMemberRepository.findByMemberIdAndStudyIdAndStatus(member.getId(), studyId,
                        StudyApplicationStatus.APPROVED)
                .orElseThrow(() -> new StudyHandler(ErrorStatus._STUDY_MEMBER_NOT_FOUND));

        // 해당 스터디의 게시글인지 확인
        storyRepository.findByIdAndStudyId(postId, studyId)
                .orElseThrow(() -> new StudyHandler(ErrorStatus._STUDY_POST_NOT_FOUND));

        // 로그인 회원이 게시글 작성자거나 owner인지 확인
        Long ownerId = story.getStudy().getMemberStudies().stream()
                .filter(StudyMember::getIsOwned)
                .map(memberStudy -> memberStudy.getMember().getId())
                .findFirst()
                .orElseThrow(() -> new StudyHandler(ErrorStatus._STUDY_OWNER_NOT_FOUND));

        if (story.getMember().getId().equals(memberId) ||
                memberId.equals(ownerId)) {

            storyImageRepository.deleteAllByStoryId(postId);
            storyCommentRepository.deleteAllByStoryId(postId);
            likedStoryRepository.deleteAllByStoryId(postId);
            storyReportRepository.deleteAllByStoryId(postId);

            member.deleteStudyPost(story);
            study.deleteStudyPost(story);
            storyRepository.delete(story);

        } else {
            throw new StudyHandler(ErrorStatus._STUDY_POST_DELETION_INVALID);
        }

        return StoryResponseDTO.StoryPreviewDTO.toDTO(story);
    }

    /**
     * 스터디 내부 게시판에 작성된 게시글에 좋아요를 누르는 메서드입니다. 게시글에 좋아요를 누른 회원의 정보가 StudyLikedPost에 저장되고 스터디 게시글의 좋아요 개수가 업데이트 됩니다.
     *
     * @param studyId 게시글이 작성된 타겟 스터디의 아이디를 입력 받습니다.
     * @param postId  좋아요를 누를 타겟 게시글의 아이디를 입력 받습니다.
     * @return 게시글의 Preview(게시글 아이디, 제목)와 함께 좋아요 개수가 반환됩니다.
     */
    @Override
    public StoryResponseDTO.StoryLikeNumDTO likePost(Long studyId, Long postId) {

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
        studyMemberRepository.findByMemberIdAndStudyIdAndStatus(member.getId(), studyId,
                        StudyApplicationStatus.APPROVED)
                .orElseThrow(() -> new StudyHandler(ErrorStatus._STUDY_MEMBER_NOT_FOUND));

        // 해당 스터디의 게시글인지 확인
        storyRepository.findByIdAndStudyId(postId, studyId)
                .orElseThrow(() -> new StudyHandler(ErrorStatus._STUDY_POST_NOT_FOUND));

        // 이미 좋아요를 눌렀다면 다시 좋아요 할 수 없음
        if (likedStoryRepository.findByMemberIdAndStoryId(memberId, postId).isPresent()) {
            throw new StudyHandler(ErrorStatus._STUDY_POST_ALREADY_LIKED);
        }

        //=== Feature ===//
        LikedStory likedStory = LikedStory.builder()
                .member(member)
                .story(story)
                .build();

        likedStory = likedStoryRepository.save(likedStory);
        member.addStudyLikedPost(likedStory);
        story.addLikedPost(likedStory);

        story.plusLikeNum();
        story = storyRepository.save(story);

        return StoryResponseDTO.StoryLikeNumDTO.toDTO(story);
    }

    /**
     * 스터디 내부 게시판에 작성된 게시글에 누른 좋아요를 취소하는 메서드입니다. 게시글에 좋아요를 누른 회원의 정보가 StudyLikedPost에서 삭제되고 스터디 게시글의 좋아요 개수가 업데이트 됩니다.
     *
     * @param studyId 게시글이 작성된 타겟 스터디의 아이디를 입력 받습니다.
     * @param postId  좋아요를 취소할 타겟 게시글의 아이디를 입력 받습니다.
     * @return 게시글의 Preview(게시글 아이디, 제목)와 함께 좋아요 개수가 반환됩니다.
     */
    @Override
    public StoryResponseDTO.StoryLikeNumDTO cancelPostLike(Long studyId, Long postId) {

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
        studyMemberRepository.findByMemberIdAndStudyIdAndStatus(member.getId(), studyId,
                        StudyApplicationStatus.APPROVED)
                .orElseThrow(() -> new StudyHandler(ErrorStatus._STUDY_MEMBER_NOT_FOUND));

        // 해당 스터디의 게시글인지 확인
        storyRepository.findByIdAndStudyId(postId, studyId)
                .orElseThrow(() -> new StudyHandler(ErrorStatus._STUDY_POST_NOT_FOUND));

        //=== Feature ===//
        LikedStory likedStory = likedStoryRepository.findByMemberIdAndStoryId(memberId, postId)
                .orElseThrow(() -> new StudyHandler(ErrorStatus._STUDY_LIKED_POST_NOT_FOUND));

        member.deleteStudyLikedPost(likedStory);
        story.deleteLikedPost(likedStory);
        story.minusLikeNum();
        likedStoryRepository.delete(likedStory);
        storyRepository.save(story);

        return StoryResponseDTO.StoryLikeNumDTO.toDTO(story);
    }

    /* ----------------------------- 스터디 게시글 댓글 관련 API ------------------------------------- */

    /**
     * 스터디 게시글에 댓글을 추가하는 메서드입니다. 답글 추가 메서드는 하단에 별도로 구현되어 있습니다.
     *
     * @param studyId           스터디 게시글이 작성된 타겟 스터디를 입력 받습니다.
     * @param postId            댓글을 추가할 스터디 게시글의 아이디를 입력 받습니다.
     * @param commentRequestDTO 추가할 댓글(내용, 익명 여부)을 입력 받습니다.
     * @return 댓글 아이디와 작성자, 내용, 좋아요와 싫어요 개수를 함께 반환합니다.
     */
    @Override
    public StoryCommentResponseDTO.CommentDTO createComment(Long studyId, Long postId,
                                                            StoryCommentRequestDTO.CommentDTO commentRequestDTO) {

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
        studyMemberRepository.findByMemberIdAndStudyIdAndStatus(member.getId(), studyId,
                        StudyApplicationStatus.APPROVED)
                .orElseThrow(() -> new StudyHandler(ErrorStatus._STUDY_MEMBER_NOT_FOUND));

        // 해당 스터디의 게시글인지 확인
        storyRepository.findByIdAndStudyId(postId, studyId)
                .orElseThrow(() -> new StudyHandler(ErrorStatus._STUDY_POST_NOT_FOUND));

        //=== Feature ===//
        Integer anonymousNum = getAnonymousNum(postId, commentRequestDTO, member);

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

        story.setCommentNum(storyCommentRepository.findAllByStoryId(postId).size());
        storyRepository.save(story);

        story.addComment(storyComment);
        member.addComment(storyComment);

        return StoryCommentResponseDTO.CommentDTO.toDTO(storyComment, "익명" + anonymousNum, defaultImage);
    }

    /**
     * 스터디 게시글에 답글을 추가하는 메서드입니다. 댓글 추가 메서드는 상단에 별도로 구현되어 있습니다.
     *
     * @param studyId           스터디 게시글이 작성된 타겟 스터디를 입력 받습니다.
     * @param postId            댓글을 추가할 스터디 게시글의 아이디를 입력 받습니다.
     * @param commentRequestDTO 추가할 댓글(내용, 익명 여부)을 입력 받습니다.
     * @return 댓글 아이디와 작성자, 내용, 좋아요와 싫어요 개수를 함께 반환합니다.
     */
    @Override
    public StoryCommentResponseDTO.CommentDTO createReply(Long studyId, Long postId, Long commentId,
                                                          StoryCommentRequestDTO.CommentDTO commentRequestDTO) {

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
        studyMemberRepository.findByMemberIdAndStudyIdAndStatus(member.getId(), studyId,
                        StudyApplicationStatus.APPROVED)
                .orElseThrow(() -> new StudyHandler(ErrorStatus._STUDY_MEMBER_NOT_FOUND));

        // 해당 스터디의 게시글인지 확인
        storyRepository.findByIdAndStudyId(postId, studyId)
                .orElseThrow(() -> new StudyHandler(ErrorStatus._STUDY_POST_NOT_FOUND));

        // 부모 댓글이 존재하는지 확인
        StoryComment parentComment = storyCommentRepository.findById(commentId)
                .orElseThrow(() -> new StudyHandler(ErrorStatus._STUDY_POST_COMMENT_NOT_FOUND));

        //=== Feature ===//
        Integer anonymousNum = getAnonymousNum(postId, commentRequestDTO, member);

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

        story.setCommentNum(storyCommentRepository.findAllByStoryId(postId).size());
        storyRepository.save(story);

        story.addComment(storyComment);
        member.addComment(storyComment);
        parentComment.addChildrenComment(storyComment);

        return StoryCommentResponseDTO.CommentDTO.toDTO(storyComment, "익명" + anonymousNum, defaultImage);
    }

    /**
     * 스터디 게시글 댓글마다 익명 번호를 부여하는 메서드입니다. 회원이 이미 타겟 스터디 게시글에 익명으로 댓글을 작성한 이력이 있는 경우 해당 번호를 반환합니다.
     *
     * @param postId            댓글을 작성할 타겟 스터디 게시글의 아이디를 입력 받습니다.
     * @param commentRequestDTO 추가할 댓글(내용, 익명 여부)을 입력 받습니다.
     * @param member            댓글 작성자를 입력 받습니다.
     * @return 댓글 작성자의 익명 번호를 반환합니다. 회원이 이미 타겟 스터디 게시글에 익명으로 댓글을 작성한 이력이 있는 경우 해당 번호를 반환합니다.
     */
    private Integer getAnonymousNum(Long postId, StoryCommentRequestDTO.CommentDTO commentRequestDTO, Member member) {
        Integer anonymousNum = null;

        List<StoryComment> storyComments = storyCommentRepository.findAllByStoryId(postId);
        List<StoryComment> myStoryComments = storyCommentRepository.findAllByMemberIdAndStoryId(member.getId(), postId);

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

    /**
     * 스터디 게시글에 작성한 댓글을 삭제하는 메서드입니다. 댓글 삭제와 답글 삭제 모두 해당 메서드를 활용합니다.
     *
     * @param studyId   스터디 게시글이 작성된 타겟 스터디의 아이디를 입력 받습니다.
     * @param postId    댓글을 삭제할 타겟 스터디 게시글의 아이디를 입력 받습니다.
     * @param commentId 삭제할 댓글의 아이디를 입력 받습니다.
     * @return 삭제한 댓글의 아이디를 반환합니다.
     */
    @Override
    public StoryCommentResponseDTO.CommentIdDTO deleteComment(Long studyId, Long postId, Long commentId) {

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
        StoryComment storyComment = storyCommentRepository.findById(commentId)
                .orElseThrow(() -> new StudyHandler(ErrorStatus._STUDY_POST_COMMENT_NOT_FOUND));

        // 댓글 작성자인지 확인
        if (!storyComment.getMember().equals(member)) {
            throw new StudyHandler(ErrorStatus._STUDY_POST_COMMENT_DELETE_INVALID);
        }

        //=== Feature ===//

        if (storyComment.getIsDeleted()) {
            throw new StudyHandler(ErrorStatus._STUDY_POST_COMMENT_ALREADY_DELETED);
        }
        storyComment.deleteComment();
        story.updateComment(storyComment);
        member.updateComment(storyComment);

        storyCommentRepository.save(storyComment);
        return new StoryCommentResponseDTO.CommentIdDTO(commentId);
    }

    /**
     * 댓글에 좋아요를 누르는 메서드입니다. 댓글 좋아요와 답글 좋아요 모두 해당 메서드를 활용합니다. 댓글에 좋아요를 누른 회원의 정보가 StudyLikedComment에 저장되고 타겟 댓글의 좋아요 개수가
     * 업데이트 됩니다.
     *
     * @param studyId   스터디 게시글이 작성된 타겟 스터디의 아이디를 입력 받습니다.
     * @param postId    타겟이 되는 스터디 게시글의 아이디를 입력 받습니다.
     * @param commentId 좋아요를 누를 타겟 댓글의 아이디를 입력 받습니다.
     * @return 댓글 아이디와 타겟 댓글의 좋아요 수와 싫어요 수가 반환됩니다.
     */
    @Override
    public StoryCommentResponseDTO.CommentPreviewDTO likeComment(Long studyId, Long postId, Long commentId) {
        StoryComment storyComment = saveStudyPostComment(studyId, postId, commentId, Boolean.TRUE);
        return StoryCommentResponseDTO.CommentPreviewDTO.toDTO(storyComment);
    }

    /**
     * 댓글에 싫어요를 누르는 메서드입니다. 댓글 싫어요와 답글 싫어요 모두 해당 메서드를 활용합니다. 댓글에 싫어요를 누른 회원의 정보가 StudyLikedComment에 저장되고 타겟 댓글의 싫어요 개수가
     * 업데이트 됩니다.
     *
     * @param studyId   스터디 게시글이 작성된 타겟 스터디의 아이디를 입력 받습니다.
     * @param postId    타겟이 되는 스터디 게시글의 아이디를 입력 받습니다.
     * @param commentId 싫어요를 누를 타겟 댓글의 아이디를 입력 받습니다.
     * @return 댓글 아이디와 타겟 댓글의 좋아요 수와 싫어요 수가 반환됩니다.
     */
    @Override
    public StoryCommentResponseDTO.CommentPreviewDTO dislikeComment(Long studyId, Long postId, Long commentId) {
        StoryComment storyComment = saveStudyPostComment(studyId, postId, commentId, Boolean.FALSE);
        return StoryCommentResponseDTO.CommentPreviewDTO.toDTO(storyComment);
    }

    /**
     * 댓글 좋아요/싫어요 메서드에서 사용되는 내부 메서드입니다. isLiked = true면 좋아요 정보를, isLiked = false면 싫어요 정보를 DB에 저장합니다.
     *
     * @param studyId   스터디 게시글이 작성된 타겟 스터디의 아이디를 입력 받습니다.
     * @param postId    타겟이 되는 스터디 게시글의 아이디를 입력 받습니다.
     * @param commentId 좋아요 혹은 싫어요를 누를 타겟 댓글의 아이디를 입력 받습니다.
     * @param isLiked   좋아요 혹은 싫어요 어부를 입력 받습니다.
     * @return SavePostComment 객체를 반환합니다.
     */
    private StoryComment saveStudyPostComment(Long studyId, Long postId, Long commentId, Boolean isLiked) {

        //=== Exception ===//

        Long memberId = SecurityUtils.getCurrentUserId();
        SecurityUtils.verifyUserId(memberId);

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberHandler(ErrorStatus._MEMBER_NOT_FOUND));
        studyRepository.findById(studyId)
                .orElseThrow(() -> new StudyHandler(ErrorStatus._STUDY_NOT_FOUND));
        StoryComment storyComment = storyCommentRepository.findById(commentId)
                .orElseThrow(() -> new StudyHandler(ErrorStatus._STUDY_POST_COMMENT_NOT_FOUND));

        // 로그인한 회원이 스터디 회원인지 확인
        studyMemberRepository.findByMemberIdAndStudyIdAndStatus(member.getId(), studyId,
                        StudyApplicationStatus.APPROVED)
                .orElseThrow(() -> new StudyHandler(ErrorStatus._STUDY_MEMBER_NOT_FOUND));

        // 해당 스터디의 게시글인지 확인
        storyRepository.findById(postId)
                .orElseThrow(() -> new StudyHandler(ErrorStatus._STUDY_POST_NOT_FOUND));

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
        member.addStudyLikedComment(likedStoryComment);
        storyComment.addLikedComment(likedStoryComment);

        if (likedStoryComment.getIsLiked()) {
            storyComment.plusLikeCount();
        } else {
            storyComment.plusDislikeCount();
        }

        storyCommentRepository.save(storyComment);
        return storyComment;
    }

    /**
     * 댓글 좋아요를 취소하는 메서드입니다. 댓글 좋아요와 답글 좋아요 모두 해당 메서드를 활용합니다. 댓글 좋아요를 취소한 회원의 정보가 StudyLikedComment에서 삭제되고 타겟 댓글의 싫어요 개수가
     * 업데이트 됩니다.
     *
     * @param studyId   스터디 게시글이 작성된 타겟 스터디의 아이디를 입력 받습니다.
     * @param postId    타겟이 되는 스터디 게시글의 아이디를 입력 받습니다.
     * @param commentId 싫어요를 취소할 타겟 댓글의 아이디를 입력 받습니다.
     * @return 댓글 아이디와 타겟 댓글의 좋아요 수와 싫어요 수가 반환됩니다.
     */
    @Override
    public StoryCommentResponseDTO.CommentPreviewDTO cancelCommentLike(Long studyId, Long postId, Long commentId) {

        Long memberId = SecurityUtils.getCurrentUserId();
        SecurityUtils.verifyUserId(memberId);

        LikedStoryComment likedStoryComment = likedStoryCommentRepository.findByMemberIdAndStoryCommentIdAndIsLiked(
                        memberId, commentId, Boolean.TRUE)
                .orElseThrow(() -> new StudyHandler(ErrorStatus._STUDY_LIKED_COMMENT_NOT_FOUND));

        StoryComment storyComment = deleteStudyLikedComment(studyId, postId, commentId, memberId, likedStoryComment);
        return StoryCommentResponseDTO.CommentPreviewDTO.toDTO(storyComment);
    }

    /**
     * 댓글 싫어요를 취소하는 메서드입니다. 댓글 싫어요와 답글 싫어요 모두 해당 메서드를 활용합니다. 댓글 싫어요를 취소한 회원의 정보가 StudyLikedComment에서 삭제되고 타겟 댓글의 싫어요 개수가
     * 업데이트 됩니다.
     *
     * @param studyId   스터디 게시글이 작성된 타겟 스터디의 아이디를 입력 받습니다.
     * @param postId    타겟이 되는 스터디 게시글의 아이디를 입력 받습니다.
     * @param commentId 싫어요를 취소할 타겟 댓글의 아이디를 입력 받습니다.
     * @return 댓글 아이디와 타겟 댓글의 좋아요 수와 싫어요 수가 반환됩니다.
     */
    @Override
    public StoryCommentResponseDTO.CommentPreviewDTO cancelCommentDislike(Long studyId, Long postId, Long commentId) {

        Long memberId = SecurityUtils.getCurrentUserId();
        SecurityUtils.verifyUserId(memberId);

        LikedStoryComment likedStoryComment = likedStoryCommentRepository.findByMemberIdAndStoryCommentIdAndIsLiked(
                        memberId, commentId, Boolean.FALSE)
                .orElseThrow(() -> new StudyHandler(ErrorStatus._STUDY_DISLIKED_COMMENT_NOT_FOUND));

        StoryComment storyComment = deleteStudyLikedComment(studyId, postId, commentId, memberId, likedStoryComment);
        return StoryCommentResponseDTO.CommentPreviewDTO.toDTO(storyComment);
    }

    /**
     * 댓글 좋아요/싫어요 취소 메서드에서 사용되는 내부 메서드입니다.
     *
     * @param studyId           스터디 게시글이 작성된 타겟 스터디의 아이디를 입력 받습니다.
     * @param postId            타겟이 되는 스터디 게시글의 아이디를 입력 받습니다.
     * @param commentId         좋아요 혹은 싫어요를 취소할 타겟 댓글의 아이디를 입력 받습니다.
     * @param memberId          댓글에 좋아요 혹은 싫어요를 누른 회원의 아이디를 입력 받습니다.
     * @param likedStoryComment DB에서 삭제할 StudyLikedComment 객체를 입력 받습니다.
     * @return 삭제된 StudyLikedComment 객체를 반환합니다.
     */
    private StoryComment deleteStudyLikedComment(Long studyId, Long postId, Long commentId, Long memberId,
                                                 LikedStoryComment likedStoryComment) {

        //=== Exception ===//
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberHandler(ErrorStatus._MEMBER_NOT_FOUND));
        studyRepository.findById(studyId)
                .orElseThrow(() -> new StudyHandler(ErrorStatus._STUDY_NOT_FOUND));
        storyRepository.findById(postId)
                .orElseThrow(() -> new StudyHandler(ErrorStatus._STUDY_POST_NOT_FOUND));
        StoryComment storyComment = storyCommentRepository.findById(commentId)
                .orElseThrow(() -> new StudyHandler(ErrorStatus._STUDY_POST_COMMENT_NOT_FOUND));

        // 로그인한 회원이 스터디 회원인지 확인
        studyMemberRepository.findByMemberIdAndStudyIdAndStatus(member.getId(), studyId,
                        StudyApplicationStatus.APPROVED)
                .orElseThrow(() -> new StudyHandler(ErrorStatus._STUDY_MEMBER_NOT_FOUND));

        // 로그인한 회원이 댓글에 반응한 사람인지 확인
        if (!likedStoryComment.getMember().equals(member)) {
            throw new StudyHandler(ErrorStatus._STUDY_POST_COMMENT_DELETE_INVALID);
        }

        //=== Feature ===//
        member.deleteStudyLikedComment(likedStoryComment);
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

}
