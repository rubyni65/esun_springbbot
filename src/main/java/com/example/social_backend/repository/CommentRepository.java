package com.example.social_backend.repository;

import com.example.social_backend.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
  /**
   * 查詢指定發文的所有留言並按創建時間升序排序
   * 
   * @param postId 發文ID
   * @return 留言列表
   */
  List<Comment> findByPostIdOrderByCreatedAtAsc(Long postId);

  /**
   * 查詢指定用戶的所有留言並按創建時間降序排序
   * 
   * @param userId 用戶ID
   * @return 留言列表
   */
  List<Comment> findByUserIdOrderByCreatedAtDesc(Long userId);
}