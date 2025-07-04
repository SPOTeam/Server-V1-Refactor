package com.example.spot.todo.application.impl;

import com.example.spot.common.api.code.status.ErrorStatus;
import com.example.spot.common.api.exception.GeneralException;
import com.example.spot.common.security.utils.SecurityUtils;
import com.example.spot.study.domain.enums.StudyApplicationStatus;
import com.example.spot.study.domain.repository.StudyMemberRepository;
import com.example.spot.todo.application.GetToDoUseCase;
import com.example.spot.todo.domain.ToDo;
import com.example.spot.todo.domain.ToDoRepository;
import com.example.spot.todo.presentation.dto.response.ToDoListResponseDTO.ToDoListSearchResponseDTO;
import com.example.spot.todo.presentation.dto.response.ToDoListResponseDTO.ToDoListSearchResponseDTO.ToDoListDTO;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetToDoUseCaseImpl implements GetToDoUseCase {

    private final StudyMemberRepository studyMemberRepository;
    private final ToDoRepository toDoRepository;

    /**
     * 특정 스터디에 저장된 내 To-Do List를 날짜 별로 페이징 조회합니다.
     *
     * @param studyId     스터디 ID
     * @param date        조회하려는 날짜
     * @param pageRequest 페이징 정보
     * @return To-Do List 목록을 반환합니다.
     * @throws GeneralException 스터디 멤버가 아닌 경우
     * @throws GeneralException 스터디 할 일이 존재하지 않는 경우
     */
    @Override
    public ToDoListSearchResponseDTO getToDoList(Long studyId, LocalDate date, PageRequest pageRequest) {
        // 로그인 중인 회원 ID 조회
        Long memberId = SecurityUtils.getCurrentUserId();

        // 로그인한 회원이 스터디 회원인지 확인
        if (!isMember(memberId, studyId)) {
            throw new GeneralException(ErrorStatus._ONLY_STUDY_MEMBER_CAN_ACCESS_TODO_LIST);
        }

        // 페이징 처리
        List<ToDo> toDos = toDoRepository.findByStudyIdAndMemberIdAndDateOrderByCreatedAtDesc(
                studyId, memberId, date, pageRequest);

        // 스터디 투 두 리스트가 존재하지 않는 경우
        if (toDos.isEmpty()) {
            throw new GeneralException(ErrorStatus._STUDY_TODO_NOT_FOUND);
        }

        // 투 두 리스트 갯수 조회
        long totalElements = toDoRepository.countByStudyIdAndMemberIdAndDate(studyId, memberId, date);

        // DTO로 변환
        List<ToDoListDTO> toDoListDTOS = getToDoListDTOS(toDos);

        return new ToDoListSearchResponseDTO(
                new PageImpl<>(toDoListDTOS, pageRequest, totalElements), toDoListDTOS, totalElements);
    }

    /**
     * 특정 스터디에 저장된 다른 스터디원의 To-Do List를 날짜 별로 페이징 조회합니다.
     *
     * @param studyId     스터디 ID
     * @param memberId    조회하려는 회원 ID
     * @param date        조회하려는 날짜
     * @param pageRequest 페이징 정보
     * @return To-Do List 목록을 반환합니다.
     * @throws GeneralException 스터디 멤버가 아닌 경우
     * @throws GeneralException 조회하려는 회원이 스터디 멤버가 아닌 경우
     * @throws GeneralException 스터디 할 일이 존재하지 않는 경우
     */
    @Override
    public ToDoListSearchResponseDTO getMemberToDoList(Long studyId, Long memberId, LocalDate date,
                                                       PageRequest pageRequest) {

        // 로그인 중인 회원이 스터디 회원인지 확인
        if (!isMember(SecurityUtils.getCurrentUserId(), studyId)) {
            throw new GeneralException(ErrorStatus._ONLY_STUDY_MEMBER_CAN_ACCESS_TODO_LIST);
        }

        // 조회하려는 회원이 스터디 회원인지 확인
        if (!isMember(memberId, studyId)) {
            throw new GeneralException(ErrorStatus._TODO_LIST_MEMBER_NOT_FOUND);
        }

        // 조회하려는 회원의 투 두 리스트 조회
        List<ToDo> toDos = toDoRepository.findByStudyIdAndMemberIdAndDateOrderByCreatedAtDesc(
                studyId, memberId, date, pageRequest);

        // 투 두 리스트가 존재하지 않는 경우
        if (toDos.isEmpty()) {
            throw new GeneralException(ErrorStatus._STUDY_TODO_NOT_FOUND);
        }

        // 투 두 리스트 갯수 조회
        long totalElements = toDoRepository.countByStudyIdAndMemberIdAndDate(studyId, memberId, date);

        // DTO로 변환
        List<ToDoListDTO> toDoListDTOS = getToDoListDTOS(toDos);

        return new ToDoListSearchResponseDTO(
                new PageImpl<>(toDoListDTOS, pageRequest, totalElements), toDoListDTOS, totalElements);
    }

    /**
     * 투 두 리스트를 DTO로 변환합니다.
     *
     * @param toDos 투 두 리스트
     * @return 투 두 리스트 DTO 목록을 반환합니다.
     */
    private static List<ToDoListDTO> getToDoListDTOS(List<ToDo> toDos) {
        List<ToDoListDTO> toDoListDTOS = toDos.stream()
                .map(toDoList -> ToDoListDTO.builder()
                        .id(toDoList.getId())
                        .content(toDoList.getContent())
                        .date(toDoList.getDate())
                        .isDone(toDoList.isDone())
                        .build())
                .toList();
        return toDoListDTOS;
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
