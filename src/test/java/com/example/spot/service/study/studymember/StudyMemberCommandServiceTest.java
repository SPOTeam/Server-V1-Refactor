package com.example.spot.service.study.studymember;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.when;

import com.example.spot.refactor.common.api.exception.handler.StudyHandler;
import com.example.spot.refactor.member.domain.Member;
import com.example.spot.refactor.study.domain.aggregate.studymember.StudyMember;
import com.example.spot.refactor.study.domain.aggregate.studytodo.StudyToDo;
import com.example.spot.refactor.study.domain.enums.StudyApplicationStatus;
import com.example.spot.refactor.member.domain.enums.Status;
import com.example.spot.refactor.study.domain.aggregate.Study;
import com.example.spot.refactor.member.domain.MemberRepository;
import com.example.spot.refactor.study.domain.aggregate.studymember.StudyMemberRepository;
import com.example.spot.refactor.study.domain.repository.StudyRepository;
import com.example.spot.refactor.study.domain.aggregate.studytodo.StudyToDoRepository;
import com.example.spot.refactor.study.application.MemberStudyCommandServiceImpl;
import com.example.spot.refactor.study.presentation.dto.request.ToDoListRequestDTO.ToDoListCreateDTO;
import com.example.spot.refactor.study.presentation.dto.response.ToDoListResponseDTO.ToDoListCreateResponseDTO;
import com.example.spot.refactor.study.presentation.dto.response.ToDoListResponseDTO.ToDoListUpdateResponseDTO;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Optional;

import com.example.spot.refactor.study.presentation.dto.response.StudyTerminationResponseDTO;
import com.example.spot.refactor.study.presentation.dto.response.StudyWithdrawalResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class StudyMemberCommandServiceTest {

    @InjectMocks
    private MemberStudyCommandServiceImpl memberStudyCommandService;

    @Mock
    private StudyRepository studyRepository;

    @Mock
    private StudyMemberRepository studyMemberRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private StudyToDoRepository studyToDoRepository;

    @Mock
    private Study study;

    @Mock
    private Member member;

    @Mock
    private StudyMember studyMember;

    @Mock
    private StudyToDo studyToDo;

    private ToDoListCreateDTO requestDTO;

    @BeforeEach
    void init() {
        requestDTO  = ToDoListCreateDTO.builder()
                .content("test")
                .date(LocalDate.EPOCH)
                .build();

        given(studyToDo.getStudy()).willReturn(study);
        given(study.getId()).willReturn(1L);
        given(studyToDo.getMember()).willReturn(member);
        given(member.getId()).willReturn(1L);

        Authentication authentication = new UsernamePasswordAuthenticationToken("1", null, Collections.emptyList());
        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

    /* ---------------------------- 진행중인 스터디 관련 메서드  ---------------------------- */

    @Test
    @DisplayName("스터디 탈퇴 - (성공)")
    void withdrawFromStudy_Success() {

        // given
        member = Member.builder()
                .id(1L)
                .name("회원1")
                .build();
        study = Study.builder()
                .id(1L)
                .title("스터디")
                .build();
        studyMember = StudyMember.builder()
                .member(member)
                .study(study)
                .isOwned(false)
                .status(StudyApplicationStatus.APPROVED)
                .build();

        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
        when(studyRepository.findById(1L)).thenReturn(Optional.of(study));
        when(studyMemberRepository.findByMemberIdAndStudyIdAndStatus(1L, 1L, StudyApplicationStatus.APPROVED))
                .thenReturn(Optional.of(studyMember));

        // when
        StudyWithdrawalResponseDTO.WithdrawalDTO result = memberStudyCommandService.withdrawFromStudy(1L);

        // then
        assertNotNull(result);
        assertThat(result.getStudyId()).isEqualTo(1L);
        assertThat(result.getStudyName()).isEqualTo("스터디");
        assertThat(result.getMemberId()).isEqualTo(1L);
        assertThat(result.getMemberName()).isEqualTo("회원1");
    }

    @Test
    @DisplayName("스터디 탈퇴 - 스터디 회원이 아닌 경우 (실패)")
    void withdrawFromStudy_NotStudyMember_Fail() {

        // given
        member = Member.builder()
                .id(1L)
                .name("회원1")
                .build();
        study = Study.builder()
                .id(1L)
                .title("스터디")
                .build();
        studyMember = StudyMember.builder()
                .member(member)
                .study(study)
                .isOwned(false)
                .status(StudyApplicationStatus.APPLIED)
                .build();

        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
        when(studyRepository.findById(1L)).thenReturn(Optional.of(study));
        when(studyMemberRepository.findByMemberIdAndStudyIdAndStatus(1L, 1L, StudyApplicationStatus.APPROVED))
                .thenReturn(Optional.empty());

        // when & then
        assertThrows(StudyHandler.class, () -> memberStudyCommandService.withdrawFromStudy(1L));
    }

    @Test
    @DisplayName("스터디 탈퇴 - 스터디장인 경우 (실패)")
    void withdrawFromStudy_StudyOwner_Fail() {

        // given
        member = Member.builder()
                .id(1L)
                .name("회원1")
                .build();
        study = Study.builder()
                .id(1L)
                .title("스터디")
                .build();
        studyMember = StudyMember.builder()
                .member(member)
                .study(study)
                .isOwned(true)
                .status(StudyApplicationStatus.APPROVED)
                .build();

        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
        when(studyRepository.findById(1L)).thenReturn(Optional.of(study));
        when(studyMemberRepository.findByMemberIdAndStudyIdAndStatus(1L, 1L, StudyApplicationStatus.APPROVED))
                .thenReturn(Optional.of(studyMember));

        // when & then
        assertThrows(StudyHandler.class, () -> memberStudyCommandService.withdrawFromStudy(1L));
    }

    @Test
    @DisplayName("스터디 종료 - (성공)")
    void terminateStudy_Success() {

        // given
        member = Member.builder()
                .id(1L)
                .name("회원1")
                .build();
        study = Study.builder()
                .id(1L)
                .title("스터디")
                .status(Status.ON)
                .build();
        studyMember = StudyMember.builder()
                .member(member)
                .study(study)
                .isOwned(true)
                .status(StudyApplicationStatus.APPROVED)
                .build();

        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
        when(studyRepository.findById(1L)).thenReturn(Optional.of(study));
        when(studyMemberRepository.findByMemberIdAndStudyIdAndStatus(1L, 1L, StudyApplicationStatus.APPROVED))
                .thenReturn(Optional.of(studyMember));

        // when
        StudyTerminationResponseDTO.TerminationDTO result = memberStudyCommandService.terminateStudy(1L, "스터디 성과");

        // then
        assertNotNull(result);
        assertThat(result.getStudyId()).isEqualTo(1L);
        assertThat(result.getStudyName()).isEqualTo("스터디");
        assertThat(result.getStatus()).isEqualTo(Status.OFF);
    }

    @Test
    @DisplayName("스터디 종료 - 스터디장이 아닌 경우 (실패)")
    void terminateStudy_NotStudyOwner_Fail() {

        // given
        member = Member.builder()
                .id(1L)
                .name("회원1")
                .build();
        study = Study.builder()
                .id(1L)
                .title("스터디")
                .status(Status.ON)
                .build();
        studyMember = StudyMember.builder()
                .member(member)
                .study(study)
                .isOwned(false)
                .status(StudyApplicationStatus.APPROVED)
                .build();

        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
        when(studyRepository.findById(1L)).thenReturn(Optional.of(study));
        when(studyMemberRepository.findByMemberIdAndStudyIdAndStatus(1L, 1L, StudyApplicationStatus.APPROVED))
                .thenReturn(Optional.of(studyMember));

        // when & then
        assertThrows(StudyHandler.class, () -> memberStudyCommandService.terminateStudy(1L, "스터디 성과"));
    }

    @Test
    @DisplayName("스터디 종료 - 진행중인 스터디가 아닌 경우 (실패)")
    void terminateStudy_AlreadyTerminated_Fail() {

        // given
        member = Member.builder()
                .id(1L)
                .name("회원1")
                .build();
        study = Study.builder()
                .id(1L)
                .title("스터디")
                .status(Status.OFF)
                .build();
        studyMember = StudyMember.builder()
                .member(member)
                .study(study)
                .isOwned(false)
                .status(StudyApplicationStatus.APPROVED)
                .build();

        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
        when(studyRepository.findById(1L)).thenReturn(Optional.of(study));
        when(studyMemberRepository.findByMemberIdAndStudyIdAndStatus(1L, 1L, StudyApplicationStatus.APPROVED))
                .thenReturn(Optional.of(studyMember));

        // when & then
        assertThrows(StudyHandler.class, () -> memberStudyCommandService.terminateStudy(1L, "스터디 성과"));
    }


    /* ---------------------------- To-Do 생성 관련 메서드  ---------------------------- */

    @Test
    @DisplayName("To-Do 생성 - 성공")
    void createToDoList() {
        // given
        when(studyRepository.findById(anyLong())).thenReturn(Optional.ofNullable(study));
        when(studyMemberRepository.findByMemberIdAndStudyIdAndStatus(anyLong(), anyLong(), any())).thenReturn(
                Optional.ofNullable(studyMember));
        when(memberRepository.findById(anyLong())).thenReturn(Optional.ofNullable(member));

        when(studyToDoRepository.save(any())).thenReturn(studyToDo);

        // when
        ToDoListCreateResponseDTO responseDTO = memberStudyCommandService.createToDoList(1L, requestDTO);

        // then
        assertEquals(responseDTO.getContent(), requestDTO.getContent());
    }

    @Test
    @DisplayName("To-Do 생성 - 스터디 회원이 아닌 경우")
    void ToDo_생성_시_스터디_회원이_아닌_경우() {
        // given
        when(studyRepository.findById(anyLong())).thenReturn(Optional.ofNullable(study));
        when(studyMemberRepository.findByMemberIdAndStudyIdAndStatus(anyLong(), anyLong(), any())).thenReturn(
                Optional.empty());


        // when & then
        assertThrows(StudyHandler.class, () -> {
            memberStudyCommandService.createToDoList(1L, requestDTO);
        });
    }

    /* ---------------------------- To-Do 수정 관련 메서드  ---------------------------- */

    @Test
    @DisplayName("To-Do 수정 - 성공")
    void ToDo_수정_성공() {
        // given
        when(studyToDoRepository.findById(anyLong())).thenReturn(Optional.ofNullable(studyToDo));

        // when
        ToDoListUpdateResponseDTO responseDTO = memberStudyCommandService.updateToDoList(1L,1L,  requestDTO);

        // then
        assertEquals(false, responseDTO.isDone());

    }

    @Test
    @DisplayName("To-Do 수정 - To-Do가 없는 경우")
    void ToDo_수정_시_ToDo가_없는_경우() {
        // given
        when(studyToDoRepository.findById(anyLong())).thenReturn(Optional.empty());

        // when & then
        assertThrows(StudyHandler.class, () -> {
            memberStudyCommandService.updateToDoList(1L,1L, requestDTO);
        });
    }

    @Test
    @DisplayName("To-Do 수정 - To-Do가 다른 스터디의 것인 경우")
    void ToDo_수정_시_ToDo가_다른_스터디의_것인_경우() {
        // given
        when(studyToDoRepository.findById(anyLong())).thenReturn(Optional.ofNullable(studyToDo));
        given(studyToDo.getStudy()).willReturn(Mockito.mock(Study.class));
        given(studyToDo.getStudy().getId()).willReturn(2L);

        // when & then
        assertThrows(StudyHandler.class, () -> {
            memberStudyCommandService.updateToDoList(1L,1L, requestDTO);
        });
    }

    @Test
    @DisplayName("To-Do 수정 - To-Do가 다른 회원의 것인 경우")
    void ToDo_수정_시_ToDo가_다른_회원의_것인_경우() {
        // given
        when(studyToDoRepository.findById(anyLong())).thenReturn(Optional.ofNullable(studyToDo));
        given(studyToDo.getMember()).willReturn(Mockito.mock(Member.class));
        given(studyToDo.getMember().getId()).willReturn(2L);

        // when & then
        assertThrows(StudyHandler.class, () -> {
            memberStudyCommandService.updateToDoList(1L,1L, requestDTO);
        });
    }

    /* ---------------------------- To-Do 삭제 관련 메서드  ---------------------------- */

    @Test
    @DisplayName("To-Do 삭제 - 성공")
    void ToDo_삭제_성공() {
        // given
        when(studyToDoRepository.findById(anyLong())).thenReturn(Optional.ofNullable(studyToDo));
        when(studyMemberRepository.findByMemberIdAndStudyIdAndStatus(anyLong(), anyLong(), any())).thenReturn(
                Optional.ofNullable(studyMember));

        // when
        ToDoListUpdateResponseDTO responseDTO = memberStudyCommandService.deleteToDoList(1L, 1L);

        // then
        verify(studyToDoRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("To-Do 삭제 - To-Do가 없는 경우")
    void ToDo_삭제_시_ToDo가_없는_경우() {
        // given
        when(studyToDoRepository.findById(anyLong())).thenReturn(Optional.empty());

        // when & then
        assertThrows(StudyHandler.class, () -> {
            memberStudyCommandService.deleteToDoList(1L, 1L);
        });
    }

    @Test
    @DisplayName("To-Do 삭제 - To-Do가 다른 스터디의 것인 경우")
    void ToDo_삭제_시_ToDo가_다른_스터디의_것인_경우() {
        // given
        when(studyToDoRepository.findById(anyLong())).thenReturn(Optional.ofNullable(studyToDo));
        given(studyToDo.getStudy()).willReturn(Mockito.mock(Study.class));
        given(studyToDo.getStudy().getId()).willReturn(2L);

        // when & then
        assertThrows(StudyHandler.class, () -> {
            memberStudyCommandService.deleteToDoList(1L, 1L);
        });
    }

    @Test
    @DisplayName("To-Do 삭제 - To-Do가 다른 회원의 것인 경우")
    void ToDo_삭제_시_ToDo가_다른_회원의_것인_경우() {
        // given
        when(studyToDoRepository.findById(anyLong())).thenReturn(Optional.ofNullable(studyToDo));
        given(studyToDo.getMember()).willReturn(Mockito.mock(Member.class));
        given(studyToDo.getMember().getId()).willReturn(2L);

        // when & then
        assertThrows(StudyHandler.class, () -> {
            memberStudyCommandService.deleteToDoList(1L, 1L);
        });
    }



}
