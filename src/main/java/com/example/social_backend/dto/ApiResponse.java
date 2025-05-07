package com.example.social_backend.dto;

/**
 * 標準化API響應的數據傳輸對象
 */
public class ApiResponse<T> {
  private boolean success;
  private String message;
  private T data;

  // 成功響應
  public static <T> ApiResponse<T> success(T data) {
    return new ApiResponse<>(true, "操作成功", data);
  }

  // 成功響應，帶自訂消息
  public static <T> ApiResponse<T> success(String message, T data) {
    return new ApiResponse<>(true, message, data);
  }

  // 錯誤響應
  public static <T> ApiResponse<T> error(String message) {
    return new ApiResponse<>(false, message, null);
  }

  // 建構子
  public ApiResponse(boolean success, String message, T data) {
    this.success = success;
    this.message = message;
    this.data = data;
  }

  // Getters and Setters
  public boolean isSuccess() {
    return success;
  }

  public void setSuccess(boolean success) {
    this.success = success;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public T getData() {
    return data;
  }

  public void setData(T data) {
    this.data = data;
  }
}