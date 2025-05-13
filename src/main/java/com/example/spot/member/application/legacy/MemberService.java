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



    Member findMemberByEmail(String email);

    boolean isMemberExists(String email);

    MemberResponseDTO.MemberUpdateDTO updateTheme(Long memberId, MemberThemeDTO requestDTO);
    MemberResponseDTO.MemberUpdateDTO updateRegion(Long memberId, MemberRegionDTO requestDTO);
    MemberResponseDTO.MemberUpdateDTO updateProfile(Long memberId, MemberUpdateDTO requestDTO);
    MemberResponseDTO.MemberUpdateDTO updateStudyReason(Long memberId, MemberReasonDTO requestDTO);

    MemberResponseDTO.MemberThemeDTO getThemes(Long memberId);
    MemberResponseDTO.MemberRegionDTO getRegions(Long memberId);
    MemberResponseDTO.MemberStudyReasonDTO getStudyReasons(Long memberId);
    MemberResponseDTO.MemberUpdateDTO toAdmin(Long memberId);

    @Transactional
    void save(Member member);
}

