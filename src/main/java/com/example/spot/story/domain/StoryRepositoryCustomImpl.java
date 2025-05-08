package com.example.spot.story.domain;

import com.example.spot.refactor.story.domain.QStory;
import com.example.spot.story.domain.enums.StoryCategory;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Pageable;

import java.util.List;

@RequiredArgsConstructor
public class StoryRepositoryCustomImpl implements StoryRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Story> findAnnouncementsByStudyId(Long studyId, Pageable pageable) {
        QStory studyPost = QStory.story;
        return queryFactory.selectFrom(studyPost)
                .where(studyPost.study.id.eq(studyId))
                .where(studyPost.isAnnouncement.eq(true))
                .orderBy(studyPost.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
    }

    @Override
    public List<Story> findAllByStudyIdAndTheme(Long studyId, StoryCategory storyCategory, Pageable pageable) {

        QStory story = QStory.story;
        return queryFactory.selectFrom(story)
                .where(story.study.id.eq(studyId))  // studyId가 일치하는지 확인
                .where(story.storyCategory.eq(storyCategory))       // category가 일치하는지 확인
                .orderBy(story.createdAt.desc())    // 최신순 정렬
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
    }

    @Override
    public List<Story> findAllByStudyId(Long studyId, Pageable pageable) {

        QStory studyPost = QStory.story;
        return queryFactory.selectFrom(studyPost)
                .where(studyPost.study.id.eq(studyId))  // studyId가 일치하는지 확인
                .orderBy(studyPost.createdAt.desc())    // 최신순 정렬
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
    }
}
