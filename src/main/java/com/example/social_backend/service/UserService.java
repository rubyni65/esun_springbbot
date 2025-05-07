package com.example.social_backend.service;

import com.example.social_backend.entity.User;
import com.example.social_backend.repository.UserRepository;
import com.example.social_backend.util.JwtUtil;
import com.example.social_backend.util.SanitizerUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

  private final UserRepository userRepository;
  private final BCryptPasswordEncoder passwordEncoder;
  private final JwtUtil jwtUtil;
  private final SanitizerUtil sanitizerUtil;

  @Autowired
  public UserService(UserRepository userRepository, JwtUtil jwtUtil, SanitizerUtil sanitizerUtil) {
    this.userRepository = userRepository;
    this.passwordEncoder = new BCryptPasswordEncoder();
    this.jwtUtil = jwtUtil;
    this.sanitizerUtil = sanitizerUtil;
  }

  /**
   * 註冊新使用者
   *
   * @param phoneNumber 手機號碼
   * @param userName    使用者姓名
   * @param email       電子郵件
   * @param password    密碼
   * @param coverImage  封面圖片URL
   * @param biography   個人簡介
   * @return 註冊成功的使用者實體
   * @throws IllegalArgumentException 如果手機號碼已經被註冊或必要欄位為空
   */
  public User registerUser(String phoneNumber, String userName, String email,
      String password, String coverImage, String biography) {
    // 驗證輸入
    if (phoneNumber == null || phoneNumber.isEmpty()) {
      throw new IllegalArgumentException("手機號碼不能為空");
    }

    if (userName == null || userName.isEmpty()) {
      throw new IllegalArgumentException("使用者姓名不能為空");
    }

    if (password == null || password.isEmpty()) {
      throw new IllegalArgumentException("密碼不能為空");
    }

    // 檢查手機號碼是否已被註冊
    Optional<User> existingUser = userRepository.findByPhoneNumber(phoneNumber);
    if (existingUser.isPresent()) {
      throw new IllegalArgumentException("此手機號碼已被註冊");
    }

    // 淨化輸入以防止XSS攻擊
    String sanitizedUserName = sanitizerUtil.stripAllHtml(userName);
    String sanitizedEmail = sanitizerUtil.stripAllHtml(email);
    String sanitizedCoverImage = sanitizerUtil.sanitizeUrl(coverImage);
    String sanitizedBiography = sanitizerUtil.sanitize(biography);

    // 加密密碼
    String encryptedPassword = passwordEncoder.encode(password);

    // 創建新使用者
    User newUser = new User(phoneNumber, sanitizedUserName, sanitizedEmail, encryptedPassword, sanitizedCoverImage,
        sanitizedBiography);

    // 儲存使用者並返回
    return userRepository.save(newUser);
  }

  /**
   * 使用者登入
   *
   * @param phoneNumber 手機號碼
   * @param password    密碼
   * @return JWT令牌
   * @throws BadCredentialsException 如果手機號碼或密碼不正確
   */
  public String loginUser(String phoneNumber, String password) {
    // 驗證輸入
    if (phoneNumber == null || phoneNumber.isEmpty() || password == null || password.isEmpty()) {
      throw new BadCredentialsException("手機號碼和密碼不能為空");
    }

    // 查找使用者
    Optional<User> userOptional = userRepository.findByPhoneNumber(phoneNumber);
    User user = userOptional.orElseThrow(() -> new BadCredentialsException("手機號碼或密碼不正確"));

    // 驗證密碼 (允許明文密碼或加密密碼匹配)
    if (!passwordEncoder.matches(password, user.getPassword()) && !password.equals(user.getPassword())) {
      throw new BadCredentialsException("手機號碼或密碼不正確");
    }

    // 生成JWT令牌
    return jwtUtil.generateToken(user.getUserId());
  }

  /**
   * 根據ID查找使用者
   *
   * @param userId 使用者ID
   * @return 使用者實體
   */
  public User findUserById(Long userId) {
    return userRepository.findById(userId)
        .orElseThrow(() -> new IllegalArgumentException("找不到ID為 " + userId + " 的使用者"));
  }
}