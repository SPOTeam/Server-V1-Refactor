package com.example.spot.todo.application.impl;

import com.example.spot.common.api.code.status.ErrorStatus;
import com.example.spot.common.api.exception.handler.StudyHandler;
import com.example.spot.common.security.utils.SecurityUtils;
import com.example.spot.member.domain.Member;
import com.example.spot.notification.domain.Notification;
import com.example.spot.notification.domain.NotificationRepository;
import com.example.spot.notification.domain.enums.NotifyType;
import com.example.spot.study.domain.association.StudyMember;
import com.example.spot.study.domain.enums.StudyApplicationStatus;
import com.example.spot.study.domain.repository.StudyMemberRepository;
import com.example.spot.todo.application.ToggleToDoUseCase;
import com.example.spot.todo.domain.ToDo;
import com.example.spot.todo.domain.ToDoRepository;
import com.example.spot.todo.presentation.dto.response.ToDoListResponseDTO.ToDoListUpdateResponseDTO;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ToggleToDoUseCaseImpl implements ToggleToDoUseCase {

    private final ToDoRepository toDoRepository;
    private final StudyMemberRepository studyMemberRepository;
    private final NotificationRepository notificationRepository;

    /**
     * To-Do List에 작성한 할 일의 체크 상태를 변경 합니다. 체크 상태를 변경 하면 해당 스터디에 참여하고 있는 모든 회원에게 알림이 전송됩니다.
     *
     * @param studyId    스터디 ID
     * @param toDoListId 변경할 To-Do List ID
     * @return To-Do List 변경 여부와 변경 시간
     * @throws StudyHandler To-Do List를 찾을 수 없을 때
     * @throws StudyHandler To-Do List가 스터디에 속하지 않을 때
     * @throws StudyHandler To-Do List를 변경하는 회원이 스터디 회원이 아닐 때
     * @throws StudyHandler 알림 생성 할 스터디 회원을 찾을 수 없을 때
     */
    @Override
    public ToDoListUpdateResponseDTO checkToDoList(Long studyId, Long toDoListId) {

        // To-Do List 조회
        ToDo toDo = toDoRepository.findById(toDoListId)
                .orElseThrow(() -> new StudyHandler(ErrorStatus._STUDY_TODO_NOT_FOUND));

        // To-Do List가 속한 스터디가 아니면 예외 처리
        if (!Objects.equals(toDo.getStudy().getId(), studyId)) {
            throw new StudyHandler(ErrorStatus._STUDY_TODO_IS_NOT_BELONG_TO_STUDY);
        }

        // To-Do List를 변경하는 회원이 스터디 회원이 아니면 예외 처리
        Long currentUserId = SecurityUtils.getCurrentUserId();
        if (!toDo.getMember().getId().equals(currentUserId)) {
            throw new StudyHandler(ErrorStatus._STUDY_TODO_NOT_AUTHORIZED);
        }

        // To-Do List 체크 상태 변경
        toDo.check();

        // 스터디 회원의 To-Do List 중 하나가 완료 되면, 해당 스터디의 모든 회원에게 알림 전송
        if (toDo.isDone()) {
            List<Member> members = studyMemberRepository.findAllByStudyIdAndStatus(studyId,
                            StudyApplicationStatus.APPROVED).stream()
                    .map(StudyMember::getMember)
                    .toList();

            // 알림을 생성할 회원이 없으면 알림 생성하지 않음
            if (members.isEmpty()) {
                return ToDoListUpdateResponseDTO.builder()
                        .id(toDo.getId())
                        .isDone(toDo.isDone())
                        .updatedAt(toDo.getUpdatedAt())
                        .build();
            }

            // 알림 생성
            members.forEach(studyMember -> {
                Notification notification = Notification.builder()
                        .member(studyMember)
                        .notifierName(toDo.getMember().getName()) // To-Do 완료한 회원 이름
                        .study(toDo.getStudy())
                        .type(NotifyType.TO_DO_UPDATE)
                        .isChecked(Boolean.FALSE)
                        .build();
                notificationRepository.save(notification);
            });
        }

        // To-Do List 저장
        toDoRepository.save(toDo);

        // To-Do List 변경 DTO 반환
        return ToDoListUpdateResponseDTO.builder()
                .id(toDo.getId())
                .isDone(toDo.isDone())
                .updatedAt(toDo.getUpdatedAt())
                .build();
    }

}
