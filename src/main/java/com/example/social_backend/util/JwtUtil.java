package com.example.social_backend.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

@Component
public class JwtUtil {

  // 預設的JWT密鑰
  private static final String DEFAULT_SECRET = "ThisIsASecretKeyForJWTGenerationWithMinimum256BitsLength";

  // 從application.properties中獲取JWT密鑰
  @Value("${jwt.secret:#{null}}")
  private String configSecret;

  // JWT令牌有效期（以毫秒為單位）- 預設24小時
  @Value("${jwt.expiration:86400000}")
  private long jwtExpiration;

  // 實例密鑰，每次應用程序重啟時會更新
  private final String instanceSecret;

  /**
   * 構造函數，初始化實例密鑰
   */
  public JwtUtil() {
    // 生成一個隨機的實例密鑰
    this.instanceSecret = Base64.getEncoder().encodeToString(UUID.randomUUID().toString().getBytes());
  }

  /**
   * 從JWT令牌中提取用戶ID
   *
   * @param token JWT令牌
   * @return 用戶ID
   */
  public Long extractUserId(String token) {
    final Claims claims = extractAllClaims(token);
    return Long.parseLong(claims.getSubject());
  }

  /**
   * 提取JWT令牌的過期時間
   *
   * @param token JWT令牌
   * @return 過期時間
   */
  public Date extractExpiration(String token) {
    return extractClaim(token, Claims::getExpiration);
  }

  /**
   * 從JWT令牌中提取指定的聲明
   *
   * @param token          JWT令牌
   * @param claimsResolver 聲明解析器
   * @return 提取的聲明值
   */
  public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
    final Claims claims = extractAllClaims(token);
    return claimsResolver.apply(claims);
  }

  /**
   * 從JWT令牌中提取所有聲明
   *
   * @param token JWT令牌
   * @return 所有聲明
   */
  private Claims extractAllClaims(String token) {
    JwtParser jwtParser = Jwts.parser()
        .verifyWith(getSigningKey())
        .build();
    return jwtParser.parseSignedClaims(token).getPayload();
  }

  /**
   * 檢查JWT令牌是否已過期
   *
   * @param token JWT令牌
   * @return 如果已過期則返回true
   */
  private Boolean isTokenExpired(String token) {
    final Date expiration = extractExpiration(token);
    return expiration.before(new Date());
  }

  /**
   * 為用戶生成JWT令牌
   *
   * @param userId 用戶ID
   * @return JWT令牌
   */
  public String generateToken(Long userId) {
    Map<String, Object> claims = new HashMap<>();
    return createToken(claims, String.valueOf(userId));
  }

  /**
   * 創建JWT令牌
   *
   * @param claims  要包含在令牌中的聲明
   * @param subject 令牌的主題（通常是用戶ID）
   * @return JWT令牌
   */
  private String createToken(Map<String, Object> claims, String subject) {
    return Jwts.builder()
        .claims(claims)
        .subject(subject)
        .issuedAt(new Date(System.currentTimeMillis()))
        .expiration(new Date(System.currentTimeMillis() + jwtExpiration))
        .signWith(getSigningKey())
        .compact();
  }

  /**
   * 驗證JWT令牌的有效性
   *
   * @param token JWT令牌
   * @return 如果令牌有效則返回true
   */
  public Boolean validateToken(String token) {
    try {
      // 只要令牌可以被解析且未過期，就認為它是有效的
      return !isTokenExpired(token);
    } catch (Exception e) {
      return false;
    }
  }

  /**
   * 獲取用於簽名JWT的密鑰
   *
   * @return 簽名密鑰
   */
  private SecretKey getSigningKey() {
    // 結合配置密鑰和實例密鑰，這樣每次應用重啟時都會產生不同的密鑰
    String baseSecret = (configSecret != null && !configSecret.isEmpty()) ? configSecret : DEFAULT_SECRET;
    String combinedSecret = baseSecret + instanceSecret;
    // 使用標準Base64編碼來處理可能的非URL安全字符
    byte[] keyBytes = Base64.getEncoder().encode((combinedSecret).getBytes());
    return Keys.hmacShaKeyFor(keyBytes);
  }

  /**
   * 驗證JWT令牌並返回用戶ID
   *
   * @param token JWT令牌
   * @return 用戶ID
   * @throws RuntimeException 如果令牌無效或已過期
   */
  public Long validateTokenAndGetUserId(String token) {
    if (!validateToken(token)) {
      throw new RuntimeException("無效或已過期的令牌");
    }
    return extractUserId(token);
  }
}