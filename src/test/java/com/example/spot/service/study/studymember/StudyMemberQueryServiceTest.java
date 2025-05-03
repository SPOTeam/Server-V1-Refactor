package com.example.spot.service.study.studymember;

import com.example.spot.refactor.common.api.exception.GeneralException;
import com.example.spot.refactor.member.domain.Member;
import com.example.spot.refactor.study.domain.aggregate.StudyMember;
import com.example.spot.refactor.schedule.domain.StudySchedule;
import com.example.spot.refactor.todo.domain.StudyToDo;
import com.example.spot.refactor.study.domain.enums.StudyApplicationStatus;
import com.example.spot.refactor.story.domain.enums.StudyPostCategory;
import com.example.spot.refactor.study.domain.Study;
import com.example.spot.refactor.story.domain.StudyPost;
import com.example.spot.refactor.member.domain.MemberRepository;
import com.example.spot.refactor.study.domain.repository.StudyMemberRepository;
import com.example.spot.refactor.schedule.domain.repository.StudyScheduleRepository;
import com.example.spot.refactor.story.domain.StudyPostRepository;
import com.example.spot.refactor.todo.domain.StudyToDoRepository;
import com.example.spot.refactor.common.security.utils.SecurityUtils;
import com.example.spot.refactor.study.application.MemberStudyQueryServiceImpl;
import com.example.spot.refactor.study.presentation.dto.response.ToDoListResponseDTO.ToDoListSearchResponseDTO;
import com.example.spot.refactor.study.presentation.dto.response.StudyMemberResponseDTO;
import com.example.spot.refactor.study.presentation.dto.response.StudyMemberResponseDTO.StudyApplicantDTO;
import com.example.spot.refactor.study.presentation.dto.response.StudyMemberResponseDTO.StudyApplyMemberDTO;
import com.example.spot.refactor.study.presentation.dto.response.StudyPostResponseDTO;
import com.example.spot.refactor.study.presentation.dto.response.StudyScheduleResponseDTO;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class StudyMemberQueryServiceTest {

    @InjectMocks
    private MemberStudyQueryServiceImpl memberStudyQueryService;

    @Mock
    private StudyMemberRepository studyMemberRepository;
    @Mock
    private MemberRepository memberRepository;
    @Mock
    private StudyPostRepository studyPostRepository;
    @Mock
    private StudyScheduleRepository studyScheduleRepository;
    @Mock
    private StudyToDoRepository studyToDoRepository;
    @Mock
    private SecurityUtils securityUtils;

    private static Member member;
    private static Member member2;
    private static Study study;
    private static StudyMember studyMember;
    private static StudyMember studyMember2;
    private static StudyMember apply;
    private static StudyToDo studyToDo;
    
    @BeforeEach
    void setup(){
        member = Member.builder()
                .id(1L)
                .build();
        member2 = Member.builder()
                .id(2L)
                .build();


        study = Study.builder()
                .build();

        studyMember = StudyMember.builder()
                .introduction("title").study(study).member(member).isOwned(true).status(StudyApplicationStatus.APPROVED).build();

        apply = StudyMember.builder()
                .introduction("title").study(study).member(member).isOwned(false).status(StudyApplicationStatus.APPLIED).build();
        studyMember2 = StudyMember.builder()
                .introduction("title").study(study).member(member2).isOwned(true).status(StudyApplicationStatus.APPROVED).build();
        studyToDo = StudyToDo.builder()
                .id(1L)
                .build();

        Long studyId = 1L;

        when(studyMemberRepository.findByMemberIdAndStudyIdAndStatus(1L, studyId, StudyApplicationStatus.APPROVED)).thenReturn(
                Optional.ofNullable(studyMember));
        Authentication authentication = new UsernamePasswordAuthenticationToken("1", null, Collections.emptyList());
        // SecurityContext 생성 및 설정
        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

    /* ------------------------------------------------ 스터디 공지사항 조회  --------------------------------------------------- */

    @Test
    @DisplayName("스터디 공지사항 조회 - 성공")
    void 스터디_공지사항_조회_성공(){

        // given
        long studyId = 1L;
        String title = "공지";
        String content = "공지입니다.";
        StudyPost studyPost = StudyPost.builder()
                .title(title)
                .content(content)
                .studyPostCategory(StudyPostCategory.WELCOME)
                .isAnnouncement(true)
                .build();

        StudyMember studyMember = StudyMember.builder()
                        .introduction(title).study(study).member(member).isOwned(true).status(StudyApplicationStatus.APPROVED).build();

        when(studyPostRepository.findByStudyIdAndIsAnnouncement(studyId, true)).thenReturn(Optional.of(studyPost));
        when(studyMemberRepository.findByMemberIdAndStudyIdAndStatus(1L, studyId, StudyApplicationStatus.APPROVED)).thenReturn(
                Optional.ofNullable(studyMember));

        // when
        StudyPostResponseDTO responseDTO = memberStudyQueryService.findStudyAnnouncementPost(studyId);

        // then
        assertEquals(title,responseDTO.getTitle());
        assertEquals(content, responseDTO.getContent());
    }

    @Test
    @DisplayName("스터디 공지사항 조회 - 로그인 한 회원이 해당 스터디 회원이 아닌 경우")
    void 스터디_공지사항_조회_실패_1(){

        // given
        long studyId = 1L;
        when(studyMemberRepository.findByMemberIdAndStudyIdAndStatus(1L, studyId, StudyApplicationStatus.APPROVED)).thenReturn(
                Optional.empty());

        // when & then
        assertThrows(GeneralException.class, () -> memberStudyQueryService.findStudyAnnouncementPost(studyId));
    }

    @Test
    @DisplayName("스터디 공지사항 조회 - 스터디 공지 글이 없는 경우")
    void 스터디_공지사항_조회_실패_2(){

        // given
        long studyId = 1L;
        StudyMember studyMember = StudyMember.builder()
                .introduction("title").study(study).member(member).isOwned(true).status(StudyApplicationStatus.APPROVED).build();


        when(studyMemberRepository.findByMemberIdAndStudyIdAndStatus(1L, studyId, StudyApplicationStatus.APPROVED)).thenReturn(
                Optional.ofNullable(studyMember));
        when(studyPostRepository.findByStudyIdAndIsAnnouncement(studyId, true)).thenReturn(Optional.empty());

        // when & then
        assertThrows(GeneralException.class, () -> memberStudyQueryService.findStudyAnnouncementPost(studyId));
    }

    /* ------------------------------------------------ 스터디 모임 목록 조회  --------------------------------------------------- */

    @Test
    @DisplayName("스터디 모임 목록 조회 - 성공")
    void 스터디_모임_목록_조회_성공(){
        // given
        Long studyId = 1L;
        String title = "title";

        StudySchedule studySchedule1 = StudySchedule.builder().id(1L).title(title).build();
        StudySchedule studySchedule2 = StudySchedule.builder().id(2L).title("title1").build();
        when(studyScheduleRepository.findAllByStudyId(studyId, Pageable.unpaged())).thenReturn(List.of(studySchedule1, studySchedule2));

        // when
        StudyScheduleResponseDTO responseDTO = memberStudyQueryService.findStudySchedule(studyId, Pageable.unpaged());

        // then
        assertEquals(2, responseDTO.getTotalElements());
        assertEquals(title, responseDTO.getSchedules().get(0).getTitle());
    }

    @Test
    @DisplayName("스터디 모임 목록 조회 - 로그인 한 회원이 해당 스터디 회원이 아닌 경우")
    void 스터디_모임_목록_조회_실패_1(){
        // given
        long studyId = 1L;
        when(studyMemberRepository.findByMemberIdAndStudyIdAndStatus(1L, studyId, StudyApplicationStatus.APPROVED)).thenReturn(
                Optional.empty());

        // when & then
        assertThrows(GeneralException.class, () -> memberStudyQueryService.findStudySchedule(studyId, Pageable.unpaged()));
    }

    @Test
    @DisplayName("스터디 모임 목록 조회 - 스터디 모임 일정이 존재하지 않는 경우")
    void 스터디_모임_목록_조회_실패_2(){
        // given
        long studyId = 1L;
        when(studyMemberRepository.findByMemberIdAndStudyIdAndStatus(1L, studyId, StudyApplicationStatus.APPROVED)).thenReturn(
                Optional.of(studyMember));
        when(studyScheduleRepository.findAllByStudyId(studyId, Pageable.unpaged())).thenReturn(Collections.emptyList());

        // when & then
        assertThrows(GeneralException.class, () -> memberStudyQueryService.findStudySchedule(studyId, Pageable.unpaged()));
    }


    /* ------------------------------------------------ 특정 스터디 회원 목록 전체 조회  --------------------------------------------------- */

    @Test
    @DisplayName("특정 스터디 회원 목록 조회 - 성공")
    void 특정_스터디_회원_목록_조회_성공() {

        // given
        Long studyId = 1L;
        when(studyMemberRepository.findAllByStudyIdAndStatus(studyId, StudyApplicationStatus.APPROVED)).thenReturn(List.of(studyMember));

        // when
        StudyMemberResponseDTO responseDTO = memberStudyQueryService.findStudyMembers(studyId);

        // then
        assertEquals(1, responseDTO.getTotalElements());
        assertEquals(1L, responseDTO.getMembers().get(0).getMemberId());

    }

    @Test
    @DisplayName("특정 스터디 회원 목록 조회 - 스터디에 멤버가 존재하지 않는 경우")
    void 특정_스터디_회원_목록_조회_실패() {

        // given
        Long studyId = 1L;
        when(studyMemberRepository.findAllByStudyIdAndStatus(studyId, StudyApplicationStatus.APPROVED)).thenReturn(List.of());

        // when & then
        assertThrows(GeneralException.class, () -> memberStudyQueryService.findStudyMembers(studyId));
    }


    /* ------------------------------------------------ 모집중인 스터디에 신청한 회원 목록 조회  --------------------------------------------------- */

    @Test
    @DisplayName("모집중인 스터디에 신청한 회원 목록 조회 - 성공")
    void 스터디_신청_회원_목록_조회_성공() {

        // given
        when(studyMemberRepository.findByMemberIdAndStudyIdAndIsOwned(1L, 1L, true)).thenReturn(
                Optional.ofNullable(studyMember));
        when(studyMemberRepository.findAllByStudyIdAndStatus(1L, StudyApplicationStatus.APPLIED))
                .thenReturn(List.of(apply));

        // when
        StudyMemberResponseDTO responseDTO = memberStudyQueryService.findStudyApplicants(1L);

        // then
        assertEquals(1, responseDTO.getTotalElements());
        assertEquals(1L, responseDTO.getMembers().get(0).getMemberId());
    }

    @Test
    @DisplayName("모집중인 스터디에 신청한 회원 목록 조회 - 로그인 한 회원이 해당 스터디 장이 아닌 경우")
    void 스터디_신청_회원_목록_조회_실패_1() {

        // given
        when(studyMemberRepository.findByMemberIdAndStudyIdAndIsOwned(1L, 1L, true)).thenReturn(
                Optional.empty());

        // when & then
        assertThrows(GeneralException.class, () -> memberStudyQueryService.findStudyApplicants(1L));
    }

    @Test
    @DisplayName("모집중인 스터디에 신청한 회원 목록 조회 - 스터디 신청자가 존재하지 않는 경우")
    void 스터디_신청_회원_목록_조회_실패_2() {

        // given
        when(studyMemberRepository.findByMemberIdAndStudyIdAndIsOwned(1L, 1L, true)).thenReturn(
                Optional.ofNullable(studyMember));
        when(studyMemberRepository.findAllByStudyIdAndStatus(1L, StudyApplicationStatus.APPLIED))
                .thenReturn(List.of());

        // when & then
        assertThrows(GeneralException.class, () -> memberStudyQueryService.findStudyApplicants(1L));
    }


    /* ------------------------------------------------ 스터디 신청자 정보 조회  --------------------------------------------------- */

    @Test
    @DisplayName("스터디 신청자 정보 조회 - 성공")
    void 스터디_신청자_정보_조회_성공() {
        // given
        when(studyMemberRepository.findByMemberIdAndStudyIdAndIsOwned(1L, 100L, true)).thenReturn(
                Optional.ofNullable(studyMember));
        when(studyMemberRepository.findByMemberIdAndStudyIdAndStatus(1L, 100L, StudyApplicationStatus.APPLIED))
                .thenReturn(Optional.ofNullable(apply));

        // when
        StudyApplyMemberDTO responseDTO = memberStudyQueryService.findStudyApplication(100L, 1L);

        // then
        assertEquals(1L, responseDTO.getMemberId());
    }

    @Test
    @DisplayName("스터디 신청자 정보 조회 - 로그인 한 회원이 스터디 장이 아닌 경우")
    void 스터디_신청자_정보_조회_실패_1() {
        // given
        when(studyMemberRepository.findByMemberIdAndStudyIdAndIsOwned(1L, 100L, true)).thenReturn(
                Optional.empty());

        // when & then
        assertThrows(GeneralException.class, () -> memberStudyQueryService.findStudyApplication(100L, 1L));
    }

    @Test
    @DisplayName("스터디 신청자 정보 조회 - 스터디 신청자가 없는 경우 ")
    void 스터디_신청자_정보_조회_실패_2() {
        // given
        when(studyMemberRepository.findByMemberIdAndStudyIdAndIsOwned(1L, 100L, true)).thenReturn(
                Optional.ofNullable(studyMember));

        when(studyMemberRepository.findByMemberIdAndStudyIdAndStatus(1L, 100L, StudyApplicationStatus.APPLIED))
                .thenReturn(Optional.empty());

        // when & then
        assertThrows(GeneralException.class, () -> memberStudyQueryService.findStudyApplication(100L, 1L));
    }

    @Test
    @DisplayName("스터디 신청자 정보 조회 - 스터디 신청자가 스터디 장인 경우")
    void 스터디_신청자_정보_조회_실패_3() {
        // given
        when(studyMemberRepository.findByMemberIdAndStudyIdAndIsOwned(1L, 100L, true)).thenReturn(
                Optional.ofNullable(studyMember));

        when(studyMemberRepository.findByMemberIdAndStudyIdAndStatus(1L, 100L, StudyApplicationStatus.APPLIED))
                .thenReturn(Optional.ofNullable(studyMember));

        // when & then
        assertThrows(GeneralException.class, () -> memberStudyQueryService.findStudyApplication(100L, 1L));
    }


    /* ------------------------------------------------ 스터디 신청 여부 조회  --------------------------------------------------- */

    @Test
    @DisplayName("스터디 신청 여부 조회 - 성공")
    void 스터디_신청_여부_조회_성공() {
        // given
        when(studyMemberRepository.findByMemberIdAndStudyIdAndStatus(1L, 100L, StudyApplicationStatus.APPROVED))
                .thenReturn(Optional.empty());
        when(studyMemberRepository.existsByMemberIdAndStudyIdAndStatus(1L, 100L, StudyApplicationStatus.APPLIED))
                .thenReturn(true);

        // when
        StudyApplicantDTO responseDTO = memberStudyQueryService.isApplied(100L);

        // then
        assertEquals(100L, responseDTO.getStudyId());
        assertEquals(true, responseDTO.isApplied());
    }

    @Test
    @DisplayName("스터디 신청 여부 조회 - 이미 가입 된 경우")
    void 스터디_신청_여부_조회_실패() {
        // given
        when(studyMemberRepository.findByMemberIdAndStudyIdAndStatus(1L, 100L, StudyApplicationStatus.APPROVED))
                .thenReturn(Optional.ofNullable(studyMember));
        // when & then
        assertThrows(GeneralException.class, () -> memberStudyQueryService.isApplied(100L));
    }

    /* ------------------------------------------------ To-Do 조회  --------------------------------------------------- */

    @Test
    @DisplayName("To-Do 조회 - 성공")
    void ToDo_조회_성공() {
        // given
        when(studyMemberRepository.findByMemberIdAndStudyIdAndStatus(1L, 100L, StudyApplicationStatus.APPROVED))
                .thenReturn(Optional.ofNullable(studyMember));
        when(studyToDoRepository.findByStudyIdAndMemberIdAndDateOrderByCreatedAtDesc(anyLong(), anyLong(), any(), any()))
                .thenReturn(List.of(studyToDo));
        when(studyToDoRepository.countByStudyIdAndMemberIdAndDate(anyLong(), anyLong(), any()))
                .thenReturn(1L);

        // when
        ToDoListSearchResponseDTO responseDTO = memberStudyQueryService.getToDoList(1L, LocalDate.MAX, PageRequest.of(0, 10));

        // then
        assertEquals(1, responseDTO.getTotalElements());
        assertEquals(1L, responseDTO.getContent().get(0).getId());
    }

    @Test
    @DisplayName("To-Do 조회 - 로그인 한 회원이 스터디 회원이 아닌 경우")
    void ToDo_조회_시_로그인_한_회원이_스터디_회원이_아닌_경우() {
        // given
        when(studyMemberRepository.findByMemberIdAndStudyIdAndStatus(1L, 100L, StudyApplicationStatus.APPROVED))
                .thenReturn(Optional.empty());

        // when & then
        assertThrows(GeneralException.class, () -> memberStudyQueryService.getToDoList(100L, LocalDate.MAX, PageRequest.of(0, 10)));
    }

    @Test
    @DisplayName("To-Do 조회 - 회원의 To-Do가 존재하지 않는 경우")
    void ToDo가_존재하지_않는_경우() {
        // given
        when(studyMemberRepository.findByMemberIdAndStudyIdAndStatus(1L, 100L, StudyApplicationStatus.APPROVED))
                .thenReturn(Optional.ofNullable(studyMember));
        when(studyToDoRepository.findByStudyIdAndMemberIdAndDateOrderByCreatedAtDesc(anyLong(), anyLong(), any(), any()))
                .thenReturn(List.of());

        // when & then
        assertThrows(GeneralException.class, () -> memberStudyQueryService.getToDoList(100L, LocalDate.MAX, PageRequest.of(0, 10)));
    }

    /* ------------------------------------------------ 다른 스터디원의 To-Do 조회  --------------------------------------------------- */

    @Test
    @DisplayName("특정 스터디 원 To-Do 조회 - 성공")
    void 스터디_원_ToDo_조회_성공() {
        // given
        when(studyMemberRepository.findByMemberIdAndStudyIdAndStatus(1L, 1L, StudyApplicationStatus.APPROVED))
                .thenReturn(Optional.ofNullable(studyMember));
        when(studyMemberRepository.findByMemberIdAndStudyIdAndStatus(2L, 1L, StudyApplicationStatus.APPROVED))
                .thenReturn(Optional.ofNullable(studyMember));
        when(studyToDoRepository.findByStudyIdAndMemberIdAndDateOrderByCreatedAtDesc(anyLong(), anyLong(), any(), any()))
                .thenReturn(List.of(studyToDo));
        when(studyToDoRepository.countByStudyIdAndMemberIdAndDate(anyLong(), anyLong(), any()))
                .thenReturn(1L);

        // when
        ToDoListSearchResponseDTO responseDTO = memberStudyQueryService.getMemberToDoList(1L, 2L, LocalDate.MAX, PageRequest.of(0, 10));

        // then
        assertEquals(1, responseDTO.getTotalElements());
        assertEquals(1L, responseDTO.getContent().get(0).getId());
    }

    @Test
    @DisplayName("특정 스터디 원 To-Do 조회 - 로그인 한 회원이 스터디 회원이 아닌 경우")
    void 스터디_원_ToDo_조회_시_로그인_한_회원이_스터디_회원이_아닌_경우() {
        // given
        when(studyMemberRepository.findByMemberIdAndStudyIdAndStatus(1L, 100L, StudyApplicationStatus.APPROVED))
                .thenReturn(Optional.empty());

        // when & then
        assertThrows(GeneralException.class, () -> memberStudyQueryService.getMemberToDoList(100L, 2L, LocalDate.MAX, PageRequest.of(0, 10)));
    }

    @Test
    @DisplayName("특정 스터디 원 To-Do 조회 - 조회 하려는 회원이 스터디 회원이 아닌 경우")
    void 스터디_원_ToDo_조회_시_조회_하려는_회원이_스터디_회원이_아닌_경우() {
        // given
        when(studyMemberRepository.findByMemberIdAndStudyIdAndStatus(1L, 100L, StudyApplicationStatus.APPROVED))
                .thenReturn(Optional.ofNullable(studyMember));
        when(studyMemberRepository.findByMemberIdAndStudyIdAndStatus(2L, 100L, StudyApplicationStatus.APPROVED))
                .thenReturn(Optional.empty());

        // when & then
        assertThrows(GeneralException.class, () -> memberStudyQueryService.getMemberToDoList(100L, 2L, LocalDate.MAX, PageRequest.of(0, 10)));
    }

    @Test
    @DisplayName("특정 스터디 원 To-Do 조회 - 회원의 To-Do가 존재하지 않는 경우")
    void 스터디_원_ToDo가_존재하지_않는_경우() {
        // given
        when(studyMemberRepository.findByMemberIdAndStudyIdAndStatus(1L, 100L, StudyApplicationStatus.APPROVED))
                .thenReturn(Optional.ofNullable(studyMember));
        when(studyToDoRepository.findByStudyIdAndMemberIdAndDateOrderByCreatedAtDesc(anyLong(), anyLong(), any(), any()))
                .thenReturn(List.of());

        // when & then
        assertThrows(GeneralException.class, () -> memberStudyQueryService.getToDoList(100L, LocalDate.MAX, PageRequest.of(0, 10)));
    }

}
