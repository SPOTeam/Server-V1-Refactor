package com.example.spot.member.infrastructure;

import com.example.spot.common.api.code.status.ErrorStatus;
import com.example.spot.common.api.exception.handler.MemberHandler;
import com.example.spot.member.domain.Member;
import com.example.spot.member.domain.enums.LoginType;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    boolean existsByEmail(String email);

    boolean existsByNickname(String nickname);

    Optional<Member> findByEmail(String email);

    Optional<Member> findByLoginId(String loginId);

    boolean existsByLoginId(String loginId);

    boolean existsByEmailAndLoginTypeNot(String email, LoginType loginType);

    boolean existsByEmailAndLoginType(String email, LoginType loginType);

    Optional<Member> findByEmailAndLoginType(String email, LoginType loginType);

    List<Member> findAllByInactiveBefore(LocalDateTime stdTime);

    default Member getById(Long memberId) {
        return findById(memberId).orElseThrow(() -> new MemberHandler(ErrorStatus._MEMBER_NOT_FOUND));
    }
}
