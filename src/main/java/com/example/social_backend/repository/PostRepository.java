package com.example.social_backend.repository;

import com.example.social_backend.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
  /**
   * 查詢所有發文並按創建時間降序排序
   * 
   * @return 發文列表
   */
  List<Post> findAllByOrderByCreatedAtDesc();

  /**
   * 查詢指定用戶的所有發文並按創建時間降序排序
   * 
   * @param userId 用戶ID
   * @return 發文列表
   */
  List<Post> findByUserIdOrderByCreatedAtDesc(Long userId);
}