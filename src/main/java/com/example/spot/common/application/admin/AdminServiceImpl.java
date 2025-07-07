package com.example.spot.common.application.admin;

import com.example.spot.auth.domain.RefreshTokenRepository;
import com.example.spot.common.api.code.status.ErrorStatus;
import com.example.spot.common.api.exception.handler.MemberHandler;
import com.example.spot.common.presentation.dto.admin.AdminResponseDTO;
import com.example.spot.common.security.utils.SecurityUtils;
import com.example.spot.member.domain.Member;
import com.example.spot.member.infrastructure.MemberRepository;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminServiceImpl implements AdminService {

    private final MemberRepository memberRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    /* ----------------------------- 회원 정보 관리 API ------------------------------------- */

    @Override
    public boolean getIsAdmin() {
        Long memberId = SecurityUtils.getCurrentUserId();
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberHandler(ErrorStatus._MEMBER_NOT_FOUND));
        return member.getIsAdmin();
    }

    @Override
    public AdminResponseDTO.DeletedMemberListDTO deleteInactiveMembers() {

        // 삭제 기준일시
        LocalDateTime stdTime = LocalDateTime.now().minusDays(30);

        // 회원 삭제
        List<Member> deletedMembers = memberRepository.findAllByInactiveBefore(stdTime);
        List<Long> deletedMemberIds = deletedMembers.stream().map(Member::getId).toList();
        AdminResponseDTO.DeletedMemberListDTO deletedMemberListDTO = AdminResponseDTO.DeletedMemberListDTO.toDTO(
                deletedMembers);

        // Token 정리
        refreshTokenRepository.deleteAllByMemberIdIn(deletedMemberIds);

        // 회원 정보 정리
        memberRepository.deleteAllByIdInBatch(deletedMemberIds);

        return deletedMemberListDTO;
    }

    /* ----------------------------- 신고 내역 관리 API ------------------------------------- */

}
