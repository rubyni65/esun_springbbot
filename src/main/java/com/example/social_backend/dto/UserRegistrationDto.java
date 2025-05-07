package com.example.social_backend.dto;

/**
 * 用於接收使用者註冊請求的數據傳輸對象
 */
public class UserRegistrationDto {
  private String phoneNumber;
  private String userName;
  private String email;
  private String password;
  private String coverImage;
  private String biography;

  // 無參數建構子
  public UserRegistrationDto() {
  }

  // 有參數建構子
  public UserRegistrationDto(String phoneNumber, String userName, String email, String password, String coverImage,
      String biography) {
    this.phoneNumber = phoneNumber;
    this.userName = userName;
    this.email = email;
    this.password = password;
    this.coverImage = coverImage;
    this.biography = biography;
  }

  // Getters and Setters
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
}