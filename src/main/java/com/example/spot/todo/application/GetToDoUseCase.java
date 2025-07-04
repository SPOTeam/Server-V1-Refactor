package com.example.spot.todo.application;

import com.example.spot.todo.presentation.dto.response.ToDoListResponseDTO.ToDoListSearchResponseDTO;
import java.time.LocalDate;
import org.springframework.data.domain.PageRequest;

public interface GetToDoUseCase {

    // 내 투두 리스트 조회
    ToDoListSearchResponseDTO getToDoList(Long studyId, LocalDate date, PageRequest pageRequest);

    // 스터디원 투두 리스트 조회
    ToDoListSearchResponseDTO getMemberToDoList(Long studyId, Long memberId, LocalDate date, PageRequest pageRequest);
}
