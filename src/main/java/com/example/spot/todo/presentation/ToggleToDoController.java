package com.example.spot.todo.presentation;

import com.example.spot.common.api.ApiResponse;
import com.example.spot.common.api.code.status.SuccessStatus;
import com.example.spot.study.domain.validation.annotation.ExistStudy;
import com.example.spot.todo.application.ToggleToDoUseCase;
import com.example.spot.todo.domain.validation.annotation.ExistToDo;
import com.example.spot.todo.presentation.dto.response.ToDoListResponseDTO.ToDoListUpdateResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/spot")
@RequiredArgsConstructor
public class ToggleToDoController {

    private final ToggleToDoUseCase toggleToDoUseCase;

    @Tag(name = "To-Do List")
    @Operation(summary = "[To-Do List] To-Do List 체크 처리", description = """ 
            ## [To-Do List] To-Do List에 작성한 할 일의 체크 상태를 변경 합니다.
            
            체크 표시 되어 있는 경우, 해당 API를 재호출 하면 체크가 해제됩니다.
            
            To-Do List의 id와 체크한 할 일의 id, 체크 여부가 반환됩니다.
            
            본인이 작성한 To-Do List만 체크할 수 있습니다.
            체크 여부가 true 인 경우, 할 일이 완료 되었음을 의미합니다.
            """)
    @Parameter(name = "studyId", description = "스터디의 id를 입력합니다.", required = true)
    @Parameter(name = "toDoId", description = "상태를 변경할 To-Do List의 id를 입력합니다.", required = true)
    @PostMapping("/studies/{studyId}/to-do/{toDoId}/check")
    public ApiResponse<ToDoListUpdateResponseDTO> checkToDoList(
            @PathVariable @ExistStudy Long studyId,
            @PathVariable @ExistToDo Long toDoId) {
        ToDoListUpdateResponseDTO toDoListUpdateResponseDTO = toggleToDoUseCase.checkToDoList(
                studyId, toDoId);
        return ApiResponse.onSuccess(SuccessStatus._TO_DO_LIST_UPDATED, toDoListUpdateResponseDTO);
    }
}
