package com.example.social_backend.dto;

/**
 * 用於接收使用者登入請求的數據傳輸對象
 */
public class LoginDto {
  private String phoneNumber;
  private String password;

  // 無參數建構子
  public LoginDto() {
  }

  // 有參數建構子
  public LoginDto(String phoneNumber, String password) {
    this.phoneNumber = phoneNumber;
    this.password = password;
  }

  // Getters and Setters
  public String getPhoneNumber() {
    return phoneNumber;
  }

  public void setPhoneNumber(String phoneNumber) {
    this.phoneNumber = phoneNumber;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }
}