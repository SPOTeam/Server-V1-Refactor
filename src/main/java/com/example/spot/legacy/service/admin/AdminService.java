package com.example.spot.legacy.service.admin;

import com.example.spot.refactor.common.presentation.dto.admin.AdminResponseDTO;

public interface AdminService {

/* ----------------------------- 회원 정보 관리 API ------------------------------------- */

    boolean getIsAdmin();

    AdminResponseDTO.DeletedMemberListDTO deleteInactiveMembers();

/* ----------------------------- 신고 내역 관리 API ------------------------------------- */

}
