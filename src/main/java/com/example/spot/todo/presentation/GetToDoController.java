package com.example.spot.todo.presentation;

import com.example.spot.common.api.ApiResponse;
import com.example.spot.common.api.code.status.SuccessStatus;
import com.example.spot.member.domain.validation.annotation.ExistMember;
import com.example.spot.study.domain.validation.annotation.ExistStudy;
import com.example.spot.todo.application.GetToDoUseCase;
import com.example.spot.todo.presentation.dto.response.ToDoListResponseDTO.ToDoListSearchResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Min;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/spot")
@RequiredArgsConstructor
public class GetToDoController {

    private final GetToDoUseCase getToDoUseCase;

    @Tag(name = "To-Do List")
    @Operation(summary = "[To-Do List] 내 To-Do List 조회", description = """ 
            ## [To-Do List] 특정 스터디에 저장된 내 To-Do List를 날짜 별로 페이징 조회합니다.
            조회하고 싶은 날짜를 입력 받아, 해당 날짜의 할 일 목록, 체크 여부가 반환됩니다.
            
            """)
    @Parameter(name = "studyId", description = "스터디의 id를 입력합니다.", required = true)
    @Parameter(name = "page", description = "조회할 페이지 번호를 입력 받습니다. 페이지 번호는 0부터 시작합니다.", required = true)
    @Parameter(name = "size", description = "조회할 페이지 크기를 입력 받습니다. 페이지 크기는 1 이상의 정수 입니다. ", required = true)
    @Parameter(name = "date", description = "조회할 날짜를 입력 받습니다. 날짜는 yyyy-MM-dd 형식으로 입력 받습니다.", required = true)
    @GetMapping("/studies/{studyId}/to-do/my")
    public ApiResponse<ToDoListSearchResponseDTO> getMyToDoList(
            @PathVariable @ExistStudy Long studyId,
            @RequestParam @Min(0) Integer page,
            @RequestParam @Min(1) Integer size,
            @RequestParam LocalDate date) {
        ToDoListSearchResponseDTO toDoList = getToDoUseCase.getToDoList(studyId, date,
                PageRequest.of(page, size));
        return ApiResponse.onSuccess(SuccessStatus._TO_DO_LIST_FOUND, toDoList);
    }

    @Tag(name = "To-Do List")
    @Operation(summary = "[To-Do List] 다른 스터디 원 To-Do List 조회", description = """ 
            ## [To-Do List] 특정 스터디에 저장된 다른 스터디원의 To-Do List를 날짜 별로 페이징 조회합니다.
            조회하고 싶은 날짜를 입력 받아, 해당 날짜의 할 일 목록, 체크 여부가 반환됩니다.
            """)
    @Parameter(name = "studyId", description = "스터디의 id를 입력합니다.", required = true)
    @Parameter(name = "memberId", description = "To-do list를 조회할 회원의 id를 입력합니다.", required = true)
    @Parameter(name = "page", description = "조회할 페이지 번호를 입력 받습니다. 페이지 번호는 0부터 시작합니다.", required = true)
    @Parameter(name = "size", description = "조회할 페이지 크기를 입력 받습니다. 페이지 크기는 1 이상의 정수 입니다. ", required = true)
    @Parameter(name = "date", description = "조회할 날짜를 입력 받습니다. 날짜는 yyyy-MM-dd 형식으로 입력 받습니다.", required = true)
    @GetMapping("/studies/{studyId}/to-do/members/{memberId}")
    public ApiResponse<ToDoListSearchResponseDTO> getOtherToDoList(
            @PathVariable @ExistStudy Long studyId,
            @PathVariable @ExistMember Long memberId,
            @RequestParam @Min(0) Integer page,
            @RequestParam @Min(1) Integer size,
            @RequestParam LocalDate date) {
        ToDoListSearchResponseDTO toDoList = getToDoUseCase.getMemberToDoList(studyId,
                memberId, date, PageRequest.of(page, size));
        return ApiResponse.onSuccess(SuccessStatus._TO_DO_LIST_FOUND, toDoList);
    }


}
