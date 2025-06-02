package com.example.spot.member.application.legacy;

import com.example.spot.member.domain.Member;
import com.example.spot.member.domain.MemberRepository;
import com.example.spot.auth.domain.CustomUserDetails;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


// TODO 추후 삭제 예정 -> 구글 로그인 관련 로직이 남아있음

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
@Deprecated
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;

    /**
     * 회원의 정보를 조회합니다.
     * @param username 회원 식별자(ID)
     * @return 회원 정보
     * @throws UsernameNotFoundException 회원을 찾을 수 없을 경우
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 회원 ID Long 타입으로 변환
        Long memberId = parseUsernameToMemberId(username);

        // 회원 조회
        Member member = memberRepository.findById(memberId)
            .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));

        // 권한 설정 -> ROLE_USER 또는 ROLE_ADMIN
        List<GrantedAuthority> authorities = List.of(
            new SimpleGrantedAuthority("ROLE_" + (member.getIsAdmin() ? "ADMIN" : "USER"))
        );

        // CustomUserDetails 객체 생성
        return CustomUserDetails.builder()
            .email(member.getEmail())
            .memberId(member.getId())
            .password(member.getPassword())
            .enabled(true)
            .authorities(authorities)
            .build();
    }

    /**
     * 문자열로 입력된 회원 ID를 Long 타입으로 파싱합니다.
     * @param username 회원 ID 문자열
     * @return 회원 ID
     * @throws UsernameNotFoundException 회원 ID 형식이 잘못된 경우
     */
    private Long parseUsernameToMemberId(String username) {
        try {
            return Long.parseLong(username);
        } catch (NumberFormatException e) {
            throw new UsernameNotFoundException("Invalid user ID format");
        }
    }


    @Override
    @Transactional
    public void save(Member member) {
        memberRepository.save(member);
    }

}
