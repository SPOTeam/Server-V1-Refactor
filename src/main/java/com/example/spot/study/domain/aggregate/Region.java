package com.example.spot.study.domain.aggregate;

import com.example.spot.common.entity.BaseEntity;
import com.example.spot.member.domain.association.PreferredRegion;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Region extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String code;

    private String province;

    private String district;

    private String neighborhood;

    @Builder.Default
    @OneToMany(mappedBy = "region", cascade = CascadeType.ALL)
    private List<StudyRegion> studyRegionList = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "region", cascade = CascadeType.ALL)
    private List<PreferredRegion> prefferedRegionList = new ArrayList<>();


/* ----------------------------- 연관관계 메소드 ------------------------------------- */

    public void addRegionStudy(StudyRegion studyRegion) {
        studyRegionList.add(studyRegion);
        studyRegion.setRegion(this);
    }

    public void addPreferredRegion(PreferredRegion preferredRegion) {
        prefferedRegionList.add(preferredRegion);
        preferredRegion.setRegion(this);
    }

    public String toRegionString() {
        return province + " " + district + " " + neighborhood;
    }
}
