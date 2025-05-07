package com.example.social_backend.service;

import com.example.social_backend.entity.Comment;
import com.example.social_backend.entity.Post;
import com.example.social_backend.repository.CommentRepository;
import com.example.social_backend.repository.PostRepository;
import com.example.social_backend.util.SanitizerUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class CommentServiceTest {

  @Mock
  private CommentRepository commentRepository;

  @Mock
  private PostRepository postRepository;

  @Mock
  private SanitizerUtil sanitizerUtil;

  @InjectMocks
  private CommentService commentService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);

    // 配置SanitizerUtil模擬
    when(sanitizerUtil.sanitize(anyString())).thenAnswer(invocation -> invocation.getArgument(0));
  }

  @Test
  void createComment_validInputs_returnsCreatedComment() {
    // 準備
    Long userId = 1L;
    Long postId = 1L;
    String content = "這是一則測試留言";

    Post post = new Post(userId, "發文內容", null);
    post.setPostId(postId);

    Comment savedComment = new Comment(userId, postId, content);
    savedComment.setCommentId(1L);
    savedComment.setCreatedAt(LocalDateTime.now());

    when(postRepository.findById(postId)).thenReturn(Optional.of(post));
    when(commentRepository.save(any(Comment.class))).thenReturn(savedComment);

    // 執行
    Comment result = commentService.createComment(userId, postId, content);

    // 驗證
    assertNotNull(result);
    assertEquals(1L, result.getCommentId());
    assertEquals(userId, result.getUserId());
    assertEquals(postId, result.getPostId());
    assertEquals(content, result.getContent());
    assertNotNull(result.getCreatedAt());

    verify(sanitizerUtil, times(1)).sanitize(content);
    verify(postRepository, times(1)).findById(postId);
    verify(commentRepository, times(1)).save(any(Comment.class));
  }

  @Test
  void createComment_nullUserId_throwsException() {
    // 執行和驗證
    Exception exception = assertThrows(IllegalArgumentException.class, () -> {
      commentService.createComment(null, 1L, "這是一則測試留言");
    });

    assertEquals("用戶ID不能為空", exception.getMessage());
    verify(commentRepository, never()).save(any(Comment.class));
  }

  @Test
  void createComment_nullPostId_throwsException() {
    // 執行和驗證
    Exception exception = assertThrows(IllegalArgumentException.class, () -> {
      commentService.createComment(1L, null, "這是一則測試留言");
    });

    assertEquals("發文ID不能為空", exception.getMessage());
    verify(commentRepository, never()).save(any(Comment.class));
  }

  @Test
  void createComment_emptyContent_throwsException() {
    // 執行和驗證
    Exception exception = assertThrows(IllegalArgumentException.class, () -> {
      commentService.createComment(1L, 1L, "");
    });

    assertEquals("留言內容不能為空", exception.getMessage());
    verify(commentRepository, never()).save(any(Comment.class));
  }

  @Test
  void createComment_contentTooLong_throwsException() {
    // 準備
    String tooLongContent = "a".repeat(501); // 501個字符

    // 執行和驗證
    Exception exception = assertThrows(IllegalArgumentException.class, () -> {
      commentService.createComment(1L, 1L, tooLongContent);
    });

    assertEquals("留言內容不能超過500字", exception.getMessage());
    verify(commentRepository, never()).save(any(Comment.class));
  }

  @Test
  void createComment_postNotFound_throwsException() {
    // 準備
    Long postId = 1L;
    when(postRepository.findById(postId)).thenReturn(Optional.empty());

    // 執行和驗證
    Exception exception = assertThrows(IllegalArgumentException.class, () -> {
      commentService.createComment(1L, postId, "這是一則測試留言");
    });

    assertEquals("找不到ID為 " + postId + " 的發文", exception.getMessage());
    verify(postRepository, times(1)).findById(postId);
    verify(commentRepository, never()).save(any(Comment.class));
  }

  @Test
  void getCommentsByPostId_returnsCommentsForPost() {
    // 準備
    Long postId = 1L;

    Comment comment1 = new Comment(1L, postId, "留言1");
    comment1.setCommentId(1L);
    comment1.setCreatedAt(LocalDateTime.now().minusHours(1));

    Comment comment2 = new Comment(2L, postId, "留言2");
    comment2.setCommentId(2L);
    comment2.setCreatedAt(LocalDateTime.now());

    List<Comment> expectedComments = Arrays.asList(comment1, comment2);

    when(commentRepository.findByPostIdOrderByCreatedAtAsc(postId)).thenReturn(expectedComments);

    // 執行
    List<Comment> result = commentService.getCommentsByPostId(postId);

    // 驗證
    assertEquals(2, result.size());
    assertEquals(comment1, result.get(0));
    assertEquals(comment2, result.get(1));

    verify(commentRepository, times(1)).findByPostIdOrderByCreatedAtAsc(postId);
  }
}