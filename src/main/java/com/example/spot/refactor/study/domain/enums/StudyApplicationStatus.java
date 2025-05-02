package com.example.spot.refactor.study.domain.enums;

public enum StudyApplicationStatus {
    APPLIED, // 신청 승인 대기
    APPROVED, // 신청 승인 완료
    AWAITING_SELF_APPROVAL, // 본인 승인 대기
    REJECTED, // 신청 거절
}
