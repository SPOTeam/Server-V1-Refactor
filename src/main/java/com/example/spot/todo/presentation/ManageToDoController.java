package com.example.spot.todo.presentation;

import com.example.spot.common.api.ApiResponse;
import com.example.spot.common.api.code.status.SuccessStatus;
import com.example.spot.study.domain.validation.annotation.ExistStudy;
import com.example.spot.todo.application.ManageToDoUseCase;
import com.example.spot.todo.domain.validation.annotation.ExistToDo;
import com.example.spot.todo.presentation.dto.request.ToDoListRequestDTO;
import com.example.spot.todo.presentation.dto.response.ToDoListResponseDTO.ToDoListCreateResponseDTO;
import com.example.spot.todo.presentation.dto.response.ToDoListResponseDTO.ToDoListUpdateResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/spot")
@RequiredArgsConstructor
public class ManageToDoController {

    private final ManageToDoUseCase manageToDoUseCase;

    @Tag(name = "To-Do List")
    @Operation(summary = "[To-Do List] To-Do List 생성", description = """ 
            ## [To-Do List] 로그인한 회원이 참여하는 스터디에 To-Do List를 생성합니다.
            To-Do List의 id와 제목, 생성 시간이 반환됩니다.
            """)
    @Parameter(name = "studyId", description = "스터디의 id를 입력합니다.", required = true)
    @PostMapping("/studies/{studyId}/to-do")
    public ApiResponse<ToDoListCreateResponseDTO> createToDoList(
            @PathVariable @ExistStudy Long studyId,
            @RequestBody @Valid ToDoListRequestDTO.ToDoListCreateDTO request) {
        ToDoListCreateResponseDTO toDoList = manageToDoUseCase.createToDoList(studyId,
                request);
        return ApiResponse.onSuccess(SuccessStatus._TO_DO_LIST_CREATED, toDoList);
    }

    @Tag(name = "To-Do List")
    @Operation(summary = "[To-Do List] To-Do List 내용 수정", description = """ 
            ## [To-Do List] To-Do List에 작성한 할 일의 내용을 수정 합니다.
            변경 하지 않을 값은 아예 입력하지 않아야 합니다.
            ex) date만 변경할 경우, content는 입력하지 않습니다. -> "date": "2022-12-31" 만 입력
            
            To-Do List의 id와 수정된 할 일의 내용, 수정 시간이 반환됩니다.
            """)
    @Parameter(name = "studyId", description = "스터디의 id를 입력합니다.", required = true)
    @Parameter(name = "toDoId", description = "상태를 변경할 To-Do List의 id를 입력합니다.", required = true)
    @PostMapping("/studies/{studyId}/to-do/{toDoId}/update")
    public ApiResponse<ToDoListUpdateResponseDTO> updateToDoList(
            @PathVariable @ExistStudy Long studyId,
            @PathVariable @ExistToDo Long toDoId,
            @RequestBody @Valid ToDoListRequestDTO.ToDoListCreateDTO request) {
        ToDoListUpdateResponseDTO toDoListUpdateResponseDTO = manageToDoUseCase.updateToDoList(
                studyId, toDoId, request);
        return ApiResponse.onSuccess(SuccessStatus._TO_DO_LIST_UPDATED, toDoListUpdateResponseDTO);
    }

    @Tag(name = "To-Do List")
    @Operation(summary = "[To-Do List] To-Do List 삭제", description = """ 
            ## [To-do list] 로그인한 회원이 참여하는 스터디에 To-Do List를 삭제합니다.
            
            To-Do List 완료 처리와는 다른 개념으로, To-Do List를 삭제합니다.
            To-Do List의 id와 상태 업데이트 시간이 반환됩니다.
            """)
    @Parameter(name = "studyId", description = "스터디의 id를 입력합니다.", required = true)
    @Parameter(name = "toDoId", description = "삭제할 To-Do List의 id를 입력합니다.", required = true)
    @DeleteMapping("/studies/{studyId}/to-do/{toDoId}")
    public ApiResponse<ToDoListUpdateResponseDTO> deleteToDoList(
            @PathVariable @ExistStudy Long studyId,
            @PathVariable @ExistToDo Long toDoId) {
        ToDoListUpdateResponseDTO toDoListUpdateResponseDTO = manageToDoUseCase.deleteToDoList(
                studyId, toDoId);
        return ApiResponse.onSuccess(SuccessStatus._TO_DO_LIST_DELETED, toDoListUpdateResponseDTO);
    }
}
