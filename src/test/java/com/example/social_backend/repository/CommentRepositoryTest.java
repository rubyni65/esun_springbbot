package com.example.social_backend.repository;

import com.example.social_backend.entity.Comment;
import com.example.social_backend.entity.Post;
import com.example.social_backend.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test") // 使用測試配置文件
class CommentRepositoryTest {

  @Autowired
  private CommentRepository commentRepository;

  @Autowired
  private PostRepository postRepository;

  @Autowired
  private UserRepository userRepository;

  private Long testUserId;
  private Long anotherUserId;
  private Long testPostId;
  private Long anotherPostId;

  @BeforeEach
  void setUp() {
    // 清空原有資料
    commentRepository.deleteAll();
    postRepository.deleteAll();
    userRepository.deleteAll();

    // 創建測試用戶
    User testUser = new User("1234567890", "測試用戶", "test@example.com", "password", null, null);
    User savedUser = userRepository.save(testUser);
    testUserId = savedUser.getUserId();

    User anotherUser = new User("9876543210", "另一用戶", "another@example.com", "password", null, null);
    User savedAnotherUser = userRepository.save(anotherUser);
    anotherUserId = savedAnotherUser.getUserId();

    // 創建測試發文
    Post testPost = new Post(testUserId, "測試發文內容", null);
    Post savedPost = postRepository.save(testPost);
    testPostId = savedPost.getPostId();

    Post anotherPost = new Post(anotherUserId, "另一篇發文內容", null);
    Post savedAnotherPost = postRepository.save(anotherPost);
    anotherPostId = savedAnotherPost.getPostId();
  }

  @Test
  void save_validComment_storesInDatabase() {
    // 準備
    Comment comment = new Comment(testUserId, testPostId, "測試留言內容");

    // 執行
    Comment savedComment = commentRepository.save(comment);

    // 驗證
    assertNotNull(savedComment.getCommentId());
    assertEquals(testUserId, savedComment.getUserId());
    assertEquals(testPostId, savedComment.getPostId());
    assertEquals("測試留言內容", savedComment.getContent());
    assertNotNull(savedComment.getCreatedAt());
  }

  @Test
  void findById_existingComment_returnsComment() {
    // 準備
    Comment comment = new Comment(testUserId, testPostId, "測試留言內容");
    Comment savedComment = commentRepository.save(comment);

    // 執行
    Optional<Comment> foundComment = commentRepository.findById(savedComment.getCommentId());

    // 驗證
    assertTrue(foundComment.isPresent());
    assertEquals(savedComment.getCommentId(), foundComment.get().getCommentId());
    assertEquals("測試留言內容", foundComment.get().getContent());
  }

  @Test
  void findById_nonExistingComment_returnsEmpty() {
    // 執行
    Optional<Comment> foundComment = commentRepository.findById(999L);

    // 驗證
    assertFalse(foundComment.isPresent());
  }

  @Test
  void update_existingComment_updatesValues() {
    // 準備
    Comment comment = new Comment(testUserId, testPostId, "原始留言內容");
    Comment savedComment = commentRepository.save(comment);
    Long commentId = savedComment.getCommentId();

    // 執行 - 更新留言
    savedComment.setContent("更新後的留言內容");
    commentRepository.save(savedComment);

    // 驗證
    Optional<Comment> updatedCommentOpt = commentRepository.findById(commentId);
    assertTrue(updatedCommentOpt.isPresent());
    Comment updatedComment = updatedCommentOpt.get();
    assertEquals("更新後的留言內容", updatedComment.getContent());
    assertEquals(testUserId, updatedComment.getUserId()); // 用戶ID保持不變
    assertEquals(testPostId, updatedComment.getPostId()); // 發文ID保持不變
  }

  @Test
  void delete_existingComment_removesFromDatabase() {
    // 準備
    Comment comment = new Comment(testUserId, testPostId, "將被刪除的留言");
    Comment savedComment = commentRepository.save(comment);
    Long commentId = savedComment.getCommentId();

    // 驗證儲存成功
    assertTrue(commentRepository.findById(commentId).isPresent());

    // 執行
    commentRepository.delete(savedComment);

    // 驗證
    assertFalse(commentRepository.findById(commentId).isPresent());
  }

  @Test
  void findByPostIdOrderByCreatedAtAsc_multipleCommentsWithDifferentTimes_returnsOrderedComments() {
    // 準備 - 創建具有不同創建時間的留言
    Comment oldComment = new Comment(testUserId, testPostId, "較早的留言");
    oldComment.setCreatedAt(LocalDateTime.now().minusHours(2));
    commentRepository.save(oldComment);

    Comment newComment = new Comment(testUserId, testPostId, "較新的留言");
    newComment.setCreatedAt(LocalDateTime.now());
    commentRepository.save(newComment);

    // 執行
    List<Comment> comments = commentRepository.findByPostIdOrderByCreatedAtAsc(testPostId);

    // 驗證 - 應該按創建時間升序排序
    assertFalse(comments.isEmpty());
    assertTrue(comments.get(0).getCreatedAt().isBefore(comments.get(1).getCreatedAt())); // 第一個是最早的
    assertEquals("較早的留言", comments.get(0).getContent());
    assertEquals("較新的留言", comments.get(1).getContent());
  }

  @Test
  void findByUserIdOrderByCreatedAtDesc_commentsFromSpecificUser_returnsUserComments() {
    // 準備 - 創建兩個用戶的留言
    Comment userComment1 = new Comment(testUserId, testPostId, "用戶1的留言1");
    Comment userComment2 = new Comment(testUserId, anotherPostId, "用戶1的留言2");
    Comment anotherUserComment = new Comment(anotherUserId, testPostId, "用戶2的留言");

    commentRepository.save(userComment1);
    commentRepository.save(userComment2);
    commentRepository.save(anotherUserComment);

    // 執行 - 查詢第一個用戶的留言
    List<Comment> userComments = commentRepository.findByUserIdOrderByCreatedAtDesc(testUserId);

    // 驗證 - 只應返回第一個用戶的留言
    assertEquals(2, userComments.size());
    for (Comment comment : userComments) {
      assertEquals(testUserId, comment.getUserId());
    }
    assertTrue(userComments.stream().anyMatch(c -> c.getContent().equals("用戶1的留言1")));
    assertTrue(userComments.stream().anyMatch(c -> c.getContent().equals("用戶1的留言2")));
  }

  @Test
  void findByPostIdOrderByCreatedAtAsc_commentsForSpecificPost_returnsPostComments() {
    // 準備 - 創建對不同發文的留言
    Comment postComment1 = new Comment(testUserId, testPostId, "發文1的留言1");
    Comment postComment2 = new Comment(anotherUserId, testPostId, "發文1的留言2");
    Comment anotherPostComment = new Comment(testUserId, anotherPostId, "發文2的留言");

    commentRepository.save(postComment1);
    commentRepository.save(postComment2);
    commentRepository.save(anotherPostComment);

    // 執行 - 查詢第一個發文的留言
    List<Comment> postComments = commentRepository.findByPostIdOrderByCreatedAtAsc(testPostId);

    // 驗證 - 只應返回第一個發文的留言
    assertEquals(2, postComments.size());
    for (Comment comment : postComments) {
      assertEquals(testPostId, comment.getPostId());
    }
    assertTrue(postComments.stream().anyMatch(c -> c.getContent().equals("發文1的留言1")));
    assertTrue(postComments.stream().anyMatch(c -> c.getContent().equals("發文1的留言2")));
  }
}