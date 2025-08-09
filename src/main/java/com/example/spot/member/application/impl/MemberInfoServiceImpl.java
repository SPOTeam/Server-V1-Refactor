package com.example.spot.member.application.impl;

import com.example.spot.common.api.exception.handler.MemberHandler;
import com.example.spot.member.application.MemberInfoService;
import com.example.spot.member.domain.Member;
import com.example.spot.member.infrastructure.jpa.MemberRepository;
import com.example.spot.member.presentation.dto.MemberRequestDTO.MemberUpdateDTO;
import com.example.spot.member.presentation.dto.MemberResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class MemberInfoServiceImpl implements MemberInfoService {

    private final MemberRepository memberRepository;

    /**
     * 회원의 프로필을 업데이트 합니다.
     *
     * @param memberId   변경할 회원 ID
     * @param requestDTO 변경할 회원 정보
     * @return 변경 된 회원 ID와 변경 시간
     * @throws MemberHandler 회원을 찾을 수 없을 경우
     */
    @Override
    public MemberResponseDTO.MemberUpdateDTO updateProfile(Long memberId, MemberUpdateDTO requestDTO) {
        // 회원 조회
        Member member = memberRepository.getById(memberId);

        // 회원 정보 업데이트
        member.updateInfo(
                requestDTO.getName(),
                requestDTO.getPhone(),
                requestDTO.getBirth(),
                requestDTO.getCarrier(),
                requestDTO.isIdInfo(),
                requestDTO.isPersonalInfo(),
                requestDTO.getProfileImage());

        // 업데이트된 회원 정보 반환
        return toUpdateDTO(member);
    }

    private MemberResponseDTO.MemberUpdateDTO toUpdateDTO(Member member) {
        return MemberResponseDTO.MemberUpdateDTO.builder()
                .memberId(member.getId())
                .updatedAt(member.getUpdatedAt())
                .build();
    }
}
