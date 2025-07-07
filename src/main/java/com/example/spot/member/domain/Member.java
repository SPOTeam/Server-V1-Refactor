package com.example.spot.member.domain;

import com.example.spot.common.entity.BaseEntity;
import com.example.spot.member.domain.enums.Carrier;
import com.example.spot.member.domain.enums.Gender;
import com.example.spot.member.domain.enums.LoginType;
import com.example.spot.member.domain.enums.Status;
import com.example.spot.member.presentation.dto.MemberRequestDTO.MemberUpdateDTO;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@Getter
@Builder
@DynamicUpdate
@DynamicInsert
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, unique = true)
    private Long id;

    @Column(nullable = false, length = 20)
    private String name;

    @Column(length = 100)
    private String loginId;

    @Setter
    @Column(nullable = false, length = 100)
    private String password;

    @Setter
    @Column(nullable = false, length = 20)
    private String nickname;

    @Column(nullable = false, length = 50, unique = true)
    private String email;

    // 안 쓰면 지워도 될 것 같은데 사이드 이펙트 생길까봐 일단 놔둡니다..!
    @Enumerated(EnumType.STRING)
    @Column
    private Carrier carrier;

    // 안 쓰면 지워도 될 것 같은데 사이드 이펙트 생길까봐 일단 놔둡니다..!
    @Column(length = 15)
    private String phone;

    @Column(nullable = false)
    private LocalDate birth;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Gender gender;

    @Column(nullable = false)
    private String profileImage;

    @Setter
    @Column
    private LocalDateTime inactive;

    @Column(nullable = false)
    private Boolean personalInfo;

    @Column(nullable = false)
    private Boolean idInfo;

    @Column(nullable = false, columnDefinition = "BIT DEFAULT 0")
    private Boolean isAdmin;

    @Enumerated(EnumType.STRING)
    private LoginType loginType;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Status status;


    public void updateTerm(Boolean personalInfo, Boolean idInfo) {
        this.personalInfo = personalInfo;
        this.idInfo = idInfo;
    }

    public void updateInfo(MemberUpdateDTO req) {
        this.name = req.getName();
        this.phone = req.getPhone();
        this.birth = req.getBirth();
        this.carrier = req.getCarrier();
        this.idInfo = req.isIdInfo();
        this.personalInfo = req.isPersonalInfo();
        this.profileImage = req.getProfileImage();
    }

    public void updateProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public void toAdmin() {
        this.isAdmin = true;
    }


}
