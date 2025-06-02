package com.example.spot.todo.application;

import com.example.spot.todo.presentation.dto.request.ToDoListRequestDTO;
import com.example.spot.todo.presentation.dto.response.ToDoListResponseDTO;
import com.example.spot.todo.presentation.dto.response.ToDoListResponseDTO.ToDoListCreateResponseDTO;

public interface ToDoCommandService {

    // 투두 리스트 생성
    ToDoListCreateResponseDTO createToDoList(Long studyId, ToDoListRequestDTO.ToDoListCreateDTO toDoListCreateDTO);

    // 투두 리스트 체크
    ToDoListResponseDTO.ToDoListUpdateResponseDTO checkToDoList(Long studyId, Long toDoListId);

    // 투두 리스트 수정
    ToDoListResponseDTO.ToDoListUpdateResponseDTO updateToDoList(Long studyId, Long toDoListId, ToDoListRequestDTO.ToDoListCreateDTO toDoListCreateDTO);

    // 투두 리스트 삭제
     ToDoListResponseDTO.ToDoListUpdateResponseDTO deleteToDoList(Long studyId, Long toDoListId);
}
