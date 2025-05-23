package com.example.social_backend.controller;

import com.example.social_backend.dto.ApiResponse;
import com.example.social_backend.dto.LoginDto;
import com.example.social_backend.dto.UserRegistrationDto;
import com.example.social_backend.entity.User;
import com.example.social_backend.service.UserService;
import com.example.social_backend.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class UserController {

  private final UserService userService;
  private final JwtUtil jwtUtil;

  @Autowired
  public UserController(UserService userService, JwtUtil jwtUtil) {
    this.userService = userService;
    this.jwtUtil = jwtUtil;
  }

  /**
   * 處理使用者註冊請求
   *
   * @param registrationDto 註冊請求的數據傳輸對象
   * @return ResponseEntity 包含API響應
   */
  @PostMapping("/register")
  public ResponseEntity<ApiResponse<Map<String, Object>>> registerUser(
      @RequestBody UserRegistrationDto registrationDto) {
    try {
      // 調用服務層進行使用者註冊
      User registeredUser = userService.registerUser(
          registrationDto.getPhoneNumber(),
          registrationDto.getUserName(),
          registrationDto.getEmail(),
          registrationDto.getPassword(),
          registrationDto.getCoverImage(),
          registrationDto.getBiography());

      // 構建安全的返回數據，不包含密碼
      Map<String, Object> userData = new HashMap<>();
      userData.put("userId", registeredUser.getUserId());
      userData.put("phoneNumber", registeredUser.getPhoneNumber());
      userData.put("userName", registeredUser.getUserName());
      userData.put("email", registeredUser.getEmail());

      // 返回成功響應
      return ResponseEntity.status(HttpStatus.CREATED)
          .body(ApiResponse.success("使用者註冊成功", userData));
    } catch (IllegalArgumentException e) {
      // 返回客戶端錯誤響應
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
          .body(ApiResponse.error(e.getMessage()));
    } catch (Exception e) {
      // 返回伺服器錯誤響應
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(ApiResponse.error("伺服器內部錯誤: " + e.getMessage()));
    }
  }

  /**
   * 處理使用者登入請求
   *
   * @param loginDto 登入請求的數據傳輸對象
   * @return ResponseEntity 包含API響應，成功時返回JWT令牌
   */
  @PostMapping("/login")
  public ResponseEntity<ApiResponse<Map<String, Object>>> loginUser(@RequestBody LoginDto loginDto) {
    try {
      // 調用服務層進行使用者登入
      String jwtToken = userService.loginUser(
          loginDto.getPhoneNumber(),
          loginDto.getPassword());

      // 構建返回數據，包含JWT令牌
      Map<String, Object> responseData = new HashMap<>();
      responseData.put("token", jwtToken);
      responseData.put("tokenType", "Bearer");

      // 返回成功響應
      return ResponseEntity.ok(ApiResponse.success("登入成功", responseData));
    } catch (BadCredentialsException e) {
      // 返回認證錯誤響應
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
          .body(ApiResponse.error(e.getMessage()));
    } catch (Exception e) {
      // 返回伺服器錯誤響應
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(ApiResponse.error("伺服器內部錯誤: " + e.getMessage()));
    }
  }

  /**
   * 驗證JWT令牌是否有效
   *
   * @param token JWT令牌
   * @return 驗證結果
   */
  @GetMapping("/validate-token")
  public ResponseEntity<?> validateToken(@RequestHeader(value = "Authorization", required = false) String token) {
    try {
      if (token == null || token.isEmpty() || !token.startsWith("Bearer ")) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("未提供有效的認證令牌");
      }

      String actualToken = token.substring(7);
      try {
        Long userId = jwtUtil.validateTokenAndGetUserId(actualToken);
        Map<String, Object> response = new HashMap<>();
        response.put("valid", true);
        response.put("userId", userId);
        return ResponseEntity.ok(response);
      } catch (RuntimeException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("認證失敗：" + e.getMessage());
      }
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("伺服器內部錯誤: " + e.getMessage());
    }
  }
}