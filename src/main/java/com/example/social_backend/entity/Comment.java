package com.example.social_backend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "Comment")
public class Comment {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "CommentID")
  private Long commentId;

  @Column(name = "UserID", nullable = false)
  private Long userId;

  @Column(name = "PostID", nullable = false)
  private Long postId;

  @Column(name = "Content", nullable = false, length = 500)
  private String content;

  @Column(name = "CreatedAt", nullable = false)
  private LocalDateTime createdAt;

  // 無參數建構子
  public Comment() {
    this.createdAt = LocalDateTime.now();
  }

  // 有參數建構子
  public Comment(Long userId, Long postId, String content) {
    this.userId = userId;
    this.postId = postId;
    this.content = content;
    this.createdAt = LocalDateTime.now();
  }

  // Getters and Setters
  public Long getCommentId() {
    return commentId;
  }

  public void setCommentId(Long commentId) {
    this.commentId = commentId;
  }

  public Long getUserId() {
    return userId;
  }

  public void setUserId(Long userId) {
    this.userId = userId;
  }

  public Long getPostId() {
    return postId;
  }

  public void setPostId(Long postId) {
    this.postId = postId;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(LocalDateTime createdAt) {
    this.createdAt = createdAt;
  }

  @Override
  public String toString() {
    return "Comment{" +
        "commentId=" + commentId +
        ", userId=" + userId +
        ", postId=" + postId +
        ", content='" + content + '\'' +
        ", createdAt=" + createdAt +
        '}';
  }
}