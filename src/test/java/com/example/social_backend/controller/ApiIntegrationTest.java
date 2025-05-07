package com.example.social_backend.controller;

import com.example.social_backend.entity.Post;
import com.example.social_backend.entity.User;
import com.example.social_backend.repository.PostRepository;
import com.example.social_backend.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ApiIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private PostRepository postRepository;

  private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

  private String testUserToken;

  @BeforeEach
  void setUp() throws Exception {
    // 創建一個測試用戶並獲取令牌
    userRepository.deleteAll(); // 清空用戶數據

    // 註冊用戶
    Map<String, String> registerRequest = new HashMap<>();
    registerRequest.put("phoneNumber", "1234567890");
    registerRequest.put("userName", "測試用戶");
    registerRequest.put("password", "password123");
    registerRequest.put("email", "test@example.com");

    mockMvc.perform(post("/api/register")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(registerRequest)))
        .andExpect(status().isCreated());

    // 登入並保存令牌
    Map<String, String> loginRequest = new HashMap<>();
    loginRequest.put("phoneNumber", "1234567890");
    loginRequest.put("password", "password123");

    MvcResult loginResult = mockMvc.perform(post("/api/login")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(loginRequest)))
        .andExpect(status().isOk())
        .andReturn();

    Map<String, Object> loginResponse = objectMapper.readValue(
        loginResult.getResponse().getContentAsString(), Map.class);
    if (loginResponse.get("data") instanceof Map) {
      @SuppressWarnings("unchecked")
      Map<String, Object> dataMap = (Map<String, Object>) loginResponse.get("data");
      testUserToken = (String) dataMap.get("token");
    }
    assertNotNull(testUserToken, "登入後未能從回應中獲取到 token");
  }

  @Test
  void registerUser_validInput_success() throws Exception {
    // 準備新用戶數據
    Map<String, String> request = new HashMap<>();
    request.put("phoneNumber", "9876543210");
    request.put("userName", "新用戶");
    request.put("password", "password456");
    request.put("email", "new@example.com");

    // 執行註冊請求
    mockMvc.perform(post("/api/register")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.userId", notNullValue()))
        .andExpect(jsonPath("$.phoneNumber", is("9876543210")))
        .andExpect(jsonPath("$.userName", is("新用戶")))
        .andExpect(jsonPath("$.email", is("new@example.com")))
        .andExpect(jsonPath("$.password").doesNotExist()); // 密碼不應該在響應中返回
  }

  @Test
  void registerUser_duplicatePhoneNumber_fails() throws Exception {
    // 使用已存在的手機號碼
    Map<String, String> request = new HashMap<>();
    request.put("phoneNumber", "1234567890"); // 已經在setUp中註冊了
    request.put("userName", "重複用戶");
    request.put("password", "password789");

    // 執行註冊請求，應該失敗
    mockMvc.perform(post("/api/register")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest())
        .andExpect(content().string(containsString("已被註冊")));
  }

  @Test
  void loginUser_validCredentials_returnsToken() throws Exception {
    // 使用有效憑證
    Map<String, String> request = new HashMap<>();
    request.put("phoneNumber", "1234567890");
    request.put("password", "password123");

    // 執行登入請求
    mockMvc.perform(post("/api/login")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.token", notNullValue()))
        .andExpect(jsonPath("$.data.token", not(emptyString())));
  }

  @Test
  void loginUser_invalidCredentials_fails() throws Exception {
    // 使用無效密碼
    Map<String, String> request = new HashMap<>();
    request.put("phoneNumber", "1234567890");
    request.put("password", "wrongpassword");

    // 執行登入請求，應該失敗
    mockMvc.perform(post("/api/login")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isUnauthorized());
  }

  @Test
  void createPost_authenticatedUser_success() throws Exception {
    // 準備發文數據
    Map<String, String> request = new HashMap<>();
    request.put("content", "這是一篇測試發文");
    request.put("image", "http://example.com/image.jpg");

    // 執行創建發文請求
    mockMvc.perform(post("/api/posts")
        .header("Authorization", "Bearer " + testUserToken)
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.postId", notNullValue()))
        .andExpect(jsonPath("$.content", is("這是一篇測試發文")))
        .andExpect(jsonPath("$.image", is("http://example.com/image.jpg")))
        .andExpect(jsonPath("$.createdAt", notNullValue()));
  }

  @Test
  void createPost_unauthenticatedUser_fails() throws Exception {
    // 準備發文數據
    Map<String, String> request = new HashMap<>();
    request.put("content", "這是一篇測試發文");

    // 執行創建發文請求，無令牌
    mockMvc.perform(post("/api/posts")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isUnauthorized());
  }

  @Test
  void createComment_validPostId_success() throws Exception {
    // 首先創建一篇發文
    Map<String, String> postRequest = new HashMap<>();
    postRequest.put("content", "發文用於測試留言");

    MvcResult postResult = mockMvc.perform(post("/api/posts")
        .header("Authorization", "Bearer " + testUserToken)
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(postRequest)))
        .andExpect(status().isCreated())
        .andReturn();

    Map<String, Object> postResponse = objectMapper.readValue(
        postResult.getResponse().getContentAsString(), Map.class);
    Integer postId = (Integer) postResponse.get("postId");

    // 然後添加留言
    Map<String, Object> commentRequest = new HashMap<>();
    commentRequest.put("postId", postId);
    commentRequest.put("content", "這是一則測試留言");

    mockMvc.perform(post("/api/comments")
        .header("Authorization", "Bearer " + testUserToken)
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(commentRequest)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.commentId", notNullValue()))
        .andExpect(jsonPath("$.postId", is(postId)))
        .andExpect(jsonPath("$.content", is("這是一則測試留言")))
        .andExpect(jsonPath("$.createdAt", notNullValue()));
  }

  @Test
  void createComment_invalidPostId_fails() throws Exception {
    // 使用不存在的發文ID
    Map<String, Object> request = new HashMap<>();
    request.put("postId", 999999); // 不存在的ID
    request.put("content", "這是一則測試留言");

    mockMvc.perform(post("/api/comments")
        .header("Authorization", "Bearer " + testUserToken)
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest())
        .andExpect(content().string(containsString("找不到ID為")));
  }

  @Test
  void getAllPosts_returnsListOfPosts() throws Exception {
    // 創建兩篇測試發文
    Map<String, String> postRequest1 = new HashMap<>();
    postRequest1.put("content", "測試發文1");

    Map<String, String> postRequest2 = new HashMap<>();
    postRequest2.put("content", "測試發文2");

    mockMvc.perform(post("/api/posts")
        .header("Authorization", "Bearer " + testUserToken)
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(postRequest1)))
        .andExpect(status().isCreated());

    mockMvc.perform(post("/api/posts")
        .header("Authorization", "Bearer " + testUserToken)
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(postRequest2)))
        .andExpect(status().isCreated());

    // 獲取所有發文
    mockMvc.perform(get("/api/posts"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(2))))
        .andExpect(jsonPath("$[0].content", notNullValue()))
        .andExpect(jsonPath("$[1].content", notNullValue()));
  }
}