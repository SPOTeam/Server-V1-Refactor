package com.example.spot.service.story;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.example.spot.common.api.exception.GeneralException;
import com.example.spot.common.api.exception.handler.StudyHandler;
import com.example.spot.member.domain.Member;
import com.example.spot.member.domain.enums.Gender;
import com.example.spot.member.infrastructure.MemberRepository;
import com.example.spot.story.application.StoryQueryServiceImpl;
import com.example.spot.story.domain.Story;
import com.example.spot.story.domain.StoryRepository;
import com.example.spot.story.domain.association.LikedStory;
import com.example.spot.story.domain.association.LikedStoryComment;
import com.example.spot.story.domain.association.StoryComment;
import com.example.spot.story.domain.enums.StoryCategory;
import com.example.spot.story.domain.enums.StoryCategoryQuery;
import com.example.spot.story.domain.repository.LikedStoryRepository;
import com.example.spot.story.domain.repository.StoryCommentRepository;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class StoryQueryServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private StudyRepository studyRepository;
    @Mock
    private StudyMemberRepository studyMemberRepository;

    @Mock
    private StoryRepository storyRepository;
    @Mock
    private LikedStoryRepository likedStoryRepository;

    @Mock
    private StoryCommentRepository storyCommentRepository;

    @InjectMocks
    private StoryQueryServiceImpl studyPostQueryService;

    private static PageRequest pageRequest;

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

    }

    /*-------------------------------------------------------- 게시글 목록 조회 ------------------------------------------------------------------------*/

    @Test
    @DisplayName("스터디 게시글 목록 조회 - 전체 게시글 조회 (성공)")
    void getAllPosts_All_Success() {

        // given
        Long studyId = 1L;
        Long memberId = 1L;

        getAuthentication(memberId);

        pageRequest = PageRequest.of(0, 10);
        when(storyRepository.findAllByStudyId(studyId, pageRequest))
                .thenReturn(List.of(story1, story2, story3));
        when(storyRepository.findAnnouncementsByStudyId(studyId, pageRequest))
                .thenReturn(List.of(story2));
        when(storyRepository.findAllByStudyIdAndTheme(studyId, StoryCategory.FREE_TALK, pageRequest))
                .thenReturn(List.of(story1, story3));

        // when
        StoryResponseDTO.StoryListDTO result = studyPostQueryService.getAllPosts(pageRequest, studyId, null);

        // then
        assertNotNull(result);
        assertThat(result.getPosts()).isNotEmpty();
        assertThat(result.getPosts()).size().isLessThanOrEqualTo(10);
        assertThat(result.getPosts()).size().isEqualTo(3);
        assertThat(result.getStudyId()).isEqualTo(studyId);
    }

    @Test
    @DisplayName("스터디 게시글 목록 조회 - 테마별 조회 (성공)")
    void getAllPosts_Theme_Success() {

        // given
        Long studyId = 1L;
        Long memberId = 1L;

        getAuthentication(memberId);

        pageRequest = PageRequest.of(0, 10);
        when(storyRepository.findAllByStudyId(studyId, pageRequest))
                .thenReturn(List.of(story1, story2, story3));
        when(storyRepository.findAnnouncementsByStudyId(studyId, pageRequest))
                .thenReturn(List.of(story2));
        when(storyRepository.findAllByStudyIdAndTheme(studyId, StoryCategory.FREE_TALK, pageRequest))
                .thenReturn(List.of(story1, story3));

        // when
        StoryResponseDTO.StoryListDTO result = studyPostQueryService.getAllPosts(pageRequest, studyId,
                StoryCategoryQuery.FREE_TALK);

        // then
        assertNotNull(result);
        assertThat(result.getPosts()).isNotEmpty();
        assertThat(result.getPosts()).size().isLessThanOrEqualTo(10);
        assertThat(result.getPosts()).size().isEqualTo(2);
        assertThat(result.getStudyId()).isEqualTo(studyId);
    }

    @Test
    @DisplayName("스터디 게시글 목록 조회 - 공지 조회 (성공)")
    void getAllPosts_Announcements_Success() {

        // given
        Long studyId = 1L;
        Long memberId = 1L;

        getAuthentication(memberId);

        pageRequest = PageRequest.of(0, 10);
        when(storyRepository.findAllByStudyId(studyId, pageRequest))
                .thenReturn(List.of(story1, story2, story3));
        when(storyRepository.findAnnouncementsByStudyId(studyId, pageRequest))
                .thenReturn(List.of(story2));
        when(storyRepository.findAllByStudyIdAndTheme(studyId, StoryCategory.FREE_TALK, pageRequest))
                .thenReturn(List.of(story1, story3));

        // when
        StoryResponseDTO.StoryListDTO result = studyPostQueryService.getAllPosts(pageRequest, studyId,
                StoryCategoryQuery.ANNOUNCEMENT);

        // then
        assertNotNull(result);
        assertThat(result.getPosts()).isNotEmpty();
        assertThat(result.getPosts()).size().isLessThanOrEqualTo(10);
        assertThat(result.getPosts()).size().isEqualTo(1);
        assertThat(result.getStudyId()).isEqualTo(studyId);
    }

    @Test
    @DisplayName("스터디 게시글 목록 조회 - 스터디 회원이 아닌 경우(실패)")
    void getAllPosts_NotStudyMember_Fail() {

        // given
        Long studyId = 1L;
        Long memberId = 2L;

        getAuthentication(memberId);

        pageRequest = PageRequest.of(0, 10);
        when(storyRepository.findAllByStudyId(studyId, pageRequest))
                .thenReturn(List.of(story1, story2, story3));
        when(storyRepository.findAnnouncementsByStudyId(studyId, pageRequest))
                .thenReturn(List.of(story2));
        when(storyRepository.findAllByStudyIdAndTheme(studyId, StoryCategory.FREE_TALK, pageRequest))
                .thenReturn(List.of(story1, story3));

        // when & then
        assertThrows(StudyHandler.class, () -> studyPostQueryService.getAllPosts(pageRequest, studyId, null));
    }

    @Test
    @DisplayName("스터디 게시글 목록 조회 - 존재하는 카테고리가 아닌 경우(실패)")
    void getAllPosts_NotCategorized_Fail() {

        // given
        Long studyId = 1L;
        Long memberId = 1L;

        getAuthentication(memberId);

        pageRequest = PageRequest.of(0, 10);
        when(storyRepository.findAllByStudyId(studyId, pageRequest))
                .thenReturn(List.of(story1, story2, story3));
        when(storyRepository.findAnnouncementsByStudyId(studyId, pageRequest))
                .thenReturn(List.of(story2));
        when(storyRepository.findAllByStudyIdAndTheme(studyId, StoryCategory.FREE_TALK, pageRequest))
                .thenReturn(List.of(story1, story3));

        // when & then
        assertThrows(IllegalArgumentException.class,
                () -> studyPostQueryService.getAllPosts(pageRequest, studyId, StoryCategoryQuery.valueOf("Nothing")));
    }


    /*-------------------------------------------------------- 게시글 조회 ------------------------------------------------------------------------*/

    @Test
    @DisplayName("스터디 게시글 단건 조회 - 일반 조회 (성공)")
    void getPost_Common_Success() {

        // given
        Long studyId = 1L;
        Long memberId = 1L;
        Long postId = 1L;

        getAuthentication(memberId);

        when(storyRepository.findByIdAndStudyId(postId, studyId))
                .thenReturn(Optional.of(story1));
        when(storyRepository.save(story1)).thenReturn(story1);
        when(memberRepository.save(member1)).thenReturn(member1);
        when(studyRepository.save(study)).thenReturn(study);
        when(storyCommentRepository.findAllByStoryId(postId))
                .thenReturn(List.of(studyPost1Comment1, studyPost1Comment2));
        when(likedStoryRepository.existsByMemberIdAndStoryId(memberId, postId))
                .thenReturn(false);

        // when
        StoryResponseDTO.StoryDetailDTO result = studyPostQueryService.getPost(studyId, postId, false);

        // then
        assertNotNull(result);
        assertThat(result.getPostId()).isEqualTo(1L);
        assertThat(result.getHitNum()).isEqualTo(11);
        assertThat(result.getTitle()).isEqualTo("잡담");
        assertThat(result.getCommentNum()).isEqualTo(2);
        assertThat(result.getIsLiked()).isEqualTo(false);
    }

    @Test
    @DisplayName("스터디 게시글 단건 조회 - 스크랩 혹은 좋아요 후 업데이트 (성공)")
    void getPost_LikeOrScrap_Success() {

        // given
        Long studyId = 1L;
        Long memberId = 1L;
        Long postId = 1L;

        getAuthentication(memberId);

        when(storyRepository.findByIdAndStudyId(postId, studyId))
                .thenReturn(Optional.of(story1));
        when(storyRepository.save(story1)).thenReturn(story1);
        when(memberRepository.save(member1)).thenReturn(member1);
        when(studyRepository.save(study)).thenReturn(study);
        when(storyCommentRepository.findAllByStoryId(postId))
                .thenReturn(List.of(studyPost1Comment1, studyPost1Comment2));
        when(likedStoryRepository.existsByMemberIdAndStoryId(memberId, postId))
                .thenReturn(false);

        // when
        StoryResponseDTO.StoryDetailDTO result = studyPostQueryService.getPost(studyId, postId, true);

        // then
        assertNotNull(result);
        assertThat(result.getPostId()).isEqualTo(1L);
        assertThat(result.getHitNum()).isEqualTo(10);
        assertThat(result.getTitle()).isEqualTo("잡담");
        assertThat(result.getCommentNum()).isEqualTo(2);
        assertThat(result.getIsLiked()).isEqualTo(false);
    }

    @Test
    @DisplayName("스터디 게시글 단건 조회 - 스터디 회원이 아닌 경우 (실패)")
    void getPost_NotStudyMember_Fail() {

        // given
        Long studyId = 1L;
        Long memberId = 2L;
        Long postId = 1L;

        getAuthentication(memberId);

        when(storyRepository.findByIdAndStudyId(postId, studyId))
                .thenReturn(Optional.of(story1));
        when(storyRepository.save(story1)).thenReturn(story1);
        when(memberRepository.save(member1)).thenReturn(member1);
        when(studyRepository.save(study)).thenReturn(study);
        when(storyCommentRepository.findAllByStoryId(postId))
                .thenReturn(List.of(studyPost1Comment1, studyPost1Comment2));
        when(likedStoryRepository.existsByMemberIdAndStoryId(memberId, postId))
                .thenReturn(false);

        // when & then
        assertThrows(StudyHandler.class, () -> studyPostQueryService.getPost(studyId, postId, false));
    }

    @Test
    @DisplayName("스터디 게시글 단건 조회 - 스터디 게시글이 아닌 경우(실패)")
    void getPost_NotStudyPost_Fail() {

        // given
        Long studyId = 1L;
        Long memberId = 2L;
        Long postId = 1L;

        getAuthentication(memberId);

        when(storyRepository.findByIdAndStudyId(postId, studyId))
                .thenReturn(Optional.empty());
        when(storyCommentRepository.findAllByStoryId(postId))
                .thenReturn(List.of());

        // when & then
        assertThrows(StudyHandler.class, () -> studyPostQueryService.getPost(studyId, postId, false));
    }

    /* ------------------------------------------------ 스터디 공지사항 조회  --------------------------------------------------- */

    @Test
    @DisplayName("스터디 공지사항 조회 - 성공")
    void 스터디_공지사항_조회_성공() {

        // given
        long studyId = 1L;
        String title = "공지";
        String content = "공지입니다.";
        Story story = Story.builder()
                .title(title)
                .content(content)
                .storyCategory(StoryCategory.WELCOME)
                .isAnnouncement(true)
                .build();

        StudyMember studyMember = StudyMember.builder()
                .introduction(title).study(study).member(owner).isOwned(true).status(StudyApplicationStatus.APPROVED)
                .build();

        when(storyRepository.findByStudyIdAndIsAnnouncement(studyId, true)).thenReturn(Optional.of(story));
        when(studyMemberRepository.findByMemberIdAndStudyIdAndStatus(1L, studyId,
                StudyApplicationStatus.APPROVED)).thenReturn(
                Optional.ofNullable(studyMember));

        // when
        StoryResponseDTO.StoryContentDTO result = studyPostQueryService.findStudyAnnouncementPost(studyId);

        // then
        assertEquals(title, result.getTitle());
        assertEquals(content, result.getContent());
    }

    @Test
    @DisplayName("스터디 공지사항 조회 - 로그인 한 회원이 해당 스터디 회원이 아닌 경우")
    void 스터디_공지사항_조회_실패_1() {

        // given
        long studyId = 1L;
        when(studyMemberRepository.findByMemberIdAndStudyIdAndStatus(1L, studyId,
                StudyApplicationStatus.APPROVED)).thenReturn(
                Optional.empty());

        // when & then
        assertThrows(GeneralException.class, () -> studyPostQueryService.findStudyAnnouncementPost(studyId));
    }

    @Test
    @DisplayName("스터디 공지사항 조회 - 스터디 공지 글이 없는 경우")
    void 스터디_공지사항_조회_실패_2() {

        // given
        long studyId = 1L;
        StudyMember studyMember = StudyMember.builder()
                .introduction("title").study(study).member(owner).isOwned(true).status(StudyApplicationStatus.APPROVED)
                .build();

        when(studyMemberRepository.findByMemberIdAndStudyIdAndStatus(1L, studyId,
                StudyApplicationStatus.APPROVED)).thenReturn(
                Optional.ofNullable(studyMember));
        when(storyRepository.findByStudyIdAndIsAnnouncement(studyId, true)).thenReturn(Optional.empty());

        // when & then
        assertThrows(GeneralException.class, () -> studyPostQueryService.findStudyAnnouncementPost(studyId));
    }


    /*-------------------------------------------------------- 댓글 목록 조회 ------------------------------------------------------------------------*/

    @Test
    @DisplayName("스터디 게시글 댓글 목록 조회 - (성공)")
    void getAllComments_Success() {

        // given
        Long memberId = 1L;
        Long studyId = 1L;
        Long postId = 1L;

        getAuthentication(memberId);

        when(storyRepository.findByIdAndStudyId(postId, studyId))
                .thenReturn(Optional.of(story1));
        when(storyCommentRepository.findAllByStoryId(postId))
                .thenReturn(List.of(studyPost1Comment1, studyPost1Comment2));

        // when
        StoryCommentResponseDTO.ReplyListDTO result = studyPostQueryService.getAllComments(studyId, postId);

        // then
        assertNotNull(result);
        assertThat(result.getPostId()).isEqualTo(1L);
        assertThat(result.getComments()).size().isEqualTo(1);
        result.getComments()
                .forEach(comment -> {
                    assertThat(comment.getCommentId()).isEqualTo(1L);       // 댓글 1개
                    assertThat(comment.getApplies()).size().isEqualTo(1L);  // 답글 1개
                });
    }

    @Test
    @DisplayName("스터디 게시글 댓글 목록 조회 - 스터디 회원이 아닌 경우(실패)")
    void getAllComments_NotStudyMember_Fail() {

        // given
        Long memberId = 2L;
        Long studyId = 1L;
        Long postId = 1L;

        getAuthentication(memberId);

        when(storyRepository.findByIdAndStudyId(postId, studyId))
                .thenReturn(Optional.of(story1));
        when(storyCommentRepository.findAllByStoryId(postId))
                .thenReturn(List.of(studyPost1Comment1, studyPost1Comment2));

        // when & then
        assertThrows(StudyHandler.class, () -> studyPostQueryService.getAllComments(studyId, postId));
    }

    @Test
    @DisplayName("스터디 게시글 댓글 목록 조회 - 스터디 게시글이 아닌 경우(실패)")
    void getAllComments_NotStudyPost_Fail() {

        // given
        Long memberId = 1L;
        Long studyId = 1L;
        Long postId = 1L;

        getAuthentication(memberId);

        when(storyRepository.findByIdAndStudyId(postId, studyId))
                .thenReturn(Optional.empty());
        when(storyCommentRepository.findAllByStoryId(postId))
                .thenReturn(List.of());

        // when & then
        assertThrows(StudyHandler.class, () -> studyPostQueryService.getAllComments(studyId, postId));
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
                .build();
        member2 = Member.builder()
                .id(2L)
                .build();
        owner = Member.builder()
                .id(3L)
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
                .isDeleted(false)
                .parentComment(null)
                .build();
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

        story1.addComment(studyPost1Comment1);
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