package com.example.spot.refactor.post.domain;

import com.example.spot.refactor.post.domain.enums.Board;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long>, PostRepositoryCustom {
    // 정렬
     Page<Post> findByBoardAndPostReportListIsEmptyOrderByCreatedAtDesc(Board board, Pageable pageable);

    // 정렬 조건 필요
    Page<Post> findByPostReportListIsEmptyOrderByCreatedAtDesc(Pageable pageable);

}
