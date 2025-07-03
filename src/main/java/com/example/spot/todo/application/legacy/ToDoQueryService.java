package com.example.spot.todo.application.legacy;

import com.example.spot.todo.presentation.legacy.dto.response.ToDoListResponseDTO;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDate;

public interface ToDoQueryService {

    // 내 투두 리스트 조회
    ToDoListResponseDTO.ToDoListSearchResponseDTO getToDoList(Long studyId, LocalDate date, PageRequest pageRequest);

    // 스터디원 투두 리스트 조회
    ToDoListResponseDTO.ToDoListSearchResponseDTO getMemberToDoList(Long studyId, Long memberId, LocalDate date, PageRequest pageRequest);

}
