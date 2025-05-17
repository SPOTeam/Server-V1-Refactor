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

// TODO 추후 삭제 예정 -> 구글 로그인 관련 로직이 남아있음
@Deprecated
public interface MemberService extends UserDetailsService {

    @Transactional
    void save(Member member);
}

