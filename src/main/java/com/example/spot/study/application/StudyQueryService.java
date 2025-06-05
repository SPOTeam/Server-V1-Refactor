package com.example.spot.study.application;

import com.example.spot.study.domain.enums.StudySortBy;
import com.example.spot.study.domain.enums.ThemeType;
import com.example.spot.study.presentation.dto.request.StudySearchRequestDTO;
import com.example.spot.study.presentation.dto.request.StudySearchRequestWithThemeDTO;
import com.example.spot.study.presentation.dto.response.SearchResponseDTO.HotKeywordDTO;
import com.example.spot.study.presentation.dto.response.SearchResponseDTO.MyPageDTO;
import com.example.spot.study.presentation.dto.response.SearchResponseDTO.StudyPreviewDTO;
import com.example.spot.study.presentation.dto.response.StudyInfoResponseDTO;

import org.springframework.data.domain.Pageable;

public interface StudyQueryService {

    // 인기 검색어 조회
    HotKeywordDTO getHotKeyword();

    // 스터디 정보 조회
    StudyInfoResponseDTO.StudyInfoDTO getStudyInfo(Long studyId);

    // 마이페이지 용 스터디 개수 조회
    MyPageDTO getMyPageStudyCount(Long memberId);

    // 스터디 목록 조회
    StudyPreviewDTO findStudies(Pageable pageable, StudySortBy sortBy);

    // 조건별 스터디 목록 조회
    StudyPreviewDTO findStudiesByConditions(Pageable pageable, StudySearchRequestDTO request, StudySortBy sortBy);

    // 내 추천 스터디 조회
    StudyPreviewDTO findRecommendStudies(Long memberId);

    // 회원별 관심 Best 스터디 3개 조회
    StudyPreviewDTO findInterestedStudies(Long memberId);

    // 내 관심사 스터디 페이징 조회
    StudyPreviewDTO findInterestStudiesByConditionsAll(Pageable pageable, Long memberId,
        StudySearchRequestDTO request, StudySortBy sortBy);

    // 내 특정 관심사 스터디 페이징 조회
    StudyPreviewDTO findInterestStudiesByConditionsSpecific(Pageable pageable, Long memberId, StudySearchRequestDTO request, ThemeType theme, StudySortBy sortBy);

    // 내 관심 지역 스터디 페이징 조회
    StudyPreviewDTO findInterestRegionStudiesByConditionsAll(
            Pageable pageable, Long memberId, StudySearchRequestWithThemeDTO request, StudySortBy sortBy);

    // 내 특정 관심 지역 스터디 페이징 조회
    StudyPreviewDTO findInterestRegionStudiesByConditionsSpecific(
            Pageable pageable, Long memberId, StudySearchRequestWithThemeDTO request, String regionCode, StudySortBy sortBy);

    // 모집 중 스터디 조회
    StudyPreviewDTO findRecruitingStudiesByConditions(
            Pageable pageable, StudySearchRequestWithThemeDTO request, StudySortBy sortBy);

    // 찜한 스터디 조회
    StudyPreviewDTO findLikedStudies(Long memberId, Pageable pageable);

    // 스터디 키워드 검색
    StudyPreviewDTO findStudiesByKeyword(Pageable pageable, String keyword, StudySortBy sortBy);

    // 테마 별 스터디 검색
    StudyPreviewDTO findStudiesByTheme(Pageable pageable, ThemeType theme, StudySortBy sortBy);

    // 내가 참여하고 있는 스터디 조회
    StudyPreviewDTO findOngoingStudiesByMemberId(Pageable pageable, Long memberId);

    // 내가 신청한 스터디 조회
    StudyPreviewDTO findAppliedStudies(Pageable pageable, Long memberId);

    // 내가 모집중인 스터디 조회
    StudyPreviewDTO findMyRecruitingStudies(Pageable pageable, Long memberId);

}
