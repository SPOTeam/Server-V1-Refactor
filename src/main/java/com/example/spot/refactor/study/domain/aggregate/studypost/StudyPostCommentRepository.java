package com.example.spot.refactor.study.domain.aggregate.studypost;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudyPostCommentRepository extends JpaRepository<StudyPostComment, Long> {

    List<StudyPostComment> findAllByMemberIdAndStudyPostId(Long id, Long postId);

    List<StudyPostComment> findAllByStudyPostId(Long postId);

    void deleteAllByStudyPostId(Long postId);
}
