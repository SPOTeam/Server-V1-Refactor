package com.example.spot.service.story;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.spot.common.api.exception.handler.StudyHandler;
import com.example.spot.common.application.s3.S3ImageService;
import com.example.spot.member.domain.Member;
import com.example.spot.member.domain.enums.Gender;
import com.example.spot.member.infrastructure.jpa.MemberRepository;
import com.example.spot.notification.domain.Notification;
import com.example.spot.notification.infrastructure.jpa.NotificationRepository;
import com.example.spot.report.infrastructure.jpa.StoryReportRepository;
import com.example.spot.story.application.StoryCommandServiceImpl;
import com.example.spot.story.domain.Story;
import com.example.spot.story.domain.StoryRepository;
import com.example.spot.story.domain.association.LikedStory;
import com.example.spot.story.domain.association.LikedStoryComment;
import com.example.spot.story.domain.association.StoryComment;
import com.example.spot.story.domain.enums.StoryCategory;
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
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class StoryCommandServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private StudyRepository studyRepository;
    @Mock
    private StudyMemberRepository studyMemberRepository;

    @Mock
    private StoryRepository storyRepository;
    @Mock
    private StoryImageRepository storyImageRepository;
    @Mock
    private StoryCommentRepository storyCommentRepository;
    @Mock
    private StoryReportRepository storyReportRepository;

    @Mock
    private LikedStoryRepository likedStoryRepository;
    @Mock
    private LikedStoryCommentRepository likedStoryCommentRepository;

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private S3ImageService s3ImageService;

    @InjectMocks
    private StoryCommandServiceImpl studyPostCommandService;

    private static Study study;
    private static Member member1;
    private static Member member2;
    private static Member owner;
    private static StudyMember member1Study;
    private static StudyMember ownerStudy;

    private static Story story1;
    private static Story story2;
    private static Story story3;
    private static LikedStory likedStory;
    private static StoryComment studyPost1Comment1;
    private static StoryComment studyPost1Comment2;
    private static LikedStoryComment likedStoryComment;
    private static LikedStoryComment studyDislikedComment;

    @BeforeEach
    void setUp() {
        initMember();
        initStudy();
        initMemberStudy();
        initStudyPost();
        initStudyLikedPost();
        initStudyPostComment();
        initStudyLikedComment();

        // Member
        when(memberRepository.findById(1L)).thenReturn(Optional.of(member1));
        when(memberRepository.findById(2L)).thenReturn(Optional.of(member2));
        when(memberRepository.findById(3L)).thenReturn(Optional.of(owner));

        // Study
        when(studyRepository.findById(1L)).thenReturn(Optional.of(study));

        // MemberStudy
        when(studyMemberRepository.findByMemberIdAndStudyIdAndStatus(1L, 1L, StudyApplicationStatus.APPROVED))
                .thenReturn(Optional.of(member1Study));
        when(studyMemberRepository.findByMemberIdAndStudyIdAndStatus(2L, 1L, StudyApplicationStatus.APPROVED))
                .thenReturn(Optional.empty());
        when(studyMemberRepository.findByMemberIdAndStudyIdAndStatus(3L, 1L, StudyApplicationStatus.APPROVED))
                .thenReturn(Optional.of(ownerStudy));

        // StudyPost
        when(storyRepository.findById(1L)).thenReturn(Optional.of(story1));
        when(storyRepository.findById(2L)).thenReturn(Optional.of(story2));
        when(storyRepository.findById(3L)).thenReturn(Optional.of(story3));

        when(likedStoryRepository.findByMemberIdAndStoryId(3L, 1L))
                .thenReturn(Optional.of(likedStory));
        when(likedStoryRepository.existsByMemberIdAndStoryId(3L, 1L))
                .thenReturn(true);

        // Comment
        when(storyCommentRepository.findAllByStoryId(1L))
                .thenReturn(List.of(studyPost1Comment1, studyPost1Comment2));
        when(storyCommentRepository.findById(1L)).thenReturn(Optional.of(studyPost1Comment1));
        when(storyCommentRepository.findById(2L)).thenReturn(Optional.of(studyPost1Comment2));

        // S3
        when(s3ImageService.upload(any(MultipartFile.class))).thenReturn("url");

    }

    /*-------------------------------------------------------- 게시글 작성 ------------------------------------------------------------------------*/

    @Test
    @DisplayName("스터디 게시글 작성 - 공지 게시글 (성공)")
    void createPost_Announcement_Success() {

        // given
        Long memberId = 3L;
        Long studyId = 1L;

        StoryRequestDTO.StoryDTO postPreviewDTO = StoryRequestDTO.StoryDTO.builder()
                .isAnnouncement(true)
                .storyCategory(StoryCategory.INFO_SHARING)
                .title("공지")
                .content("내용")
                .build();

        getAuthentication(memberId);

        when(studyMemberRepository.findByMemberIdAndStudyIdAndIsOwned(memberId, studyId, true))
                .thenReturn(Optional.of(ownerStudy));
        when(storyRepository.save(any(Story.class))).thenReturn(story2);
        when(notificationRepository.save(any(Notification.class))).thenReturn(null);
        when(studyMemberRepository.findAllByStudyIdAndStatus(studyId, StudyApplicationStatus.APPROVED))
                .thenReturn(List.of(member1Study, ownerStudy));

        // when
        StoryResponseDTO.StoryPreviewDTO result = studyPostCommandService.createPost(studyId, postPreviewDTO);

        // then
        assertNotNull(result);
        assertThat(result.getTitle()).isEqualTo("공지");
        verify(storyRepository, times(1)).save(any(Story.class));
    }

    @Test
    @DisplayName("스터디 게시글 작성 - 일반 게시글 (성공)")
    void createPost_Common_Success() {

        // given
        Long memberId = 1L;
        Long studyId = 1L;

        StoryRequestDTO.StoryDTO postPreviewDTO = StoryRequestDTO.StoryDTO.builder()
                .isAnnouncement(false)
                .storyCategory(StoryCategory.FREE_TALK)
                .title("잡담")
                .content("내용")
                .build();

        getAuthentication(memberId);

        when(studyMemberRepository.findByMemberIdAndStudyIdAndIsOwned(memberId, studyId, true))
                .thenReturn(Optional.of(member1Study));
        when(storyRepository.save(any(Story.class))).thenReturn(story1);
        when(notificationRepository.save(any(Notification.class))).thenReturn(null);
        when(studyMemberRepository.findAllByStudyIdAndStatus(studyId, StudyApplicationStatus.APPROVED))
                .thenReturn(List.of(member1Study, ownerStudy));

        // when
        StoryResponseDTO.StoryPreviewDTO result = studyPostCommandService.createPost(studyId, postPreviewDTO);

        // then
        assertNotNull(result);
        assertThat(result.getTitle()).isEqualTo("잡담");
        verify(storyRepository, times(1)).save(any(Story.class));
    }

    @Test
    @DisplayName("스터디 게시글 작성 - 스터디 회원이 아닌 경우 (실패)")
    void createPost_NotStudyMember_Fail() {

        // given
        Long memberId = 2L;
        Long studyId = 1L;

        StoryRequestDTO.StoryDTO postPreviewDTO = StoryRequestDTO.StoryDTO.builder()
                .isAnnouncement(true)
                .storyCategory(StoryCategory.INFO_SHARING)
                .title("공지")
                .content("내용")
                .build();

        getAuthentication(memberId);

        when(studyMemberRepository.findByMemberIdAndStudyIdAndIsOwned(memberId, studyId, true))
                .thenReturn(Optional.of(ownerStudy));
        when(storyRepository.save(any(Story.class))).thenReturn(story2);
        when(notificationRepository.save(any(Notification.class))).thenReturn(null);
        when(studyMemberRepository.findAllByStudyIdAndStatus(studyId, StudyApplicationStatus.APPROVED))
                .thenReturn(List.of(member1Study, ownerStudy));

        // when & then
        assertThrows(StudyHandler.class, () -> studyPostCommandService.createPost(studyId, postPreviewDTO));
    }

    @Test
    @DisplayName("스터디 게시글 작성 - 스터디장이 아닌 회원이 공지 게시글을 작성하는 경우 (실패)")
    void createPost_MemberAnnounced_Fail() {

        // given
        Long memberId = 2L;
        Long studyId = 1L;

        StoryRequestDTO.StoryDTO postPreviewDTO = StoryRequestDTO.StoryDTO.builder()
                .isAnnouncement(true)
                .storyCategory(StoryCategory.INFO_SHARING)
                .title("공지")
                .content("내용")
                .build();

        getAuthentication(memberId);

        when(studyMemberRepository.findByMemberIdAndStudyIdAndIsOwned(memberId, studyId, true))
                .thenReturn(Optional.of(member1Study));
        when(storyRepository.save(any(Story.class))).thenReturn(story2);
        when(notificationRepository.save(any(Notification.class))).thenReturn(null);
        when(studyMemberRepository.findAllByStudyIdAndStatus(studyId, StudyApplicationStatus.APPROVED))
                .thenReturn(List.of(member1Study, ownerStudy));

        // when & then
        assertThrows(StudyHandler.class, () -> studyPostCommandService.createPost(studyId, postPreviewDTO));
    }

    @Test
    @DisplayName("스터디 게시글 작성 - 제목이 50자를 초과하는 경우 (실패)")
    void createPost_TitleOverflow_Fail() {

        // given
        Long memberId = 1L;
        Long studyId = 1L;

        StoryRequestDTO.StoryDTO postPreviewDTO = StoryRequestDTO.StoryDTO.builder()
                .isAnnouncement(true)
                .storyCategory(StoryCategory.INFO_SHARING)
                .title("50자가 넘어가는 제목 "
                        + "50자가 넘어가는 제목 "
                        + "50자가 넘어가는 제목 "
                        + "50자가 넘어가는 제목 "
                        + "50자가 넘어가는 제목 ")
                .content("내용")
                .build();

        getAuthentication(memberId);

        when(studyMemberRepository.findByMemberIdAndStudyIdAndIsOwned(memberId, studyId, true))
                .thenReturn(Optional.of(ownerStudy));
        when(storyRepository.save(any(Story.class))).thenReturn(story2);
        when(notificationRepository.save(any(Notification.class))).thenReturn(null);
        when(studyMemberRepository.findAllByStudyIdAndStatus(studyId, StudyApplicationStatus.APPROVED))
                .thenReturn(List.of(member1Study, ownerStudy));

        // when & then
        assertThrows(StudyHandler.class, () -> studyPostCommandService.createPost(studyId, postPreviewDTO));
    }


    /*-------------------------------------------------------- 게시글 삭제 ------------------------------------------------------------------------*/

    @Test
    @DisplayName("스터디 게시글 삭제 - (성공)")
    void deletePost_Success() {

        // given
        Long memberId = 1L;
        Long studyId = 1L;
        Long postId = 1L;

        getAuthentication(memberId);

        when(storyRepository.findByIdAndStudyId(postId, studyId))
                .thenReturn(Optional.of(story1));
        when(storyRepository.findByIdAndMemberId(postId, memberId))
                .thenReturn(Optional.of(story1));

        // when
        StoryResponseDTO.StoryPreviewDTO result = studyPostCommandService.deletePost(studyId, postId);

        // then
        assertNotNull(result);
        assertThat(result.getPostId()).isEqualTo(1L);
        assertThat(result.getTitle()).isEqualTo("잡담");
        verify(storyRepository, times(1)).delete(any(Story.class));
        verify(likedStoryRepository, times(1)).deleteAllByStoryId(postId);
    }

    @Test
    @DisplayName("스터디 게시글 삭제 - 이미 삭제된 게시글인 경우 (실패)")
    void deletePost_AlreadyDeleted_Fail() {

        // given
        Long memberId = 1L;
        Long studyId = 1L;
        Long postId = 1L;

        getAuthentication(memberId);

        when(storyRepository.findByIdAndStudyId(postId, studyId))
                .thenReturn(Optional.empty());
        when(storyRepository.findByIdAndMemberId(postId, memberId))
                .thenReturn(Optional.empty());

        // when & then
        assertThrows(StudyHandler.class, () -> studyPostCommandService.deletePost(studyId, postId));
    }

    @Test
    @DisplayName("스터디 게시글 삭제 - 작성자 본인이나 스터디장이 아닌 경우 (실패)")
    void deletePost_NotAvailableMember_Fail() {

        // given
        Long memberId = 2L;
        Long studyId = 1L;
        Long postId = 1L;

        getAuthentication(memberId);

        when(storyRepository.findByIdAndStudyId(postId, studyId))
                .thenReturn(Optional.of(story1));
        when(storyRepository.findByIdAndMemberId(postId, memberId))
                .thenReturn(Optional.of(story1));

        // when & then
        assertThrows(StudyHandler.class, () -> studyPostCommandService.deletePost(studyId, postId));
    }

    /*-------------------------------------------------------- 게시글 좋아요 ------------------------------------------------------------------------*/

    @Test
    @DisplayName("스터디 게시글 좋아요 - (성공)")
    void likePost_Success() {

        // given
        Long memberId = 3L;
        Long studyId = 1L;
        Long postId = 1L;

        getAuthentication(memberId);

        when(storyRepository.findByIdAndStudyId(postId, studyId))
                .thenReturn(Optional.of(story1));
        when(likedStoryRepository.findByMemberIdAndStoryId(memberId, postId))
                .thenReturn(Optional.empty());
        when(likedStoryRepository.save(any(LikedStory.class))).thenReturn(likedStory);
        when(storyRepository.save(any(Story.class))).thenReturn(story1);

        // when
        StoryResponseDTO.StoryLikeNumDTO result = studyPostCommandService.likePost(studyId, postId);

        // then
        assertNotNull(result);
        assertThat(result.getPostId()).isEqualTo(1L);
        assertThat(result.getTitle()).isEqualTo("잡담");
        assertThat(result.getLikeNum()).isEqualTo(2);
        verify(likedStoryRepository, times(1)).save(any(LikedStory.class));
    }

    @Test
    @DisplayName("스터디 게시글 좋아요 - 스터디 회원이 아닌 경우 (실패)")
    void likePost_NotStudyMember_Fail() {

        // given
        Long memberId = 2L;
        Long studyId = 1L;
        Long postId = 1L;

        getAuthentication(memberId);

        when(storyRepository.findByIdAndStudyId(postId, studyId))
                .thenReturn(Optional.of(story1));
        when(likedStoryRepository.findByMemberIdAndStoryId(memberId, postId))
                .thenReturn(Optional.empty());
        when(likedStoryRepository.save(any(LikedStory.class))).thenReturn(likedStory);
        when(storyRepository.save(any(Story.class))).thenReturn(story1);

        // when & then
        assertThrows(StudyHandler.class, () -> studyPostCommandService.likePost(studyId, postId));
    }

    @Test
    @DisplayName("스터디 게시글 좋아요 - 이미 좋아요를 누른 경우 (실패)")
    void likePost_AlreadyLiked_Fail() {

        // given
        Long memberId = 3L;
        Long studyId = 1L;
        Long postId = 1L;

        getAuthentication(memberId);

        when(storyRepository.findByIdAndStudyId(postId, studyId))
                .thenReturn(Optional.of(story1));
        when(likedStoryRepository.findByMemberIdAndStoryId(memberId, postId))
                .thenReturn(Optional.of(likedStory));
        when(likedStoryRepository.save(any(LikedStory.class))).thenReturn(likedStory);
        when(storyRepository.save(any(Story.class))).thenReturn(story1);

        // when & then
        assertThrows(StudyHandler.class, () -> studyPostCommandService.likePost(studyId, postId));
    }


    /*-------------------------------------------------------- 게시글 좋아요 취소 ------------------------------------------------------------------------*/

    @Test
    @DisplayName("스터디 게시글 좋아요 취소 - (성공)")
    void cancelPostLike_Success() {

        // given
        Long memberId = 3L;
        Long studyId = 1L;
        Long postId = 1L;

        getAuthentication(memberId);

        when(storyRepository.findByIdAndStudyId(postId, studyId))
                .thenReturn(Optional.of(story1));
        when(likedStoryRepository.findByMemberIdAndStoryId(memberId, postId))
                .thenReturn(Optional.of(likedStory));
        when(storyRepository.save(any(Story.class))).thenReturn(story1);

        // when
        StoryResponseDTO.StoryLikeNumDTO result = studyPostCommandService.cancelPostLike(studyId, postId);

        // then
        assertNotNull(result);
        assertThat(result.getPostId()).isEqualTo(1L);
        assertThat(result.getTitle()).isEqualTo("잡담");
        assertThat(result.getLikeNum()).isEqualTo(0);
    }

    @Test
    @DisplayName("스터디 게시글 좋아요 취소 - 스터디 회원이 아닌 경우 (실패)")
    void cancelPostLike_NotStudyMember_Fail() {

        // given
        Long memberId = 2L;
        Long studyId = 1L;
        Long postId = 1L;

        getAuthentication(memberId);

        when(storyRepository.findByIdAndStudyId(postId, studyId))
                .thenReturn(Optional.of(story1));
        when(likedStoryRepository.findByMemberIdAndStoryId(memberId, postId))
                .thenReturn(Optional.of(likedStory));
        when(storyRepository.save(any(Story.class))).thenReturn(story1);

        // when & then
        assertThrows(StudyHandler.class, () -> studyPostCommandService.cancelPostLike(studyId, postId));
    }

    @Test
    @DisplayName("스터디 게시글 좋아요 취소 - 좋아요를 누르지 않은 게시글인 경우 (실패)")
    void cancelPostLike_NotLiked_Fail() {

        // given
        Long memberId = 3L;
        Long studyId = 1L;
        Long postId = 1L;

        getAuthentication(memberId);

        when(storyRepository.findByIdAndStudyId(postId, studyId))
                .thenReturn(Optional.of(story1));
        when(likedStoryRepository.findByMemberIdAndStoryId(memberId, postId))
                .thenReturn(Optional.empty());
        when(storyRepository.save(any(Story.class))).thenReturn(story1);

        // when & then
        assertThrows(StudyHandler.class, () -> studyPostCommandService.cancelPostLike(studyId, postId));
    }


    /*-------------------------------------------------------- 댓글 작성 ------------------------------------------------------------------------*/

    @Test
    @DisplayName("스터디 게시글 댓글 작성 - 익명 댓글 (성공)")
    void createComment_Anonymous_Success() {

        // given
        Long memberId = 1L;
        Long studyId = 1L;
        Long postId = 1L;

        getAuthentication(memberId);

        StoryCommentRequestDTO.CommentDTO commentDTO = StoryCommentRequestDTO.CommentDTO.builder()
                .content("댓글")
                .isAnonymous(true)
                .build();

        when(storyRepository.findByIdAndStudyId(postId, studyId))
                .thenReturn(Optional.of(story1));
        when(storyCommentRepository.save(any(StoryComment.class))).thenReturn(studyPost1Comment1);
        when(storyRepository.save(any(Story.class))).thenReturn(story1);
        when(storyCommentRepository.findAllByStoryId(postId)).thenReturn(List.of());
        when(storyCommentRepository.findAllByMemberIdAndStoryId(memberId, postId)).thenReturn(List.of());

        // when
        StoryCommentResponseDTO.CommentDTO result = studyPostCommandService.createComment(studyId, postId, commentDTO);

        // then
        assertNotNull(result);
        assertThat(result.getMember().getMemberId()).isEqualTo(1L);
        assertThat(result.getMember().getName()).isEqualTo("익명1");
        assertThat(result.getContent()).isEqualTo("댓글");
    }

    @Test
    @DisplayName("스터디 게시글 댓글 작성 - 실명 댓글 (성공)")
    void createComment_Name_Success() {

        // given
        Long memberId = 1L;
        Long studyId = 1L;
        Long postId = 1L;

        getAuthentication(memberId);

        StoryCommentRequestDTO.CommentDTO commentDTO = StoryCommentRequestDTO.CommentDTO.builder()
                .content("댓글")
                .isAnonymous(false)
                .build();

        when(storyRepository.findByIdAndStudyId(postId, studyId))
                .thenReturn(Optional.of(story1));
        when(storyCommentRepository.save(any(StoryComment.class))).thenReturn(studyPost1Comment1);
        when(storyRepository.save(any(Story.class))).thenReturn(story1);
        when(storyCommentRepository.findAllByStoryId(postId)).thenReturn(List.of());
        when(storyCommentRepository.findAllByMemberIdAndStoryId(memberId, postId)).thenReturn(List.of());

        // when
        StoryCommentResponseDTO.CommentDTO result = studyPostCommandService.createComment(studyId, postId, commentDTO);

        // then
        assertNotNull(result);
        assertThat(result.getMember().getMemberId()).isEqualTo(1L);
        assertThat(result.getMember().getName()).isEqualTo("회원1");
        assertThat(result.getContent()).isEqualTo("댓글");
    }

    @Test
    @DisplayName("스터디 게시글 댓글 작성 - 스터디 회원이 아닌 경우 (실패)")
    void createComment_NotStudyMember_Fail() {

        // given
        Long memberId = 2L;
        Long studyId = 1L;
        Long postId = 1L;

        getAuthentication(memberId);

        StoryCommentRequestDTO.CommentDTO commentDTO = StoryCommentRequestDTO.CommentDTO.builder()
                .content("댓글")
                .isAnonymous(false)
                .build();

        when(storyRepository.findByIdAndStudyId(postId, studyId))
                .thenReturn(Optional.of(story1));
        when(storyCommentRepository.save(any(StoryComment.class))).thenReturn(studyPost1Comment1);
        when(storyRepository.save(any(Story.class))).thenReturn(story1);
        when(storyCommentRepository.findAllByStoryId(postId)).thenReturn(List.of());
        when(storyCommentRepository.findAllByMemberIdAndStoryId(memberId, postId)).thenReturn(List.of());

        // when
        assertThrows(StudyHandler.class, () -> studyPostCommandService.createComment(studyId, postId, commentDTO));
    }



    /*-------------------------------------------------------- 답글 작성 ------------------------------------------------------------------------*/

    @Test
    @DisplayName("스터디 게시글 답글 작성 - 익명 댓글 (성공)")
    void createReply_Anonymous_Success() {

        // given
        Long memberId = 3L;
        Long studyId = 1L;
        Long postId = 1L;
        Long commentId = 1L;

        getAuthentication(memberId);

        StoryCommentRequestDTO.CommentDTO commentDTO = StoryCommentRequestDTO.CommentDTO.builder()
                .content("답글")
                .isAnonymous(true)
                .build();

        when(storyRepository.findByIdAndStudyId(postId, studyId))
                .thenReturn(Optional.of(story1));
        when(storyCommentRepository.save(any(StoryComment.class))).thenReturn(studyPost1Comment1);
        when(storyRepository.save(any(Story.class))).thenReturn(story1);
        when(storyCommentRepository.findAllByStoryId(postId))
                .thenReturn(List.of(studyPost1Comment1));
        when(storyCommentRepository.findAllByMemberIdAndStoryId(memberId, postId))
                .thenReturn(List.of());

        // when
        StoryCommentResponseDTO.CommentDTO result = studyPostCommandService
                .createReply(studyId, postId, commentId, commentDTO);

        //then
        assertNotNull(result);
        assertThat(result.getMember().getMemberId()).isEqualTo(3L);
        assertThat(result.getMember().getName()).isEqualTo("익명2");
        assertThat(result.getContent()).isEqualTo("답글");
    }

    @Test
    @DisplayName("스터디 게시글 답글 작성 - 실명 댓글 (성공)")
    void createReply_Name_Success() {

        // given
        Long memberId = 3L;
        Long studyId = 1L;
        Long postId = 1L;
        Long commentId = 1L;

        getAuthentication(memberId);

        StoryCommentRequestDTO.CommentDTO commentDTO = StoryCommentRequestDTO.CommentDTO.builder()
                .content("답글")
                .isAnonymous(false)
                .build();

        when(storyRepository.findByIdAndStudyId(postId, studyId))
                .thenReturn(Optional.of(story1));
        when(storyCommentRepository.save(any(StoryComment.class))).thenReturn(studyPost1Comment1);
        when(storyRepository.save(any(Story.class))).thenReturn(story1);
        when(storyCommentRepository.findAllByStoryId(postId))
                .thenReturn(List.of(studyPost1Comment1));
        when(storyCommentRepository.findAllByMemberIdAndStoryId(memberId, postId))
                .thenReturn(List.of());

        // when
        StoryCommentResponseDTO.CommentDTO result = studyPostCommandService
                .createReply(studyId, postId, commentId, commentDTO);

        //then
        assertNotNull(result);
        assertThat(result.getMember().getMemberId()).isEqualTo(3L);
        assertThat(result.getMember().getName()).isEqualTo("회원3");
        assertThat(result.getContent()).isEqualTo("답글");
    }

    @Test
    @DisplayName("스터디 게시글 답글 작성 - 스터디 회원이 아닌 경우 (실패)")
    void createReply_NotStudyMember_Fail() {

        // given
        Long memberId = 2L;
        Long studyId = 1L;
        Long postId = 1L;
        Long commentId = 1L;

        getAuthentication(memberId);

        StoryCommentRequestDTO.CommentDTO commentDTO = StoryCommentRequestDTO.CommentDTO.builder()
                .content("답글")
                .isAnonymous(false)
                .build();

        when(storyRepository.findByIdAndStudyId(postId, studyId))
                .thenReturn(Optional.of(story1));
        when(storyCommentRepository.save(any(StoryComment.class))).thenReturn(studyPost1Comment1);
        when(storyRepository.save(any(Story.class))).thenReturn(story1);
        when(storyCommentRepository.findAllByStoryId(postId))
                .thenReturn(List.of(studyPost1Comment1));
        when(storyCommentRepository.findAllByMemberIdAndStoryId(memberId, postId))
                .thenReturn(List.of());

        // when
        assertThrows(StudyHandler.class,
                () -> studyPostCommandService.createReply(studyId, postId, commentId, commentDTO));
    }

    @Test
    @DisplayName("스터디 게시글 답글 작성 - 상위 댓글이 존재하지 않는 경우 (실패)")
    void createReply_ParentCommentNotExist_Fail() {

        // given
        Long memberId = 1L;
        Long studyId = 1L;
        Long postId = 1L;

        getAuthentication(memberId);

        StoryCommentRequestDTO.CommentDTO commentDTO = StoryCommentRequestDTO.CommentDTO.builder()
                .content("답글")
                .isAnonymous(false)
                .build();

        when(storyRepository.findByIdAndStudyId(postId, studyId))
                .thenReturn(Optional.of(story1));
        when(storyCommentRepository.save(any(StoryComment.class))).thenReturn(studyPost1Comment1);
        when(storyRepository.save(any(Story.class))).thenReturn(story1);
        when(storyCommentRepository.findAllByStoryId(postId))
                .thenReturn(List.of(studyPost1Comment1, studyPost1Comment2));
        when(storyCommentRepository.findAllByMemberIdAndStoryId(memberId, postId))
                .thenReturn(List.of(studyPost1Comment1));

        // when
        assertThrows(StudyHandler.class, () -> studyPostCommandService.createReply(studyId, postId, null, commentDTO));
    }


    /*-------------------------------------------------------- 댓글 삭제 ------------------------------------------------------------------------*/

    // @Test
    // @DisplayName("스터디 게시글 댓글 삭제")
    // void deleteComment() {
    // }

    /*-------------------------------------------------------- 댓글 좋아요 ------------------------------------------------------------------------*/

    @Test
    @DisplayName("스터디 게시글 댓글 좋아요 - (성공)")
    void likeComment_Success() {

        // given
        Long memberId = 1L;
        Long studyId = 1L;
        Long postId = 1L;
        Long commentId = 1L;

        getAuthentication(memberId);

        when(likedStoryCommentRepository.findByMemberIdAndStoryCommentIdAndIsLiked(memberId, commentId, true))
                .thenReturn(Optional.empty());
        when(likedStoryCommentRepository.findByMemberIdAndStoryCommentIdAndIsLiked(memberId, commentId, false))
                .thenReturn(Optional.empty());
        when(likedStoryCommentRepository.save(any(LikedStoryComment.class))).thenReturn(likedStoryComment);

        // when
        StoryCommentResponseDTO.CommentPreviewDTO result = studyPostCommandService.likeComment(studyId, postId,
                commentId);

        // then
        assertNotNull(result);
        assertThat(result.getCommentId()).isEqualTo(1L);
        assertThat(result.getLikeCount()).isEqualTo(1L);
        assertThat(result.getDislikeCount()).isEqualTo(0L);
    }

    @Test
    @DisplayName("스터디 게시글 댓글 좋아요 - 스터디 회원이 아닌 경우 (실패)")
    void likeComment_NotStudyMember_Fail() {

        // given
        Long memberId = 2L;
        Long studyId = 1L;
        Long postId = 1L;
        Long commentId = 1L;

        getAuthentication(memberId);

        when(likedStoryCommentRepository.findByMemberIdAndStoryCommentIdAndIsLiked(memberId, commentId, true))
                .thenReturn(Optional.empty());
        when(likedStoryCommentRepository.findByMemberIdAndStoryCommentIdAndIsLiked(memberId, commentId, false))
                .thenReturn(Optional.empty());
        when(likedStoryCommentRepository.save(any(LikedStoryComment.class))).thenReturn(likedStoryComment);

        // when
        assertThrows(StudyHandler.class, () -> studyPostCommandService.likeComment(studyId, postId, commentId));
    }

    @Test
    @DisplayName("스터디 게시글 댓글 좋아요 - 이미 좋아요를 누른 경우 (실패)")
    void likeComment_AlreadyLiked_Fail() {

        // given
        Long memberId = 1L;
        Long studyId = 1L;
        Long postId = 1L;
        Long commentId = 2L;

        getAuthentication(memberId);

        when(likedStoryCommentRepository.findByMemberIdAndStoryCommentIdAndIsLiked(memberId, commentId, true))
                .thenReturn(Optional.of(likedStoryComment));
        when(likedStoryCommentRepository.findByMemberIdAndStoryCommentIdAndIsLiked(memberId, commentId, false))
                .thenReturn(Optional.empty());
        when(likedStoryCommentRepository.save(any(LikedStoryComment.class))).thenReturn(likedStoryComment);

        // when
        assertThrows(StudyHandler.class, () -> studyPostCommandService.likeComment(studyId, postId, commentId));
    }


    /*-------------------------------------------------------- 댓글 싫어요 ------------------------------------------------------------------------*/

    @Test
    @DisplayName("스터디 게시글 댓글 싫어요 - (성공)")
    void dislikeComment_Success() {

        // given
        Long memberId = 1L;
        Long studyId = 1L;
        Long postId = 1L;
        Long commentId = 1L;

        getAuthentication(memberId);

        when(likedStoryCommentRepository.findByMemberIdAndStoryCommentIdAndIsLiked(memberId, commentId, true))
                .thenReturn(Optional.empty());
        when(likedStoryCommentRepository.findByMemberIdAndStoryCommentIdAndIsLiked(memberId, commentId, false))
                .thenReturn(Optional.empty());
        when(likedStoryCommentRepository.save(any(LikedStoryComment.class))).thenReturn(likedStoryComment);

        // when
        StoryCommentResponseDTO.CommentPreviewDTO result = studyPostCommandService.dislikeComment(studyId, postId,
                commentId);

        // then
        assertNotNull(result);
        assertThat(result.getCommentId()).isEqualTo(1L);
        assertThat(result.getLikeCount()).isEqualTo(1L);
        assertThat(result.getDislikeCount()).isEqualTo(0L);
    }

    @Test
    @DisplayName("스터디 게시글 댓글 싫어요 - 스터디 회원인 아닌 경우(실패)")
    void dislikeComment_NotStudyMember_Fail() {

        // given
        Long memberId = 2L;
        Long studyId = 1L;
        Long postId = 1L;
        Long commentId = 1L;

        getAuthentication(memberId);

        when(likedStoryCommentRepository.findByMemberIdAndStoryCommentIdAndIsLiked(memberId, commentId, true))
                .thenReturn(Optional.empty());
        when(likedStoryCommentRepository.findByMemberIdAndStoryCommentIdAndIsLiked(memberId, commentId, false))
                .thenReturn(Optional.empty());
        when(likedStoryCommentRepository.save(any(LikedStoryComment.class))).thenReturn(likedStoryComment);

        // when
        assertThrows(StudyHandler.class, () -> studyPostCommandService.dislikeComment(studyId, postId, commentId));

    }

    @Test
    @DisplayName("스터디 게시글 댓글 싫어요 - 이미 싫어요를 누른 경우(실패)")
    void dislikeComment_AlreadyDisliked_Fail() {

        // given
        Long memberId = 3L;
        Long studyId = 1L;
        Long postId = 1L;
        Long commentId = 2L;

        getAuthentication(memberId);

        when(likedStoryCommentRepository.findByMemberIdAndStoryCommentIdAndIsLiked(memberId, commentId, true))
                .thenReturn(Optional.empty());
        when(likedStoryCommentRepository.findByMemberIdAndStoryCommentIdAndIsLiked(memberId, commentId, false))
                .thenReturn(Optional.of(likedStoryComment));
        when(likedStoryCommentRepository.save(any(LikedStoryComment.class))).thenReturn(likedStoryComment);

        // when
        assertThrows(StudyHandler.class, () -> studyPostCommandService.dislikeComment(studyId, postId, commentId));
    }

    /*-------------------------------------------------------- 댓글 좋아요 취소 ------------------------------------------------------------------------*/

    @Test
    @DisplayName("스터디 게시글 댓글 좋아요 취소 - (성공)")
    void cancelCommentLike_Success() {

        // given
        Long memberId = 1L;
        Long studyId = 1L;
        Long postId = 1L;
        Long commentId = 2L;

        getAuthentication(memberId);

        when(likedStoryCommentRepository.findByMemberIdAndStoryCommentIdAndIsLiked(memberId, studyPost1Comment2.getId(),
                true))
                .thenReturn(Optional.of(likedStoryComment));
        when(storyCommentRepository.save(any(StoryComment.class))).thenReturn(studyPost1Comment2);

        // when
        StoryCommentResponseDTO.CommentPreviewDTO result = studyPostCommandService
                .cancelCommentLike(studyId, postId, commentId);

        // then
        assertNotNull(result);
        assertThat(result.getCommentId()).isEqualTo(2L);
        assertThat(result.getLikeCount()).isEqualTo(0L);
        assertThat(result.getDislikeCount()).isEqualTo(1L);
    }

    @Test
    @DisplayName("스터디 게시글 댓글 좋아요 취소 - 스터디 회원이 아닌 경우 (실패)")
    void cancelCommentLike_NotStudyMember_Fail() {

        // given
        Long memberId = 2L;
        Long studyId = 1L;
        Long postId = 1L;
        Long commentId = 2L;

        getAuthentication(memberId);

        when(likedStoryCommentRepository.findByMemberIdAndStoryCommentIdAndIsLiked(memberId, studyPost1Comment2.getId(),
                true))
                .thenReturn(Optional.of(likedStoryComment));
        when(storyCommentRepository.save(any(StoryComment.class))).thenReturn(studyPost1Comment2);

        // when & then
        assertThrows(StudyHandler.class, () -> studyPostCommandService.cancelCommentLike(studyId, postId, commentId));
    }

    @Test
    @DisplayName("스터디 게시글 댓글 좋아요 취소 - 좋아요를 누른 댓글이 아닌 경우 (실패)")
    void cancelCommentLike_NotLiked_Fail() {

        // given
        Long memberId = 3L;
        Long studyId = 1L;
        Long postId = 1L;
        Long commentId = 2L;

        getAuthentication(memberId);

        when(likedStoryCommentRepository.findByMemberIdAndStoryCommentIdAndIsLiked(memberId, studyPost1Comment2.getId(),
                true))
                .thenReturn(Optional.empty());
        when(storyCommentRepository.save(any(StoryComment.class))).thenReturn(studyPost1Comment2);

        // when & then
        assertThrows(StudyHandler.class, () -> studyPostCommandService.cancelCommentLike(studyId, postId, commentId));
    }

    /*-------------------------------------------------------- 댓글 싫어요 취소 ------------------------------------------------------------------------*/

    @Test
    @DisplayName("스터디 게시글 댓글 싫어요 취소 - (성공)")
    void cancelCommentDislike() {

        // given
        Long memberId = 3L;
        Long studyId = 1L;
        Long postId = 1L;
        Long commentId = 2L;

        getAuthentication(memberId);

        when(likedStoryCommentRepository.findByMemberIdAndStoryCommentIdAndIsLiked(memberId, studyPost1Comment2.getId(),
                false))
                .thenReturn(Optional.of(studyDislikedComment));
        when(storyCommentRepository.save(any(StoryComment.class))).thenReturn(studyPost1Comment2);

        // when
        StoryCommentResponseDTO.CommentPreviewDTO result = studyPostCommandService
                .cancelCommentDislike(studyId, postId, commentId);

        // then
        assertNotNull(result);
        assertThat(result.getCommentId()).isEqualTo(2L);
        assertThat(result.getLikeCount()).isEqualTo(1L);
        assertThat(result.getDislikeCount()).isEqualTo(0L);
    }

    @Test
    @DisplayName("스터디 게시글 댓글 싫어요 취소 - 스터디 회원이 아닌 경우 (실패)")
    void cancelCommentDislike_NotStudyMember_Fail() {

        // given
        Long memberId = 2L;
        Long studyId = 1L;
        Long postId = 1L;
        Long commentId = 2L;

        getAuthentication(memberId);

        when(likedStoryCommentRepository.findByMemberIdAndStoryCommentIdAndIsLiked(memberId, studyPost1Comment2.getId(),
                false))
                .thenReturn(Optional.of(studyDislikedComment));
        when(storyCommentRepository.save(any(StoryComment.class))).thenReturn(studyPost1Comment2);

        // when & then
        assertThrows(StudyHandler.class,
                () -> studyPostCommandService.cancelCommentDislike(studyId, postId, commentId));
    }

    @Test
    @DisplayName("스터디 게시글 댓글 싫어요 취소 - 싫어요를 누른 댓글이 아닌 경우 (실패)")
    void cancelCommentDislike_NotDisliked_Fail() {

        // given
        Long memberId = 1L;
        Long studyId = 1L;
        Long postId = 1L;
        Long commentId = 2L;

        getAuthentication(memberId);

        when(likedStoryCommentRepository.findByMemberIdAndStoryCommentIdAndIsLiked(memberId, studyPost1Comment2.getId(),
                false))
                .thenReturn(Optional.empty());
        when(storyCommentRepository.save(any(StoryComment.class))).thenReturn(studyPost1Comment2);

        // when & then
        assertThrows(StudyHandler.class,
                () -> studyPostCommandService.cancelCommentDislike(studyId, postId, commentId));
    }


    /*-------------------------------------------------------- Utils ------------------------------------------------------------------------*/

    private static void getAuthentication(Long memberId) {
        String idString = String.valueOf(memberId);
        Authentication authentication = new UsernamePasswordAuthenticationToken(idString, null,
                Collections.emptyList());
        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

    private static void initMember() {
        member1 = Member.builder()
                .id(1L)
                .name("회원1")
                .build();
        member2 = Member.builder()
                .id(2L)
                .name("회원2")
                .build();
        owner = Member.builder()
                .id(3L)
                .name("회원3")
                .build();
    }

    private static void initStudy() {
        study = Study.builder()
                .id(1L)
                .gender(Gender.MALE)
                .minAge(20)
                .maxAge(29)
                .fee(10000)
                .profileImage("a.jpg")
                .hasFee(true)
                .isOnline(true)
                .goal("SQLD")
                .introduction("SQLD 자격증 스터디")
                .title("SQLD Master")
                .maxPeople(10L)
                .build();
    }

    private static void initMemberStudy() {
        ownerStudy = StudyMember.builder()
                .id(1L)
                .status(StudyApplicationStatus.APPROVED)
                .isOwned(true)
                .introduction("Hi")
                .member(owner)
                .study(study)
                .build();
        study.addMemberStudy(ownerStudy);

        member1Study = StudyMember.builder()
                .id(2L)
                .status(StudyApplicationStatus.APPROVED)
                .isOwned(false)
                .introduction("Hi")
                .member(member1)
                .study(study)
                .build();
        study.addMemberStudy(member1Study);
    }

    private static void initStudyPost() {
        story1 = Story.builder()
                .id(1L)
                .member(member1)
                .study(study)
                .isAnnouncement(false)
                .storyCategory(StoryCategory.FREE_TALK)
                .title("잡담")
                .content("내용")
                .hitNum(0)
                .likeNum(0)
                .commentNum(0)
                .build();
        study.addStudyPost(story1);

        story2 = Story.builder()
                .id(2L)
                .member(owner)
                .study(study)
                .isAnnouncement(true)
                .storyCategory(StoryCategory.INFO_SHARING)
                .title("공지")
                .content("내용")
                .hitNum(0)
                .likeNum(0)
                .commentNum(0)
                .build();
        study.addStudyPost(story2);

        story3 = Story.builder()
                .id(3L)
                .member(owner)
                .study(study)
                .isAnnouncement(false)
                .storyCategory(StoryCategory.FREE_TALK)
                .title("테스트")
                .content("내용")
                .hitNum(0)
                .likeNum(0)
                .commentNum(0)
                .build();
        study.addStudyPost(story3);

        for (int i = 0; i < 10; i++) {
            story1.plusHitNum();
        }
    }

    private static void initStudyLikedPost() {
        likedStory = LikedStory.builder()
                .id(1L)
                .story(story1)
                .member(owner)
                .build();
        story1.addLikedPost(likedStory);
        story1.plusLikeNum();
    }

    private static void initStudyPostComment() {
        studyPost1Comment1 = StoryComment.builder()
                .id(1L)
                .story(story1)
                .member(member1)
                .content("댓글")
                .likeCount(0)
                .dislikeCount(0)
                .isAnonymous(true)
                .anonymousNum(1)
                .isDeleted(false)
                .parentComment(null)
                .build();
        story1.addComment(studyPost1Comment1);

        studyPost1Comment2 = StoryComment.builder()
                .id(2L)
                .story(story1)
                .member(owner)
                .content("답글")
                .likeCount(0)
                .dislikeCount(0)
                .isAnonymous(false)
                .isDeleted(false)
                .parentComment(studyPost1Comment1)
                .build();
        studyPost1Comment1.addChildrenComment(studyPost1Comment2);
        story1.addComment(studyPost1Comment2);
    }

    private static void initStudyLikedComment() {
        likedStoryComment = LikedStoryComment.builder()
                .id(1L)
                .isLiked(true)
                .storyComment(studyPost1Comment2)
                .member(member1)
                .build();
        studyPost1Comment2.addLikedComment(likedStoryComment);
        studyPost1Comment2.plusLikeCount();

        studyDislikedComment = LikedStoryComment.builder()
                .id(2L)
                .isLiked(false)
                .storyComment(studyPost1Comment2)
                .member(owner)
                .build();
        studyPost1Comment2.addLikedComment(studyDislikedComment);
        studyPost1Comment2.plusDislikeCount();
    }
}