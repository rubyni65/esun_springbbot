package com.example.social_backend.service;

import com.example.social_backend.entity.Comment;
import com.example.social_backend.entity.Post;
import com.example.social_backend.repository.PostRepository;
import com.example.social_backend.util.SanitizerUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PostService {

  private final PostRepository postRepository;
  private final SanitizerUtil sanitizerUtil;
  private final JdbcTemplate jdbcTemplate;

  @Autowired
  public PostService(PostRepository postRepository, SanitizerUtil sanitizerUtil, JdbcTemplate jdbcTemplate) {
    this.postRepository = postRepository;
    this.sanitizerUtil = sanitizerUtil;
    this.jdbcTemplate = jdbcTemplate;
  }

  /**
   * 創建新發文
   *
   * @param userId  用戶ID
   * @param content 發文內容
   * @param image   圖片URL (可選)
   * @return 已創建的發文
   */
  @Transactional
  public Post createPost(Long userId, String content, String image) {
    // 驗證輸入
    if (userId == null) {
      throw new IllegalArgumentException("用戶ID不能為空");
    }

    if (content == null || content.isEmpty()) {
      throw new IllegalArgumentException("發文內容不能為空");
    }

    if (content.length() > 500) {
      throw new IllegalArgumentException("發文內容不能超過500字");
    }

    // 淨化輸入以防範XSS攻擊
    String sanitizedContent = sanitizerUtil.sanitize(content);
    String sanitizedImage = sanitizerUtil.sanitizeUrl(image);

    // 創建並保存發文
    Post post = new Post(userId, sanitizedContent, sanitizedImage);
    return postRepository.save(post);
  }

  /**
   * 創建發文和留言（使用事務保證原子性）
   *
   * @param userId         用戶ID
   * @param postContent    發文內容
   * @param postImage      發文圖片URL (可選)
   * @param commentContent 留言內容
   * @return 包含創建的發文ID和留言ID的Map
   */
  @Transactional
  public Map<String, Long> createPostAndComment(Long userId, String postContent, String postImage,
      String commentContent) {
    // 驗證輸入
    if (userId == null) {
      throw new IllegalArgumentException("用戶ID不能為空");
    }

    if (postContent == null || postContent.isEmpty()) {
      throw new IllegalArgumentException("發文內容不能為空");
    }

    if (postContent.length() > 500) {
      throw new IllegalArgumentException("發文內容不能超過500字");
    }

    if (commentContent == null || commentContent.isEmpty()) {
      throw new IllegalArgumentException("留言內容不能為空");
    }

    if (commentContent.length() > 500) {
      throw new IllegalArgumentException("留言內容不能超過500字");
    }

    // 淨化輸入以防範XSS攻擊
    String sanitizedPostContent = sanitizerUtil.sanitize(postContent);
    String sanitizedPostImage = sanitizerUtil.sanitizeUrl(postImage);
    String sanitizedCommentContent = sanitizerUtil.sanitize(commentContent);

    // 使用單一事務執行SQL操作
    // 步驟1：插入發文
    jdbcTemplate.update(
        "INSERT INTO post (user_id, content, image, created_at) VALUES (?, ?, ?, ?)",
        userId, sanitizedPostContent, sanitizedPostImage, LocalDateTime.now());

    // 步驟2：獲取最近插入的發文ID
    Long postId = jdbcTemplate.queryForObject("SELECT last_insert_rowid()", Long.class);

    // 步驟3：插入留言
    jdbcTemplate.update(
        "INSERT INTO comment (user_id, post_id, content, created_at) VALUES (?, ?, ?, ?)",
        userId, postId, sanitizedCommentContent, LocalDateTime.now());

    // 步驟4：獲取最近插入的留言ID
    Long commentId = jdbcTemplate.queryForObject("SELECT last_insert_rowid()", Long.class);

    // 返回創建的ID
    Map<String, Long> result = new HashMap<>();
    result.put("postId", postId);
    result.put("commentId", commentId);
    return result;
  }

  /**
   * 獲取所有發文（按創建時間降序排序）
   *
   * @return 發文列表
   */
  public List<Post> getAllPosts() {
    return postRepository.findAllByOrderByCreatedAtDesc();
  }

  /**
   * 獲取特定用戶的所有發文（按創建時間降序排序）
   *
   * @param userId 用戶ID
   * @return 發文列表
   */
  public List<Post> getPostsByUserId(Long userId) {
    return postRepository.findByUserIdOrderByCreatedAtDesc(userId);
  }

  /**
   * 通過ID獲取發文
   *
   * @param postId 發文ID
   * @return 發文
   */
  public Post getPostById(Long postId) {
    return postRepository.findById(postId)
        .orElseThrow(() -> new IllegalArgumentException("找不到ID為 " + postId + " 的發文"));
  }

  /**
   * 更新發文
   *
   * @param userId  請求用戶ID（用於權限檢查）
   * @param postId  要更新的發文ID
   * @param content 新內容
   * @param image   新圖片URL
   * @return 更新後的發文
   */
  @Transactional
  public Post updatePost(Long userId, Long postId, String content, String image) {
    // 獲取發文
    Post post = getPostById(postId);

    // 權限檢查
    if (!post.getUserId().equals(userId)) {
      throw new IllegalArgumentException("您沒有權限編輯此發文");
    }

    // 驗證內容
    if (content == null || content.isEmpty()) {
      throw new IllegalArgumentException("發文內容不能為空");
    }

    if (content.length() > 500) {
      throw new IllegalArgumentException("發文內容不能超過500字");
    }

    // 淨化輸入以防範XSS攻擊
    String sanitizedContent = sanitizerUtil.sanitize(content);
    String sanitizedImage = sanitizerUtil.sanitizeUrl(image);

    // 更新發文
    post.setContent(sanitizedContent);
    post.setImage(sanitizedImage);

    // 保存並返回更新後的發文
    return postRepository.save(post);
  }

  /**
   * 刪除發文
   *
   * @param userId 請求用戶ID（用於權限檢查）
   * @param postId 要刪除的發文ID
   */
  @Transactional
  public void deletePost(Long userId, Long postId) {
    // 獲取發文
    Post post = getPostById(postId);

    // 權限檢查
    if (!post.getUserId().equals(userId)) {
      throw new IllegalArgumentException("您沒有權限刪除此發文");
    }

    // 刪除發文
    postRepository.delete(post);
  }
}