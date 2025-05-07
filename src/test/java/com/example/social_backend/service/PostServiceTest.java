package com.example.social_backend.service;

import com.example.social_backend.entity.Post;
import com.example.social_backend.repository.PostRepository;
import com.example.social_backend.util.SanitizerUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.jdbc.core.JdbcTemplate;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class PostServiceTest {

  @Mock
  private PostRepository postRepository;

  @Mock
  private SanitizerUtil sanitizerUtil;

  @Mock
  private JdbcTemplate jdbcTemplate;

  @InjectMocks
  private PostService postService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);

    // 配置SanitizerUtil模擬
    when(sanitizerUtil.sanitize(anyString())).thenAnswer(invocation -> invocation.getArgument(0));
    when(sanitizerUtil.sanitizeUrl(anyString())).thenAnswer(invocation -> invocation.getArgument(0));
  }

  @Test
  void createPost_validInputs_returnsCreatedPost() {
    // 準備
    Long userId = 1L;
    String content = "這是一篇測試發文";
    String image = "http://example.com/image.jpg";

    Post savedPost = new Post(userId, content, image);
    savedPost.setPostId(1L);
    savedPost.setCreatedAt(LocalDateTime.now());

    when(postRepository.save(any(Post.class))).thenReturn(savedPost);

    // 執行
    Post result = postService.createPost(userId, content, image);

    // 驗證
    assertNotNull(result);
    assertEquals(1L, result.getPostId());
    assertEquals(userId, result.getUserId());
    assertEquals(content, result.getContent());
    assertEquals(image, result.getImage());
    assertNotNull(result.getCreatedAt());

    verify(sanitizerUtil, times(1)).sanitize(content);
    verify(sanitizerUtil, times(1)).sanitizeUrl(image);
    verify(postRepository, times(1)).save(any(Post.class));
  }

  @Test
  void createPost_nullUserId_throwsException() {
    // 執行和驗證
    Exception exception = assertThrows(IllegalArgumentException.class, () -> {
      postService.createPost(null, "這是一篇測試發文", "http://example.com/image.jpg");
    });

    assertEquals("用戶ID不能為空", exception.getMessage());
    verify(postRepository, never()).save(any(Post.class));
  }

  @Test
  void createPost_emptyContent_throwsException() {
    // 執行和驗證
    Exception exception = assertThrows(IllegalArgumentException.class, () -> {
      postService.createPost(1L, "", "http://example.com/image.jpg");
    });

    assertEquals("發文內容不能為空", exception.getMessage());
    verify(postRepository, never()).save(any(Post.class));
  }

  @Test
  void createPost_contentTooLong_throwsException() {
    // 準備
    String tooLongContent = "a".repeat(501); // 501個字符

    // 執行和驗證
    Exception exception = assertThrows(IllegalArgumentException.class, () -> {
      postService.createPost(1L, tooLongContent, "http://example.com/image.jpg");
    });

    assertEquals("發文內容不能超過500字", exception.getMessage());
    verify(postRepository, never()).save(any(Post.class));
  }

  @Test
  void getAllPosts_returnsAllPostsSortedByCreatedAtDesc() {
    // 準備
    LocalDateTime now = LocalDateTime.now();
    Post post1 = new Post(1L, "發文1", "image1.jpg");
    post1.setPostId(1L);
    post1.setCreatedAt(now.minusDays(1));

    Post post2 = new Post(1L, "發文2", "image2.jpg");
    post2.setPostId(2L);
    post2.setCreatedAt(now);

    List<Post> expectedPosts = Arrays.asList(post2, post1); // 按創建時間降序排序

    when(postRepository.findAllByOrderByCreatedAtDesc()).thenReturn(expectedPosts);

    // 執行
    List<Post> result = postService.getAllPosts();

    // 驗證
    assertEquals(2, result.size());
    assertEquals(post2, result.get(0)); // 最新的發文應該在前面
    assertEquals(post1, result.get(1));

    verify(postRepository, times(1)).findAllByOrderByCreatedAtDesc();
  }

  @Test
  void createPostAndComment_validInputs_returnsIds() {
    // 準備
    Long userId = 1L;
    String postContent = "發文內容";
    String postImage = "image.jpg";
    String commentContent = "留言內容";

    Long postId = 1L;
    Long commentId = 1L;

    when(jdbcTemplate.update(anyString(), any(), any(), any(), any())).thenReturn(1);
    when(jdbcTemplate.queryForObject(eq("SELECT last_insert_rowid()"), eq(Long.class))).thenReturn(postId, commentId);

    // 執行
    Map<String, Long> result = postService.createPostAndComment(userId, postContent, postImage, commentContent);

    // 驗證
    assertNotNull(result);
    assertEquals(postId, result.get("postId"));
    assertEquals(commentId, result.get("commentId"));

    verify(sanitizerUtil, times(1)).sanitize(postContent);
    verify(sanitizerUtil, times(1)).sanitizeUrl(postImage);
    verify(sanitizerUtil, times(1)).sanitize(commentContent);
    verify(jdbcTemplate, times(2)).update(anyString(), any(), any(), any(), any());
    verify(jdbcTemplate, times(2)).queryForObject(eq("SELECT last_insert_rowid()"), eq(Long.class));
  }

  @Test
  void getPostById_existingPost_returnsPost() {
    // 準備
    Long postId = 1L;
    Post expectedPost = new Post(1L, "發文內容", "image.jpg");
    expectedPost.setPostId(postId);

    when(postRepository.findById(postId)).thenReturn(Optional.of(expectedPost));

    // 執行
    Post result = postService.getPostById(postId);

    // 驗證
    assertNotNull(result);
    assertEquals(postId, result.getPostId());
    verify(postRepository, times(1)).findById(postId);
  }

  @Test
  void getPostById_nonExistingPost_throwsException() {
    // 準備
    Long postId = 999L;
    when(postRepository.findById(postId)).thenReturn(Optional.empty());

    // 執行和驗證
    Exception exception = assertThrows(IllegalArgumentException.class, () -> {
      postService.getPostById(postId);
    });

    assertEquals("找不到ID為 " + postId + " 的發文", exception.getMessage());
    verify(postRepository, times(1)).findById(postId);
  }

  @Test
  void updatePost_ownerUser_updatesPost() {
    // 準備
    Long userId = 1L;
    Long postId = 1L;
    String newContent = "更新後的內容";
    String newImage = "new-image.jpg";

    Post existingPost = new Post(userId, "原始內容", "old-image.jpg");
    existingPost.setPostId(postId);

    Post updatedPost = new Post(userId, newContent, newImage);
    updatedPost.setPostId(postId);

    when(postRepository.findById(postId)).thenReturn(Optional.of(existingPost));
    when(postRepository.save(any(Post.class))).thenReturn(updatedPost);

    // 執行
    Post result = postService.updatePost(userId, postId, newContent, newImage);

    // 驗證
    assertNotNull(result);
    assertEquals(postId, result.getPostId());
    assertEquals(userId, result.getUserId()); // 用戶ID不變
    assertEquals(newContent, result.getContent());
    assertEquals(newImage, result.getImage());

    verify(postRepository, times(1)).findById(postId);
    verify(sanitizerUtil, times(1)).sanitize(newContent);
    verify(sanitizerUtil, times(1)).sanitizeUrl(newImage);
    verify(postRepository, times(1)).save(any(Post.class));
  }

  @Test
  void updatePost_nonOwnerUser_throwsException() {
    // 準備
    Long ownerId = 1L;
    Long nonOwnerId = 2L; // 不同的用戶ID
    Long postId = 1L;

    Post existingPost = new Post(ownerId, "原始內容", "image.jpg");
    existingPost.setPostId(postId);

    when(postRepository.findById(postId)).thenReturn(Optional.of(existingPost));

    // 執行和驗證
    Exception exception = assertThrows(IllegalArgumentException.class, () -> {
      postService.updatePost(nonOwnerId, postId, "新內容", "new-image.jpg");
    });

    assertEquals("您沒有權限編輯此發文", exception.getMessage());
    verify(postRepository, times(1)).findById(postId);
    verify(postRepository, never()).save(any(Post.class));
  }

  @Test
  void updatePost_emptyContent_throwsException() {
    // 準備
    Long userId = 1L;
    Long postId = 1L;
    String emptyContent = "";

    Post existingPost = new Post(userId, "原始內容", "image.jpg");
    existingPost.setPostId(postId);

    when(postRepository.findById(postId)).thenReturn(Optional.of(existingPost));

    // 執行和驗證
    Exception exception = assertThrows(IllegalArgumentException.class, () -> {
      postService.updatePost(userId, postId, emptyContent, "new-image.jpg");
    });

    assertEquals("發文內容不能為空", exception.getMessage());
    verify(postRepository, times(1)).findById(postId);
    verify(postRepository, never()).save(any(Post.class));
  }

  @Test
  void updatePost_contentTooLong_throwsException() {
    // 準備
    Long userId = 1L;
    Long postId = 1L;
    String tooLongContent = "a".repeat(501); // 501個字符

    Post existingPost = new Post(userId, "原始內容", "image.jpg");
    existingPost.setPostId(postId);

    when(postRepository.findById(postId)).thenReturn(Optional.of(existingPost));

    // 執行和驗證
    Exception exception = assertThrows(IllegalArgumentException.class, () -> {
      postService.updatePost(userId, postId, tooLongContent, "new-image.jpg");
    });

    assertEquals("發文內容不能超過500字", exception.getMessage());
    verify(postRepository, times(1)).findById(postId);
    verify(postRepository, never()).save(any(Post.class));
  }

  @Test
  void deletePost_ownerUser_deletesPost() {
    // 準備
    Long userId = 1L;
    Long postId = 1L;

    Post existingPost = new Post(userId, "將被刪除的內容", "image.jpg");
    existingPost.setPostId(postId);

    when(postRepository.findById(postId)).thenReturn(Optional.of(existingPost));
    doNothing().when(postRepository).delete(existingPost);

    // 執行
    postService.deletePost(userId, postId);

    // 驗證
    verify(postRepository, times(1)).findById(postId);
    verify(postRepository, times(1)).delete(existingPost);
  }

  @Test
  void deletePost_nonOwnerUser_throwsException() {
    // 準備
    Long ownerId = 1L;
    Long nonOwnerId = 2L; // 不同的用戶ID
    Long postId = 1L;

    Post existingPost = new Post(ownerId, "內容", "image.jpg");
    existingPost.setPostId(postId);

    when(postRepository.findById(postId)).thenReturn(Optional.of(existingPost));

    // 執行和驗證
    Exception exception = assertThrows(IllegalArgumentException.class, () -> {
      postService.deletePost(nonOwnerId, postId);
    });

    assertEquals("您沒有權限刪除此發文", exception.getMessage());
    verify(postRepository, times(1)).findById(postId);
    verify(postRepository, never()).delete(any(Post.class));
  }

  @Test
  void deletePost_nonExistingPost_throwsException() {
    // 準備
    Long userId = 1L;
    Long postId = 999L;

    when(postRepository.findById(postId)).thenReturn(Optional.empty());

    // 執行和驗證
    Exception exception = assertThrows(IllegalArgumentException.class, () -> {
      postService.deletePost(userId, postId);
    });

    assertEquals("找不到ID為 " + postId + " 的發文", exception.getMessage());
    verify(postRepository, times(1)).findById(postId);
    verify(postRepository, never()).delete(any(Post.class));
  }

  @Test
  void getPostsByUserId_existingUser_returnsUserPosts() {
    // 準備
    Long userId = 1L;

    LocalDateTime now = LocalDateTime.now();
    Post post1 = new Post(userId, "用戶的發文1", "image1.jpg");
    post1.setPostId(1L);
    post1.setCreatedAt(now.minusDays(1));

    Post post2 = new Post(userId, "用戶的發文2", "image2.jpg");
    post2.setPostId(2L);
    post2.setCreatedAt(now);

    List<Post> expectedPosts = Arrays.asList(post2, post1); // 按創建時間降序排序

    when(postRepository.findByUserIdOrderByCreatedAtDesc(userId)).thenReturn(expectedPosts);

    // 執行
    List<Post> result = postService.getPostsByUserId(userId);

    // 驗證
    assertNotNull(result);
    assertEquals(2, result.size());
    assertEquals(post2, result.get(0)); // 最新的發文應該在前面
    assertEquals(post1, result.get(1));

    // 檢查所有返回的發文都屬於指定用戶
    for (Post post : result) {
      assertEquals(userId, post.getUserId());
    }

    verify(postRepository, times(1)).findByUserIdOrderByCreatedAtDesc(userId);
  }

  @Test
  void getPostsByUserId_userWithNoPosts_returnsEmptyList() {
    // 準備
    Long userId = 2L; // 沒有發文的用戶

    when(postRepository.findByUserIdOrderByCreatedAtDesc(userId)).thenReturn(List.of());

    // 執行
    List<Post> result = postService.getPostsByUserId(userId);

    // 驗證
    assertNotNull(result);
    assertTrue(result.isEmpty());
    verify(postRepository, times(1)).findByUserIdOrderByCreatedAtDesc(userId);
  }

  @Test
  void getPostsByUserId_multipleUsers_returnsOnlySpecifiedUserPosts() {
    // 準備
    Long userId1 = 1L;
    Long userId2 = 2L;

    // 第一個用戶的發文
    Post post1User1 = new Post(userId1, "用戶1的發文1", null);
    post1User1.setPostId(1L);
    post1User1.setCreatedAt(LocalDateTime.now().minusDays(1));

    Post post2User1 = new Post(userId1, "用戶1的發文2", null);
    post2User1.setPostId(2L);
    post2User1.setCreatedAt(LocalDateTime.now());

    // 第二個用戶的發文
    Post post1User2 = new Post(userId2, "用戶2的發文", null);
    post1User2.setPostId(3L);
    post1User2.setCreatedAt(LocalDateTime.now().minusHours(5));

    // 模擬第一個用戶的發文查詢結果
    List<Post> user1Posts = Arrays.asList(post2User1, post1User1);
    when(postRepository.findByUserIdOrderByCreatedAtDesc(userId1)).thenReturn(user1Posts);

    // 模擬第二個用戶的發文查詢結果
    List<Post> user2Posts = List.of(post1User2);
    when(postRepository.findByUserIdOrderByCreatedAtDesc(userId2)).thenReturn(user2Posts);

    // 執行 - 查詢第一個用戶的發文
    List<Post> resultUser1 = postService.getPostsByUserId(userId1);

    // 驗證 - 只應返回第一個用戶的發文
    assertEquals(2, resultUser1.size());
    for (Post post : resultUser1) {
      assertEquals(userId1, post.getUserId());
    }

    // 執行 - 查詢第二個用戶的發文
    List<Post> resultUser2 = postService.getPostsByUserId(userId2);

    // 驗證 - 只應返回第二個用戶的發文
    assertEquals(1, resultUser2.size());
    assertEquals(userId2, resultUser2.get(0).getUserId());

    verify(postRepository, times(1)).findByUserIdOrderByCreatedAtDesc(userId1);
    verify(postRepository, times(1)).findByUserIdOrderByCreatedAtDesc(userId2);
  }
}