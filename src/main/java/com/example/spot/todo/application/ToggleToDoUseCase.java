package com.example.spot.todo.application;

import com.example.spot.todo.presentation.dto.response.ToDoListResponseDTO;

public interface ToggleToDoUseCase {

    // 투두 리스트 체크
    ToDoListResponseDTO.ToDoListUpdateResponseDTO checkToDoList(Long studyId, Long toDoListId);
}
