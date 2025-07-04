package com.example.spot.service.todo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import com.example.spot.common.api.exception.GeneralException;
import com.example.spot.member.domain.Member;
import com.example.spot.study.domain.Study;
import com.example.spot.study.domain.association.StudyMember;
import com.example.spot.study.domain.enums.StudyApplicationStatus;
import com.example.spot.study.domain.repository.StudyMemberRepository;
import com.example.spot.todo.application.impl.GetToDoUseCaseImpl;
import com.example.spot.todo.domain.ToDo;
import com.example.spot.todo.domain.ToDoRepository;
import com.example.spot.todo.presentation.dto.response.ToDoListResponseDTO.ToDoListSearchResponseDTO;
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
public class ToDoQueryServiceTest {

    @InjectMocks
    private GetToDoUseCaseImpl getToDoUseCase;

    @Mock
    private StudyMemberRepository studyMemberRepository;
    @Mock
    private ToDoRepository toDoRepository;

    private static Member member;
    private static Member member2;
    private static Study study;
    private static StudyMember studyMember;
    private static StudyMember studyMember2;
    private static StudyMember apply;
    private static ToDo toDo;

    @BeforeEach
    void setup() {
        member = Member.builder()
                .id(1L)
                .build();
        member2 = Member.builder()
                .id(2L)
                .build();

        study = Study.builder()
                .build();

        studyMember = StudyMember.builder()
                .introduction("title").study(study).member(member).isOwned(true).status(StudyApplicationStatus.APPROVED)
                .build();

        apply = StudyMember.builder()
                .introduction("title").study(study).member(member).isOwned(false).status(StudyApplicationStatus.APPLIED)
                .build();
        studyMember2 = StudyMember.builder()
                .introduction("title").study(study).member(member2).isOwned(true)
                .status(StudyApplicationStatus.APPROVED).build();
        toDo = ToDo.builder()
                .id(1L)
                .build();

        Long studyId = 1L;

        when(studyMemberRepository.findByMemberIdAndStudyIdAndStatus(1L, studyId,
                StudyApplicationStatus.APPROVED)).thenReturn(
                Optional.ofNullable(studyMember));
        Authentication authentication = new UsernamePasswordAuthenticationToken("1", null, Collections.emptyList());
        // SecurityContext 생성 및 설정
        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

    /* ------------------------------------------------ To-Do 조회  --------------------------------------------------- */

    @Test
    @DisplayName("To-Do 조회 - 성공")
    void ToDo_조회_성공() {
        // given
        when(studyMemberRepository.findByMemberIdAndStudyIdAndStatus(1L, 100L, StudyApplicationStatus.APPROVED))
                .thenReturn(Optional.ofNullable(studyMember));
        when(toDoRepository.findByStudyIdAndMemberIdAndDateOrderByCreatedAtDesc(anyLong(), anyLong(), any(), any()))
                .thenReturn(List.of(toDo));
        when(toDoRepository.countByStudyIdAndMemberIdAndDate(anyLong(), anyLong(), any()))
                .thenReturn(1L);

        // when
        ToDoListSearchResponseDTO responseDTO = getToDoUseCase.getToDoList(1L, LocalDate.MAX, PageRequest.of(0, 10));

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
        assertThrows(GeneralException.class,
                () -> getToDoUseCase.getToDoList(100L, LocalDate.MAX, PageRequest.of(0, 10)));
    }

    @Test
    @DisplayName("To-Do 조회 - 회원의 To-Do가 존재하지 않는 경우")
    void ToDo가_존재하지_않는_경우() {
        // given
        when(studyMemberRepository.findByMemberIdAndStudyIdAndStatus(1L, 100L, StudyApplicationStatus.APPROVED))
                .thenReturn(Optional.ofNullable(studyMember));
        when(toDoRepository.findByStudyIdAndMemberIdAndDateOrderByCreatedAtDesc(anyLong(), anyLong(), any(), any()))
                .thenReturn(List.of());

        // when & then
        assertThrows(GeneralException.class,
                () -> getToDoUseCase.getToDoList(100L, LocalDate.MAX, PageRequest.of(0, 10)));
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
        when(toDoRepository.findByStudyIdAndMemberIdAndDateOrderByCreatedAtDesc(anyLong(), anyLong(), any(), any()))
                .thenReturn(List.of(toDo));
        when(toDoRepository.countByStudyIdAndMemberIdAndDate(anyLong(), anyLong(), any()))
                .thenReturn(1L);

        // when
        ToDoListSearchResponseDTO responseDTO = getToDoUseCase.getMemberToDoList(1L, 2L, LocalDate.MAX,
                PageRequest.of(0, 10));

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
        assertThrows(GeneralException.class,
                () -> getToDoUseCase.getMemberToDoList(100L, 2L, LocalDate.MAX, PageRequest.of(0, 10)));
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
        assertThrows(GeneralException.class,
                () -> getToDoUseCase.getMemberToDoList(100L, 2L, LocalDate.MAX, PageRequest.of(0, 10)));
    }

    @Test
    @DisplayName("특정 스터디 원 To-Do 조회 - 회원의 To-Do가 존재하지 않는 경우")
    void 스터디_원_ToDo가_존재하지_않는_경우() {
        // given
        when(studyMemberRepository.findByMemberIdAndStudyIdAndStatus(1L, 100L, StudyApplicationStatus.APPROVED))
                .thenReturn(Optional.ofNullable(studyMember));
        when(toDoRepository.findByStudyIdAndMemberIdAndDateOrderByCreatedAtDesc(anyLong(), anyLong(), any(), any()))
                .thenReturn(List.of());

        // when & then
        assertThrows(GeneralException.class,
                () -> getToDoUseCase.getToDoList(100L, LocalDate.MAX, PageRequest.of(0, 10)));
    }

}
