package com.example.social_backend.controller;

import com.example.social_backend.entity.Comment;
import com.example.social_backend.service.CommentService;
import com.example.social_backend.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/comments")
@CrossOrigin(origins = "*")
public class CommentController {

  private final CommentService commentService;
  private final JwtUtil jwtUtil;

  @Autowired
  public CommentController(CommentService commentService, JwtUtil jwtUtil) {
    this.commentService = commentService;
    this.jwtUtil = jwtUtil;
  }

  /**
   * 創建新留言
   *
   * @param token   JWT 令牌
   * @param request 請求體
   * @return 已創建的留言
   */
  @PostMapping
  public ResponseEntity<?> createComment(
      @RequestHeader(value = "Authorization", required = false) String token,
      @RequestBody Map<String, Object> request) {

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

      if (!request.containsKey("postId") || !request.containsKey("content")) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("請求必須包含postId和content");
      }

      Long postId = Long.parseLong(request.get("postId").toString());
      String content = request.get("content").toString();
      if (content == null || content.trim().isEmpty()) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("留言內容不可為空");
      }

      Comment newComment = commentService.createComment(userId, postId, content);
      return ResponseEntity.status(HttpStatus.CREATED).body(newComment);
    } catch (NumberFormatException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("postId格式不正確");
    } catch (IllegalArgumentException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    } catch (Exception e) {
      System.err.println("創建留言時發生內部錯誤: " + e.getMessage());
      e.printStackTrace();
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("創建留言時發生內部錯誤，請查看日誌");
    }
  }

  /**
   * 獲取指定發文的所有留言
   *
   * @param postId 發文ID
   * @return 留言列表
   */
  @GetMapping("/post/{postId}")
  public ResponseEntity<?> getCommentsByPostId(@PathVariable Long postId) {
    try {
      List<Comment> comments = commentService.getCommentsByPostId(postId);
      return ResponseEntity.ok(comments);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("伺服器內部錯誤: " + e.getMessage());
    }
  }

  /**
   * 獲取當前用戶的所有留言
   *
   * @param token JWT 令牌
   * @return 留言列表
   */
  @GetMapping("/user")
  public ResponseEntity<?> getUserComments(
      @RequestHeader(value = "Authorization", required = false) String token) {

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

      List<Comment> comments = commentService.getCommentsByUserId(userId);
      return ResponseEntity.ok(comments);
    } catch (Exception e) {
      System.err.println("獲取用戶留言時發生內部錯誤: " + e.getMessage());
      e.printStackTrace();
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("獲取用戶留言時發生內部錯誤，請查看日誌");
    }
  }
}