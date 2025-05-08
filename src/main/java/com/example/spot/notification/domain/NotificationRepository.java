package com.example.spot.notification.domain;

import com.example.spot.notification.domain.enums.NotifyType;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    Optional<Notification> findByMemberIdAndStudyIdAndTypeAndIsChecked(Long memberId, Long studyId, NotifyType type, boolean isChecked);
    List<Notification> findByMemberIdAndTypeNot(Long memberId, Pageable pageable, NotifyType type);
    List<Notification> findByMemberIdAndTypeAndIsChecked(Long memberId, Pageable pageable, NotifyType type, boolean isChecked);

    List<Notification> findByType(NotifyType type);
}
