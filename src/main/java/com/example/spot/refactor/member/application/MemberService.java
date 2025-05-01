package com.example.spot.refactor.member.application;


import com.example.spot.refactor.member.domain.Member;
import com.example.spot.refactor.member.presentation.dto.MemberRequestDTO.MemberInfoListDTO;
import com.example.spot.refactor.member.presentation.dto.MemberRequestDTO.MemberReasonDTO;
import com.example.spot.refactor.member.presentation.dto.MemberRequestDTO.MemberRegionDTO;
import com.example.spot.refactor.member.presentation.dto.MemberRequestDTO.MemberThemeDTO;
import com.example.spot.refactor.member.presentation.dto.MemberRequestDTO.MemberUpdateDTO;
import com.example.spot.refactor.member.presentation.dto.MemberResponseDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import java.io.IOException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.transaction.annotation.Transactional;

public interface MemberService extends UserDetailsService {
    // 테스트 용 멤버 생성
    MemberResponseDTO.MemberTestDTO testMember(MemberInfoListDTO memberInfoListDTO);

    MemberResponseDTO.SocialLoginSignInDTO signUpByKAKAO(String code) throws JsonProcessingException;

    MemberResponseDTO.SocialLoginSignInDTO signUpByKAKAOForTest(String code)
        throws JsonProcessingException;

    void redirectURL() throws IOException;

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

