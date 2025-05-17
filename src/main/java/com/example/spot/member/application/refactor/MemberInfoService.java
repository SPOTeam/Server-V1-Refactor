package com.example.spot.member.application.refactor;

import com.example.spot.member.presentation.dto.MemberRequestDTO;
import com.example.spot.member.presentation.dto.MemberResponseDTO;

public interface MemberInfoService {

	MemberResponseDTO.MemberUpdateDTO updateProfile(Long memberId, MemberRequestDTO.MemberUpdateDTO requestDTO);
}
