package com.example.spot.todo.application.impl;

import com.example.spot.common.api.code.status.ErrorStatus;
import com.example.spot.common.api.exception.handler.MemberHandler;
import com.example.spot.common.api.exception.handler.StudyHandler;
import com.example.spot.common.security.utils.SecurityUtils;
import com.example.spot.member.domain.Member;
import com.example.spot.member.infrastructure.MemberRepository;
import com.example.spot.study.domain.Study;
import com.example.spot.study.domain.StudyRepository;
import com.example.spot.study.domain.enums.StudyApplicationStatus;
import com.example.spot.study.domain.repository.StudyMemberRepository;
import com.example.spot.todo.application.ManageToDoUseCase;
import com.example.spot.todo.domain.ToDo;
import com.example.spot.todo.domain.ToDoRepository;
import com.example.spot.todo.presentation.dto.request.ToDoListRequestDTO.ToDoListCreateDTO;
import com.example.spot.todo.presentation.dto.response.ToDoListResponseDTO.ToDoListCreateResponseDTO;
import com.example.spot.todo.presentation.dto.response.ToDoListResponseDTO.ToDoListUpdateResponseDTO;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ManageToDoUseCaseImpl implements ManageToDoUseCase {

    private final MemberRepository memberRepository;
    private final StudyRepository studyRepository;
    private final StudyMemberRepository studyMemberRepository;
    private final ToDoRepository toDoRepository;

    /**
     * To-Do List를 생성합니다.
     *
     * @param studyId           생성할 To-Do List가 속한 스터디 ID
     * @param toDoListCreateDTO 생성할 To-Do List 정보
     * @return 생성된 To-Do List 정보
     * @throws StudyHandler 스터디를 찾을 수 없을 때
     * @throws StudyHandler To-Do List를 생성하는 회원이 스터디 회원이 아닐 때
     * @throws StudyHandler 해당 회원을 찾을 수 없을 때
     */
    @Override
    public ToDoListCreateResponseDTO createToDoList(Long studyId,
                                                    ToDoListCreateDTO toDoListCreateDTO) {

        // 스터디 조회
        Study study = studyRepository.findById(studyId)
                .orElseThrow(() -> new StudyHandler(ErrorStatus._STUDY_NOT_FOUND));

        // To-Do List를 생성하는 회원 ID 조회
        Long currentUserId = SecurityUtils.getCurrentUserId();

        // To-Do List를 생성하는 회원이 스터디 회원인지 확인
        if (!isMember(currentUserId, studyId)) {
            throw new StudyHandler(ErrorStatus._STUDY_MEMBER_NOT_FOUND);
        }

        // 회원 조회
        Member member = memberRepository.findById(currentUserId)
                .orElseThrow(() -> new MemberHandler(ErrorStatus._MEMBER_NOT_FOUND));

        // To-Do List 생성
        ToDo toDo = ToDo.builder()
                .study(study)
                .member(member)
                .date(toDoListCreateDTO.getDate())
                .isDone(false)
                .content(toDoListCreateDTO.getContent())
                .build();

        // To-Do List 저장
        toDo.setToDoList();
        toDoRepository.save(toDo);

        // To-Do List 생성 DTO 반환
        return ToDoListCreateResponseDTO.builder()
                .id(toDo.getId())
                .content(toDo.getContent())
                .createdAt(toDo.getCreatedAt())
                .build();
    }

    /**
     * To-Do List 내용을 수정합니다.
     *
     * @param studyId           수정할 To-Do List가 속한 스터디 ID
     * @param toDoListId        수정할 To-Do List ID
     * @param toDoListCreateDTO 수정할 To-Do List 정보
     * @return 수정된 To-Do List 정보
     * @throws StudyHandler To-Do List를 찾을 수 없을 때
     * @throws StudyHandler To-Do List가 스터디에 속하지 않을 때
     * @throws StudyHandler To-Do List를 수정하는 회원이 스터디 회원이 아닐 때
     */
    @Override
    public ToDoListUpdateResponseDTO updateToDoList(Long studyId, Long toDoListId,
                                                    ToDoListCreateDTO toDoListCreateDTO) {

        // To-Do List 조회
        ToDo toDo = toDoRepository.findById(toDoListId)
                .orElseThrow(() -> new StudyHandler(ErrorStatus._STUDY_TODO_NOT_FOUND));

        // To-Do List가 속한 스터디가 아니면 예외 처리
        if (!Objects.equals(toDo.getStudy().getId(), studyId)) {
            throw new StudyHandler(ErrorStatus._STUDY_TODO_IS_NOT_BELONG_TO_STUDY);
        }

        // To-Do List를 수정하는 회원이 스터디 회원이 아니면 예외 처리
        Long currentUserId = SecurityUtils.getCurrentUserId();
        if (!toDo.getMember().getId().equals(currentUserId)) {
            throw new StudyHandler(ErrorStatus._STUDY_TODO_NOT_AUTHORIZED);
        }

        // To-Do List 수정
        toDo.update(toDoListCreateDTO.getContent(), toDoListCreateDTO.getDate());

        // To-Do List 저장
        toDoRepository.save(toDo);

        // To-Do List 변경 DTO 반환
        return ToDoListUpdateResponseDTO.builder()
                .id(toDo.getId())
                .isDone(toDo.isDone())
                .updatedAt(toDo.getUpdatedAt())
                .build();
    }

    /**
     * To-Do List를 삭제합니다.
     *
     * @param studyId    삭제할 To-Do List가 속한 스터디 ID
     * @param toDoListId 삭제할 To-Do List ID
     * @return 삭제된 To-Do List 정보
     * @throws StudyHandler To-Do List를 찾을 수 없을 때
     * @throws StudyHandler To-Do List가 스터디에 속하지 않을 때
     * @throws StudyHandler To-Do List를 삭제하는 회원이 스터디 회원이 아닐 때
     */
    @Override
    public ToDoListUpdateResponseDTO deleteToDoList(Long studyId, Long toDoListId) {

        // 로그인 중인 회원 ID 조회
        Long currentUserId = SecurityUtils.getCurrentUserId();

        // To-Do List를 삭제하는 회원이 스터디 회원인지 확인
        if (!isMember(currentUserId, studyId)) {
            throw new StudyHandler(ErrorStatus._STUDY_MEMBER_NOT_FOUND);
        }

        // To-Do List 조회
        ToDo toDo = toDoRepository.findById(toDoListId)
                .orElseThrow(() -> new StudyHandler(ErrorStatus._STUDY_TODO_NOT_FOUND));

        // To-Do List가 속한 스터디가 아니면 예외 처리
        if (!Objects.equals(toDo.getStudy().getId(), studyId)) {
            throw new StudyHandler(ErrorStatus._STUDY_TODO_IS_NOT_BELONG_TO_STUDY);
        }

        // To-Do List를 삭제하는 회원의 ID와 To-Do List를 생성한 회원의 ID가 다르면 예외 처리
        if (!toDo.getMember().getId().equals(currentUserId)) {
            throw new StudyHandler(ErrorStatus._STUDY_TODO_NOT_AUTHORIZED);
        }

        // To-Do List 삭제
        toDoRepository.deleteById(toDoListId);

        // To-Do List 삭제 DTO 반환
        return ToDoListUpdateResponseDTO.builder()
                .id(toDo.getId())
                .isDone(toDo.isDone())
                .updatedAt(toDo.getUpdatedAt())
                .build();
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
}
