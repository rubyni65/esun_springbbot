package com.example.social_backend.service;

import com.example.social_backend.entity.User;
import com.example.social_backend.repository.UserRepository;
import com.example.social_backend.util.JwtUtil;
import com.example.social_backend.util.SanitizerUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class UserServiceTest {

  @Mock
  private UserRepository userRepository;

  @Mock
  private JwtUtil jwtUtil;

  @Mock
  private SanitizerUtil sanitizerUtil;

  @InjectMocks
  private UserService userService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);

    // 配置SanitizerUtil模擬
    when(sanitizerUtil.stripAllHtml(anyString())).thenAnswer(invocation -> invocation.getArgument(0));
    when(sanitizerUtil.sanitizeUrl(anyString())).thenAnswer(invocation -> invocation.getArgument(0));
    when(sanitizerUtil.sanitize(anyString())).thenAnswer(invocation -> invocation.getArgument(0));
  }

  @Test
  void registerUser_validInputs_returnsCreatedUser() {
    // 準備
    String phoneNumber = "1234567890";
    String userName = "測試用戶";
    String email = "test@example.com";
    String password = "password123";
    String coverImage = "http://example.com/image.jpg";
    String biography = "這是我的簡介";

    User savedUser = new User(phoneNumber, userName, email, "encryptedPassword", coverImage, biography);
    savedUser.setUserId(1L);

    when(userRepository.findByPhoneNumber(phoneNumber)).thenReturn(Optional.empty());
    when(userRepository.save(any(User.class))).thenReturn(savedUser);

    // 執行
    User result = userService.registerUser(phoneNumber, userName, email, password, coverImage, biography);

    // 驗證
    assertNotNull(result);
    assertEquals(1L, result.getUserId());
    assertEquals(phoneNumber, result.getPhoneNumber());
    assertEquals(userName, result.getUserName());
    assertEquals(email, result.getEmail());
    assertEquals(coverImage, result.getCoverImage());
    assertEquals(biography, result.getBiography());

    verify(userRepository, times(1)).findByPhoneNumber(phoneNumber);
    verify(userRepository, times(1)).save(any(User.class));
  }

  @Test
  void registerUser_duplicatePhoneNumber_throwsException() {
    // 準備
    String phoneNumber = "1234567890";
    when(userRepository.findByPhoneNumber(phoneNumber)).thenReturn(Optional.of(new User()));

    // 執行和驗證
    Exception exception = assertThrows(IllegalArgumentException.class, () -> {
      userService.registerUser(phoneNumber, "測試用戶", "test@example.com", "password123", "http://example.com/image.jpg",
          "這是我的簡介");
    });

    assertEquals("此手機號碼已被註冊", exception.getMessage());
    verify(userRepository, times(1)).findByPhoneNumber(phoneNumber);
    verify(userRepository, never()).save(any(User.class));
  }

  @Test
  void registerUser_nullPhoneNumber_throwsException() {
    // 執行和驗證
    Exception exception = assertThrows(IllegalArgumentException.class, () -> {
      userService.registerUser(null, "測試用戶", "test@example.com", "password123", "http://example.com/image.jpg",
          "這是我的簡介");
    });

    assertEquals("手機號碼不能為空", exception.getMessage());
    verify(userRepository, never()).findByPhoneNumber(anyString());
    verify(userRepository, never()).save(any(User.class));
  }

  @Test
  void loginUser_validCredentials_returnsJwtToken() {
    // 準備
    String phoneNumber = "1234567890";
    String password = "password123";
    String encryptedPassword = new BCryptPasswordEncoder().encode(password);
    String jwtToken = "jwt.token.here";

    User user = new User();
    user.setUserId(1L);
    user.setPhoneNumber(phoneNumber);
    user.setPassword(encryptedPassword);

    when(userRepository.findByPhoneNumber(phoneNumber)).thenReturn(Optional.of(user));
    when(jwtUtil.generateToken(1L)).thenReturn(jwtToken);

    // 執行
    String result = userService.loginUser(phoneNumber, password);

    // 驗證
    assertEquals(jwtToken, result);
    verify(userRepository, times(1)).findByPhoneNumber(phoneNumber);
    verify(jwtUtil, times(1)).generateToken(1L);
  }

  @Test
  void loginUser_invalidCredentials_throwsException() {
    // 準備
    String phoneNumber = "1234567890";
    String password = "wrongPassword";
    String encryptedPassword = new BCryptPasswordEncoder().encode("correctPassword");

    User user = new User();
    user.setUserId(1L);
    user.setPhoneNumber(phoneNumber);
    user.setPassword(encryptedPassword);

    when(userRepository.findByPhoneNumber(phoneNumber)).thenReturn(Optional.of(user));

    // 執行和驗證
    Exception exception = assertThrows(BadCredentialsException.class, () -> {
      userService.loginUser(phoneNumber, password);
    });

    assertEquals("手機號碼或密碼不正確", exception.getMessage());
    verify(userRepository, times(1)).findByPhoneNumber(phoneNumber);
    verify(jwtUtil, never()).generateToken(anyLong());
  }

  @Test
  void loginUser_userNotFound_throwsException() {
    // 準備
    String phoneNumber = "1234567890";
    String password = "password123";

    when(userRepository.findByPhoneNumber(phoneNumber)).thenReturn(Optional.empty());

    // 執行和驗證
    Exception exception = assertThrows(BadCredentialsException.class, () -> {
      userService.loginUser(phoneNumber, password);
    });

    assertEquals("手機號碼或密碼不正確", exception.getMessage());
    verify(userRepository, times(1)).findByPhoneNumber(phoneNumber);
    verify(jwtUtil, never()).generateToken(anyLong());
  }
}