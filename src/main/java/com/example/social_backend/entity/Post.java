package com.example.social_backend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "Post")
public class Post {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "PostID")
  private Long postId;

  @Column(name = "UserID", nullable = false)
  private Long userId;

  @Column(name = "Content", nullable = false, length = 500)
  private String content;

  @Column(name = "Image")
  private String image;

  @Column(name = "CreatedAt", nullable = false)
  private LocalDateTime createdAt;

  // 無參數建構子
  public Post() {
    this.createdAt = LocalDateTime.now();
  }

  // 有參數建構子
  public Post(Long userId, String content, String image) {
    this.userId = userId;
    this.content = content;
    this.image = image;
    this.createdAt = LocalDateTime.now();
  }

  // Getters and Setters
  public Long getPostId() {
    return postId;
  }

  public void setPostId(Long postId) {
    this.postId = postId;
  }

  public Long getUserId() {
    return userId;
  }

  public void setUserId(Long userId) {
    this.userId = userId;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public String getImage() {
    return image;
  }

  public void setImage(String image) {
    this.image = image;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(LocalDateTime createdAt) {
    this.createdAt = createdAt;
  }

  @Override
  public String toString() {
    return "Post{" +
        "postId=" + postId +
        ", userId=" + userId +
        ", content='" + content + '\'' +
        ", image='" + image + '\'' +
        ", createdAt=" + createdAt +
        '}';
  }
}