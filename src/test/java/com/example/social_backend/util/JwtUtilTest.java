package com.example.social_backend.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

  private JwtUtil jwtUtil;
  private final Long userId = 123L;
  private final String testSecret = "VGhpc0lzQVRlc3RTZWNyZXRLZXlGb3JKV1RHZW5lcmF0aW9uV2l0aE1pbmltdW0yNTZCaXRzTGVuZ3Ro";
  private final long testExpiration = 3600000; // 1小時

  @BeforeEach
  void setUp() {
    jwtUtil = new JwtUtil();
    // 使用反射設置私有字段，模擬配置文件中的值
    ReflectionTestUtils.setField(jwtUtil, "configSecret", testSecret);
    ReflectionTestUtils.setField(jwtUtil, "jwtExpiration", testExpiration);
  }

  @Test
  void generateToken_validUserId_returnsValidToken() {
    // 執行
    String token = jwtUtil.generateToken(userId);

    // 驗證
    assertNotNull(token);
    assertFalse(token.isEmpty());
  }

  @Test
  void validateToken_validToken_returnsTrue() {
    // 準備
    String token = jwtUtil.generateToken(userId);

    // 執行
    boolean isValid = jwtUtil.validateToken(token);

    // 驗證
    assertTrue(isValid);
  }

  @Test
  void validateToken_expiredToken_returnsFalse() throws Exception {
    // 準備 - 創建一個帶有負的過期時間的JwtUtil實例
    JwtUtil expiredJwtUtil = new JwtUtil();
    ReflectionTestUtils.setField(expiredJwtUtil, "configSecret", testSecret);
    ReflectionTestUtils.setField(expiredJwtUtil, "jwtExpiration", -10000); // 負的過期時間，使令牌立即過期

    // 執行 - 生成一個已過期的令牌
    String expiredToken = expiredJwtUtil.generateToken(userId);
    Thread.sleep(100); // 確保令牌過期

    // 驗證
    assertFalse(expiredJwtUtil.validateToken(expiredToken));
  }

  @Test
  void validateToken_invalidToken_returnsFalse() {
    // 準備
    String invalidToken = "invalid.token.string";

    // 執行
    boolean isValid = jwtUtil.validateToken(invalidToken);

    // 驗證
    assertFalse(isValid);
  }

  @Test
  void extractUserId_validToken_returnsCorrectUserId() {
    // 準備
    String token = jwtUtil.generateToken(userId);

    // 執行
    Long extractedUserId = jwtUtil.extractUserId(token);

    // 驗證
    assertEquals(userId, extractedUserId);
  }

  @Test
  void validateTokenAndGetUserId_validToken_returnsUserId() {
    // 準備
    String token = jwtUtil.generateToken(userId);

    // 執行
    Long extractedUserId = jwtUtil.validateTokenAndGetUserId(token);

    // 驗證
    assertEquals(userId, extractedUserId);
  }

  @Test
  void validateTokenAndGetUserId_invalidToken_throwsException() {
    // 準備
    String invalidToken = "invalid.token.string";

    // 執行和驗證
    Exception exception = assertThrows(RuntimeException.class, () -> jwtUtil.validateTokenAndGetUserId(invalidToken));

    assertEquals("無效或已過期的令牌", exception.getMessage());
  }

  @Test
  void extractExpiration_validToken_returnsCorrectExpirationDate() {
    // 準備
    String token = jwtUtil.generateToken(userId);
    long now = System.currentTimeMillis();

    // 執行
    Date expiration = jwtUtil.extractExpiration(token);

    // 驗證 - 過期時間應該在現在時間之後且接近now + testExpiration
    assertTrue(expiration.after(new Date(now)));
    // 過期時間應該在預期範圍內（允許1秒誤差）
    long expectedExpiration = now + testExpiration;
    assertTrue(Math.abs(expiration.getTime() - expectedExpiration) < 1000);
  }
}