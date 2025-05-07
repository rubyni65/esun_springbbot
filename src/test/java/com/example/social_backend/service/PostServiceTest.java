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
}