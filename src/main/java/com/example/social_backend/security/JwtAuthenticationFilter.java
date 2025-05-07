package com.example.social_backend.security;

import com.example.social_backend.entity.User;
import com.example.social_backend.service.UserService;
import com.example.social_backend.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

/**
 * JWT認證過濾器，用於解析和驗證JWT令牌
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final JwtUtil jwtUtil;
  private final UserService userService;

  @Autowired
  public JwtAuthenticationFilter(JwtUtil jwtUtil, UserService userService) {
    this.jwtUtil = jwtUtil;
    this.userService = userService;
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

    // 從HTTP請求頭中獲取Authorization
    final String authorizationHeader = request.getHeader("Authorization");

    String jwt = null;
    Long userId = null;

    // 檢查Authorization頭是否存在且以Bearer開頭
    if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
      // 提取JWT令牌
      jwt = authorizationHeader.substring(7);
      try {
        // 從令牌中提取用戶ID
        userId = jwtUtil.extractUserId(jwt);
      } catch (Exception e) {
        logger.error("無法從令牌中提取用戶ID", e);
      }
    } else {
      logger.debug("未找到Bearer令牌");
    }

    // 檢查用戶ID是否存在且當前尚未進行身份驗證
    if (userId != null && SecurityContextHolder.getContext().getAuthentication() == null) {
      try {
        // 驗證JWT令牌
        if (jwtUtil.validateToken(jwt)) {
          // 從數據庫中獲取用戶
          User user = userService.findUserById(userId);

          // 創建身份驗證對象
          UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
              user, null, Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));

          authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

          // 設置安全上下文
          SecurityContextHolder.getContext().setAuthentication(authenticationToken);

          logger.debug("用戶已通過JWT令牌身份驗證: " + userId);
        } else {
          logger.debug("JWT令牌無效");
        }
      } catch (Exception e) {
        logger.error("無法設置用戶身份驗證", e);
      }
    }

    // 繼續過濾器鏈
    filterChain.doFilter(request, response);
  }
}