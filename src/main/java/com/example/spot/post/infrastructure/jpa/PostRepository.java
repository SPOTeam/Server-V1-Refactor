package com.example.spot.post.infrastructure.jpa;

import com.example.spot.post.domain.Post;
import com.example.spot.post.domain.enums.Board;
import com.example.spot.post.infrastructure.querydsl.PostRepositoryCustom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PostRepository extends JpaRepository<Post, Long>, PostRepositoryCustom {

    @Query(value = """
              select p
              from Post p
              where not exists (
                select 1 from PostReport r
                where r.post = p
              )
              order by p.createdAt desc
            """, countQuery = """
              select count(p)
              from Post p
              where not exists (
                select 1 from PostReport r
                where r.post = p
              )
            """)
    Page<Post> findPostsWithoutReport(Pageable pageable);

    @Query(value = """
              select p
              from Post p
              where p.board = :board
                and not exists (
                  select 1 from PostReport r
                  where r.post = p
                )
              order by p.createdAt desc
            """, countQuery = """
              select count(p)
              from Post p
              where p.board = :board
                and not exists (
                  select 1 from PostReport r
                  where r.post = p
                )
            """)
    Page<Post> findPostsWithoutReportByBoard(@Param("board") Board board, Pageable pageable);
}
