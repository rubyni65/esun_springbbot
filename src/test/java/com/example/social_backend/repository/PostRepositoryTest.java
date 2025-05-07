package com.example.social_backend.repository;

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
class PostRepositoryTest {

  @Autowired
  private PostRepository postRepository;

  @Autowired
  private UserRepository userRepository;

  private Long testUserId;
  private Long anotherUserId;

  @BeforeEach
  void setUp() {
    // 清空原有資料
    postRepository.deleteAll();
    userRepository.deleteAll();

    // 創建測試用戶
    User testUser = new User("1234567890", "測試用戶", "test@example.com", "password", null, null);
    User savedUser = userRepository.save(testUser);
    testUserId = savedUser.getUserId();

    User anotherUser = new User("9876543210", "另一用戶", "another@example.com", "password", null, null);
    User savedAnotherUser = userRepository.save(anotherUser);
    anotherUserId = savedAnotherUser.getUserId();
  }

  @Test
  void save_validPost_storesInDatabase() {
    // 準備
    Post post = new Post(testUserId, "測試發文內容", "http://example.com/image.jpg");

    // 執行
    Post savedPost = postRepository.save(post);

    // 驗證
    assertNotNull(savedPost.getPostId());
    assertEquals(testUserId, savedPost.getUserId());
    assertEquals("測試發文內容", savedPost.getContent());
    assertEquals("http://example.com/image.jpg", savedPost.getImage());
    assertNotNull(savedPost.getCreatedAt());
  }

  @Test
  void findById_existingPost_returnsPost() {
    // 準備
    Post post = new Post(testUserId, "測試發文內容", "http://example.com/image.jpg");
    Post savedPost = postRepository.save(post);

    // 執行
    Optional<Post> foundPost = postRepository.findById(savedPost.getPostId());

    // 驗證
    assertTrue(foundPost.isPresent());
    assertEquals(savedPost.getPostId(), foundPost.get().getPostId());
    assertEquals("測試發文內容", foundPost.get().getContent());
  }

  @Test
  void findById_nonExistingPost_returnsEmpty() {
    // 執行
    Optional<Post> foundPost = postRepository.findById(999L);

    // 驗證
    assertFalse(foundPost.isPresent());
  }

  @Test
  void update_existingPost_updatesValues() {
    // 準備
    Post post = new Post(testUserId, "原始發文內容", "http://example.com/original.jpg");
    Post savedPost = postRepository.save(post);
    Long postId = savedPost.getPostId();

    // 執行 - 更新發文
    savedPost.setContent("更新後的發文內容");
    savedPost.setImage("http://example.com/updated.jpg");
    postRepository.save(savedPost);

    // 驗證
    Optional<Post> updatedPostOpt = postRepository.findById(postId);
    assertTrue(updatedPostOpt.isPresent());
    Post updatedPost = updatedPostOpt.get();
    assertEquals("更新後的發文內容", updatedPost.getContent());
    assertEquals("http://example.com/updated.jpg", updatedPost.getImage());
    assertEquals(testUserId, updatedPost.getUserId()); // 用戶ID保持不變
  }

  @Test
  void delete_existingPost_removesFromDatabase() {
    // 準備
    Post post = new Post(testUserId, "將被刪除的發文", null);
    Post savedPost = postRepository.save(post);
    Long postId = savedPost.getPostId();

    // 驗證儲存成功
    assertTrue(postRepository.findById(postId).isPresent());

    // 執行
    postRepository.delete(savedPost);

    // 驗證
    assertFalse(postRepository.findById(postId).isPresent());
  }

  @Test
  void findAllByOrderByCreatedAtDesc_multiplePostsWithDifferentTimes_returnsOrderedPosts() {
    // 準備 - 創建具有不同創建時間的發文
    Post oldPost = new Post(testUserId, "較早的發文", null);
    oldPost.setCreatedAt(LocalDateTime.now().minusDays(2));
    postRepository.save(oldPost);

    Post newPost = new Post(testUserId, "較新的發文", null);
    newPost.setCreatedAt(LocalDateTime.now());
    postRepository.save(newPost);

    // 執行
    List<Post> posts = postRepository.findAllByOrderByCreatedAtDesc();

    // 驗證 - 應該按創建時間降序排序
    assertFalse(posts.isEmpty());
    assertTrue(posts.get(0).getCreatedAt().isAfter(posts.get(1).getCreatedAt())); // 第一個是最新的
    assertEquals("較新的發文", posts.get(0).getContent());
    assertEquals("較早的發文", posts.get(1).getContent());
  }

  @Test
  void findByUserIdOrderByCreatedAtDesc_postsFromSpecificUser_returnsUserPosts() {
    // 準備 - 創建兩個用戶的發文
    Post userPost1 = new Post(testUserId, "用戶1的發文1", null);
    Post userPost2 = new Post(testUserId, "用戶1的發文2", null);
    Post anotherUserPost = new Post(anotherUserId, "用戶2的發文", null);

    postRepository.save(userPost1);
    postRepository.save(userPost2);
    postRepository.save(anotherUserPost);

    // 執行 - 查詢第一個用戶的發文
    List<Post> userPosts = postRepository.findByUserIdOrderByCreatedAtDesc(testUserId);

    // 驗證 - 只應返回第一個用戶的發文
    assertEquals(2, userPosts.size());
    for (Post post : userPosts) {
      assertEquals(testUserId, post.getUserId());
    }
    assertTrue(userPosts.stream().anyMatch(p -> p.getContent().equals("用戶1的發文1")));
    assertTrue(userPosts.stream().anyMatch(p -> p.getContent().equals("用戶1的發文2")));
  }
}