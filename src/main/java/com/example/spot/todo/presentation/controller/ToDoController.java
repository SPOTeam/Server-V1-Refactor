package com.example.spot.todo.presentation.controller;

import com.example.spot.common.api.ApiResponse;
import com.example.spot.common.api.code.status.SuccessStatus;
import com.example.spot.member.domain.validation.annotation.ExistMember;
import com.example.spot.study.domain.validation.annotation.ExistStudy;
import com.example.spot.todo.application.ToDoCommandService;
import com.example.spot.todo.application.ToDoQueryService;
import com.example.spot.todo.presentation.dto.request.ToDoListRequestDTO;
import com.example.spot.todo.presentation.dto.response.ToDoListResponseDTO.ToDoListCreateResponseDTO;
import com.example.spot.todo.presentation.dto.response.ToDoListResponseDTO.ToDoListSearchResponseDTO;
import com.example.spot.todo.presentation.dto.response.ToDoListResponseDTO.ToDoListUpdateResponseDTO;
import com.example.spot.todo.domain.validation.annotation.ExistToDo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
@RequestMapping("/spot")
@Validated
public class ToDoController {

    private final ToDoQueryService toDoQueryService;
    private final ToDoCommandService toDoCommandService;


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
        ToDoListCreateResponseDTO toDoList = toDoCommandService.createToDoList(studyId,
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
        ToDoListUpdateResponseDTO toDoListUpdateResponseDTO = toDoCommandService.updateToDoList(
            studyId, toDoId, request);
        return ApiResponse.onSuccess(SuccessStatus._TO_DO_LIST_UPDATED, toDoListUpdateResponseDTO);
    }



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
        ToDoListUpdateResponseDTO toDoListUpdateResponseDTO = toDoCommandService.checkToDoList(
            studyId, toDoId);
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
        ToDoListUpdateResponseDTO toDoListUpdateResponseDTO = toDoCommandService.deleteToDoList(
            studyId, toDoId);
        return ApiResponse.onSuccess(SuccessStatus._TO_DO_LIST_DELETED, toDoListUpdateResponseDTO);
    }

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
        ToDoListSearchResponseDTO toDoList = toDoQueryService.getToDoList(studyId, date,
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
        ToDoListSearchResponseDTO toDoList = toDoQueryService.getMemberToDoList(studyId,
            memberId, date, PageRequest.of(page, size));
        return ApiResponse.onSuccess(SuccessStatus._TO_DO_LIST_FOUND, toDoList);
    }

}
