package com.example.spot.member.application.refactor;

import com.example.spot.member.presentation.dto.MemberRequestDTO;
import com.example.spot.member.presentation.dto.MemberResponseDTO;

public interface MemberTestSupportService {

	// 테스트 용 멤버 생성
	MemberResponseDTO.MemberTestDTO testMember(MemberRequestDTO.MemberInfoListDTO memberInfoListDTO);

	MemberResponseDTO.MemberUpdateDTO toAdmin(Long memberId);

}
