package com.example.spot.study.domain.association;

import com.example.spot.common.entity.BaseEntity;
import com.example.spot.member.domain.association.PreferredTheme;
import com.example.spot.study.domain.enums.ThemeType;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@Getter
@Builder
@DynamicUpdate
@DynamicInsert
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Theme extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, unique = true)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ThemeType themeType;

    //== 해당 테마를 선호하는 멤버 목록 ==//
    @Builder.Default
    @OneToMany(mappedBy = "theme", cascade = CascadeType.ALL)
    private List<PreferredTheme> preferredThemeList = new ArrayList<>();

    //== 테마별 스터디 목록 ==//
    @Builder.Default
    @OneToMany(mappedBy = "theme", cascade = CascadeType.ALL)
    private List<StudyTheme> studyThemeList = new ArrayList<>();

    /* ----------------------------- 연관관계 메소드 ------------------------------------- */

    public void addMemberTheme(PreferredTheme preferredTheme) {
        preferredThemeList.add(preferredTheme);
        preferredTheme.setTheme(this);
    }

    public void addStudyTheme(StudyTheme studyTheme) {
        studyThemeList.add(studyTheme);
        studyTheme.setTheme(this);
    }
}
