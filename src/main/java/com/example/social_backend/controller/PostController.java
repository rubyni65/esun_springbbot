package com.example.social_backend.controller;

import com.example.social_backend.entity.Post;
import com.example.social_backend.service.PostService;
import com.example.social_backend.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/posts")
@CrossOrigin(origins = "*")
public class PostController {

  private final PostService postService;
  private final JwtUtil jwtUtil;

  @Autowired
  public PostController(PostService postService, JwtUtil jwtUtil) {
    this.postService = postService;
    this.jwtUtil = jwtUtil;
  }

  /**
   * 創建新發文
   *
   * @param token   JWT 令牌
   * @param request 請求體
   * @return 已創建的發文
   */
  @PostMapping
  public ResponseEntity<?> createPost(
      @RequestHeader(value = "Authorization", required = false) String token,
      @RequestBody Map<String, String> request) {

    try {
      if (token == null || token.isEmpty() || !token.startsWith("Bearer ")) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("未提供有效的認證令牌");
      }
      String actualToken = token.substring(7);
      Long userId;
      try {
        userId = jwtUtil.validateTokenAndGetUserId(actualToken);
      } catch (RuntimeException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("認證失敗：" + e.getMessage());
      }

      String content = request.get("content");
      String image = request.get("image");
      if (content == null || content.trim().isEmpty()) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("發文內容不可為空");
      }
      Post newPost = postService.createPost(userId, content, image);
      return ResponseEntity.status(HttpStatus.CREATED).body(newPost);
    } catch (IllegalArgumentException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    } catch (Exception e) {
      System.err.println("創建發文時發生內部錯誤: " + e.getMessage());
      e.printStackTrace();
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("創建發文時發生內部錯誤，請查看日誌");
    }
  }

  /**
   * 獲取所有發文
   *
   * @return 發文列表
   */
  @GetMapping
  public ResponseEntity<List<Post>> getAllPosts() {
    return ResponseEntity.ok(postService.getAllPosts());
  }

  /**
   * 獲取特定用戶的發文
   *
   * @param userId 用戶ID
   * @return 發文列表
   */
  @GetMapping("/user/{userId}")
  public ResponseEntity<List<Post>> getUserPosts(@PathVariable Long userId) {
    return ResponseEntity.ok(postService.getPostsByUserId(userId));
  }

  /**
   * 獲取單一發文
   *
   * @param postId 發文ID
   * @return 發文
   */
  @GetMapping("/{postId}")
  public ResponseEntity<?> getPost(@PathVariable Long postId) {
    try {
      Post post = postService.getPostById(postId);
      return ResponseEntity.ok(post);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }
  }

  /**
   * 更新發文
   *
   * @param token   JWT 令牌
   * @param postId  發文ID
   * @param request 請求體
   * @return 更新後的發文
   */
  @PutMapping("/{postId}")
  public ResponseEntity<?> updatePost(
      @RequestHeader(value = "Authorization", required = false) String token,
      @PathVariable Long postId,
      @RequestBody Map<String, String> request) {

    try {
      if (token == null || token.isEmpty() || !token.startsWith("Bearer ")) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("未提供有效的認證令牌");
      }
      String actualToken = token.substring(7);
      Long userId;
      try {
        userId = jwtUtil.validateTokenAndGetUserId(actualToken);
      } catch (RuntimeException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("認證失敗：" + e.getMessage());
      }

      String content = request.get("content");
      String image = request.get("image");
      if (content == null || content.trim().isEmpty()) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("發文內容不可為空");
      }

      Post updatedPost = postService.updatePost(userId, postId, content, image);
      return ResponseEntity.ok(updatedPost);
    } catch (IllegalArgumentException e) {
      // 用於權限或找不到發文的錯誤
      if (e.getMessage() != null && (e.getMessage().contains("無權") || e.getMessage().contains("not found"))) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
      }
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    } catch (Exception e) {
      System.err.println("更新發文時發生內部錯誤: " + e.getMessage());
      e.printStackTrace();
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("更新發文時發生內部錯誤，請查看日誌");
    }
  }

  /**
   * 刪除發文
   *
   * @param token  JWT 令牌
   * @param postId 發文ID
   * @return 刪除狀態
   */
  @DeleteMapping("/{postId}")
  public ResponseEntity<?> deletePost(
      @RequestHeader(value = "Authorization", required = false) String token,
      @PathVariable Long postId) {

    try {
      if (token == null || token.isEmpty() || !token.startsWith("Bearer ")) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("未提供有效的認證令牌");
      }
      String actualToken = token.substring(7);
      Long userId;
      try {
        userId = jwtUtil.validateTokenAndGetUserId(actualToken);
      } catch (RuntimeException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("認證失敗：" + e.getMessage());
      }

      postService.deletePost(userId, postId);
      Map<String, String> response = new HashMap<>();
      response.put("message", "發文已成功刪除");
      return ResponseEntity.ok(response);
    } catch (IllegalArgumentException e) {
      // 用於權限或找不到發文的錯誤
      if (e.getMessage() != null && (e.getMessage().contains("無權") || e.getMessage().contains("not found"))) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
      }
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    } catch (Exception e) {
      System.err.println("刪除發文時發生內部錯誤: " + e.getMessage());
      e.printStackTrace();
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("刪除發文時發生內部錯誤，請查看日誌");
    }
  }

  /**
   * 創建發文和留言（使用存儲過程事務）
   *
   * @param token   JWT 令牌
   * @param request 請求體
   * @return 已創建的發文和留言ID
   */
  @PostMapping("/with-comment")
  public ResponseEntity<?> createPostAndComment(
      @RequestHeader(value = "Authorization", required = false) String token,
      @RequestBody Map<String, String> request) {

    try {
      if (token == null || token.isEmpty() || !token.startsWith("Bearer ")) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("未提供有效的認證令牌");
      }
      String actualToken = token.substring(7);
      Long userId;
      try {
        userId = jwtUtil.validateTokenAndGetUserId(actualToken);
      } catch (RuntimeException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("認證失敗：" + e.getMessage());
      }

      String postContent = request.get("postContent");
      String postImage = request.get("postImage");
      String commentContent = request.get("commentContent");

      if (postContent == null || postContent.trim().isEmpty()) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("發文內容不可為空");
      }
      if (commentContent == null || commentContent.trim().isEmpty()) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("留言內容不可為空");
      }

      Map<String, Long> result = postService.createPostAndComment(userId, postContent, postImage, commentContent);
      return ResponseEntity.status(HttpStatus.CREATED).body(result);
    } catch (IllegalArgumentException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    } catch (Exception e) {
      System.err.println("創建發文和留言時發生內部錯誤: " + e.getMessage());
      e.printStackTrace();
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("創建發文和留言時發生內部錯誤，請查看日誌");
    }
  }
}