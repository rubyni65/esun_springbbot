package com.example.social_backend.service;

import com.example.social_backend.entity.Comment;
import com.example.social_backend.entity.Post;
import com.example.social_backend.repository.CommentRepository;
import com.example.social_backend.repository.PostRepository;
import com.example.social_backend.util.SanitizerUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CommentService {

  private final CommentRepository commentRepository;
  private final PostRepository postRepository;
  private final SanitizerUtil sanitizerUtil;

  @Autowired
  public CommentService(CommentRepository commentRepository, PostRepository postRepository,
      SanitizerUtil sanitizerUtil) {
    this.commentRepository = commentRepository;
    this.postRepository = postRepository;
    this.sanitizerUtil = sanitizerUtil;
  }

  /**
   * 創建新留言
   *
   * @param userId  用戶ID
   * @param postId  發文ID
   * @param content 留言內容
   * @return 已創建的留言
   */
  @Transactional
  public Comment createComment(Long userId, Long postId, String content) {
    // 驗證輸入
    if (userId == null) {
      throw new IllegalArgumentException("用戶ID不能為空");
    }

    if (postId == null) {
      throw new IllegalArgumentException("發文ID不能為空");
    }

    if (content == null || content.isEmpty()) {
      throw new IllegalArgumentException("留言內容不能為空");
    }

    if (content.length() > 500) {
      throw new IllegalArgumentException("留言內容不能超過500字");
    }

    // 確認發文存在
    Post post = postRepository.findById(postId)
        .orElseThrow(() -> new IllegalArgumentException("找不到ID為 " + postId + " 的發文"));

    // 淨化輸入以防範XSS攻擊
    String sanitizedContent = sanitizerUtil.sanitize(content);

    // 創建並保存留言
    Comment comment = new Comment(userId, postId, sanitizedContent);
    return commentRepository.save(comment);
  }

  /**
   * 獲取指定發文的所有留言
   *
   * @param postId 發文ID
   * @return 留言列表
   */
  public List<Comment> getCommentsByPostId(Long postId) {
    return commentRepository.findByPostIdOrderByCreatedAtAsc(postId);
  }

  /**
   * 獲取指定用戶的所有留言
   *
   * @param userId 用戶ID
   * @return 留言列表
   */
  public List<Comment> getCommentsByUserId(Long userId) {
    return commentRepository.findByUserIdOrderByCreatedAtDesc(userId);
  }
}