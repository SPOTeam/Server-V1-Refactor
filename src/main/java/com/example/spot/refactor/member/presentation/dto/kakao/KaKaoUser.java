package com.example.spot.refactor.member.presentation.dto.kakao;

import com.example.spot.refactor.member.domain.Member;
import com.example.spot.refactor.member.domain.enums.Carrier;
import com.example.spot.refactor.member.domain.enums.LoginType;
import com.example.spot.refactor.common.security.utils.MemberUtils;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@AllArgsConstructor
@NoArgsConstructor
public class KaKaoUser {
    Long id;
    String connected_at;
    KaKaoPropertiesDTO properties;
    KaKaoAccountDTO kakao_account;

    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    @AllArgsConstructor
    @NoArgsConstructor
    public static class KaKaoAccountDTO {
        Boolean has_email;
        Boolean email_needs_agreement;
        Boolean is_email_valid;
        Boolean is_email_verified;
        String email;
    }

    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    @AllArgsConstructor
    @NoArgsConstructor
    public static class KaKaoPropertiesDTO{
        String nickname;
        String profile_image;
        String thumbnail_image;
    }

    public Member toMember(){
        return Member.builder()
            .name(properties.getNickname())
            .nickname(properties.getNickname())
            .email(kakao_account.getEmail())
            .profileImage(properties.getProfile_image())
            .carrier(Carrier.NONE)
            .password("default")
            .phone(MemberUtils.generatePhoneNumber())
            .birth(LocalDate.now())
            .personalInfo(false)
            .idInfo(false)
            .isAdmin(false)
            .loginType(LoginType.KAKAO)
            .build();
    }

}
