package com.example.social_backend.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "User")
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "UserID")
  private Long userId;

  @Column(name = "PhoneNumber", unique = true, length = 12)
  private String phoneNumber;

  @Column(name = "UserName", nullable = false)
  private String userName;

  @Column(name = "Email")
  private String email;

  @Column(name = "Password", nullable = false)
  private String password;

  @Column(name = "CoverImage", nullable = true)
  private String coverImage;

  @Column(name = "Biography", nullable = true)
  private String biography;

  // 無參數建構子
  public User() {
  }

  // 有參數建構子
  public User(String phoneNumber, String userName, String email, String password, String coverImage, String biography) {
    this.phoneNumber = phoneNumber;
    this.userName = userName;
    this.email = email;
    this.password = password;
    this.coverImage = coverImage;
    this.biography = biography;
  }

  // Getters and Setters
  public Long getUserId() {
    return userId;
  }

  public void setUserId(Long userId) {
    this.userId = userId;
  }

  public String getPhoneNumber() {
    return phoneNumber;
  }

  public void setPhoneNumber(String phoneNumber) {
    this.phoneNumber = phoneNumber;
  }

  public String getUserName() {
    return userName;
  }

  public void setUserName(String userName) {
    this.userName = userName;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getCoverImage() {
    return coverImage;
  }

  public void setCoverImage(String coverImage) {
    this.coverImage = coverImage;
  }

  public String getBiography() {
    return biography;
  }

  public void setBiography(String biography) {
    this.biography = biography;
  }

  @Override
  public String toString() {
    return "User{" +
        "userId=" + userId +
        ", phoneNumber='" + phoneNumber + '\'' +
        ", userName='" + userName + '\'' +
        ", email='" + email + '\'' +
        ", coverImage='" + coverImage + '\'' +
        ", biography='" + biography + '\'' +
        '}';
  }
}