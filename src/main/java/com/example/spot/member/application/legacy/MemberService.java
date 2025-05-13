package com.example.spot.member.application.legacy;


import com.example.spot.member.domain.Member;
import com.example.spot.member.presentation.dto.MemberRequestDTO.MemberInfoListDTO;
import com.example.spot.member.presentation.dto.MemberRequestDTO.MemberReasonDTO;
import com.example.spot.member.presentation.dto.MemberRequestDTO.MemberRegionDTO;
import com.example.spot.member.presentation.dto.MemberRequestDTO.MemberThemeDTO;
import com.example.spot.member.presentation.dto.MemberRequestDTO.MemberUpdateDTO;
import com.example.spot.member.presentation.dto.MemberResponseDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import java.io.IOException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.transaction.annotation.Transactional;

@Deprecated
public interface MemberService extends UserDetailsService {
    // 테스트 용 멤버 생성
    MemberResponseDTO.MemberTestDTO testMember(MemberInfoListDTO memberInfoListDTO);

    MemberResponseDTO.MemberUpdateDTO updateProfile(Long memberId, MemberUpdateDTO requestDTO);

    MemberResponseDTO.MemberUpdateDTO toAdmin(Long memberId);

    @Transactional
    void save(Member member);
}

