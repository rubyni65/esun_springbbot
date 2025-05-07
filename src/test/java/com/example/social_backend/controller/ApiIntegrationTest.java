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
        .andExpect(jsonPath("$.data.userId", notNullValue()))
        .andExpect(jsonPath("$.data.phoneNumber", is("9876543210")))
        .andExpect(jsonPath("$.data.userName", is("新用戶")))
        .andExpect(jsonPath("$.data.email", is("new@example.com")))
        .andExpect(jsonPath("$.data.password").doesNotExist()) // 密碼不應該在響應中返回
        .andExpect(jsonPath("$.success", is(true)))
        .andExpect(jsonPath("$.message", is("使用者註冊成功")));
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
        .andExpect(status().isForbidden());
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
    mockMvc.perform(get("/api/posts")
        .header("Authorization", "Bearer " + testUserToken))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", isA(java.util.List.class)))
        .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(2))))
        .andExpect(jsonPath("$[0].content", anyOf(is("測試發文1"), is("測試發文2"))))
        .andExpect(jsonPath("$[1].content", anyOf(is("測試發文1"), is("測試發文2"))));
  }

  @Test
  void updatePost_authenticatedOwner_success() throws Exception {
    // 首先創建一篇發文
    Map<String, String> createRequest = new HashMap<>();
    createRequest.put("content", "原始發文內容");
    createRequest.put("image", "http://example.com/original.jpg");

    MvcResult createResult = mockMvc.perform(post("/api/posts")
        .header("Authorization", "Bearer " + testUserToken)
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(createRequest)))
        .andExpect(status().isCreated())
        .andReturn();

    Map<String, Object> createResponse = objectMapper.readValue(
        createResult.getResponse().getContentAsString(), Map.class);
    Integer postId = (Integer) createResponse.get("postId");

    // 然後更新發文
    Map<String, String> updateRequest = new HashMap<>();
    updateRequest.put("content", "更新後的發文內容");
    updateRequest.put("image", "http://example.com/updated.jpg");

    mockMvc.perform(put("/api/posts/" + postId)
        .header("Authorization", "Bearer " + testUserToken)
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(updateRequest)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.postId", is(postId)))
        .andExpect(jsonPath("$.content", is("更新後的發文內容")))
        .andExpect(jsonPath("$.image", is("http://example.com/updated.jpg")))
        .andExpect(jsonPath("$.createdAt", notNullValue()));
  }

  @Test
  void updatePost_unauthenticatedUser_fails() throws Exception {
    // 首先創建一篇發文
    Map<String, String> createRequest = new HashMap<>();
    createRequest.put("content", "原始發文內容");

    MvcResult createResult = mockMvc.perform(post("/api/posts")
        .header("Authorization", "Bearer " + testUserToken)
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(createRequest)))
        .andExpect(status().isCreated())
        .andReturn();

    Map<String, Object> createResponse = objectMapper.readValue(
        createResult.getResponse().getContentAsString(), Map.class);
    Integer postId = (Integer) createResponse.get("postId");

    // 無認證嘗試更新發文
    Map<String, String> updateRequest = new HashMap<>();
    updateRequest.put("content", "更新後的發文內容");

    mockMvc.perform(put("/api/posts/" + postId)
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(updateRequest)))
        .andExpect(status().isForbidden());
  }

  @Test
  void updatePost_nonExistentPost_fails() throws Exception {
    // 嘗試更新一個不存在的發文
    Map<String, String> updateRequest = new HashMap<>();
    updateRequest.put("content", "更新後的發文內容");

    mockMvc.perform(put("/api/posts/99999")
        .header("Authorization", "Bearer " + testUserToken)
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(updateRequest)))
        .andExpect(status().isForbidden())
        .andExpect(content().string(containsString("沒有權限")));
  }

  @Test
  void deletePost_authenticatedOwner_success() throws Exception {
    // 首先創建一篇發文
    Map<String, String> createRequest = new HashMap<>();
    createRequest.put("content", "將被刪除的發文");

    MvcResult createResult = mockMvc.perform(post("/api/posts")
        .header("Authorization", "Bearer " + testUserToken)
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(createRequest)))
        .andExpect(status().isCreated())
        .andReturn();

    Map<String, Object> createResponse = objectMapper.readValue(
        createResult.getResponse().getContentAsString(), Map.class);
    Integer postId = (Integer) createResponse.get("postId");

    // 然後刪除發文
    mockMvc.perform(delete("/api/posts/" + postId)
        .header("Authorization", "Bearer " + testUserToken))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.message", containsString("成功刪除")));

    // 驗證發文已被刪除
    mockMvc.perform(get("/api/posts/" + postId))
        .andExpect(status().isNotFound());
  }

  @Test
  void deletePost_unauthenticatedUser_fails() throws Exception {
    // 首先創建一篇發文
    Map<String, String> createRequest = new HashMap<>();
    createRequest.put("content", "將被刪除的發文");

    MvcResult createResult = mockMvc.perform(post("/api/posts")
        .header("Authorization", "Bearer " + testUserToken)
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(createRequest)))
        .andExpect(status().isCreated())
        .andReturn();

    Map<String, Object> createResponse = objectMapper.readValue(
        createResult.getResponse().getContentAsString(), Map.class);
    Integer postId = (Integer) createResponse.get("postId");

    // 無認證嘗試刪除發文
    mockMvc.perform(delete("/api/posts/" + postId))
        .andExpect(status().isForbidden());
  }

  @Test
  void deletePost_nonExistentPost_fails() throws Exception {
    // 嘗試刪除一個不存在的發文
    mockMvc.perform(delete("/api/posts/99999")
        .header("Authorization", "Bearer " + testUserToken))
        .andExpect(status().isForbidden())
        .andExpect(content().string(containsString("沒有權限")));
  }

  @Test
  void createPostAndComment_validTransaction_successAndConsistentData() throws Exception {
    // 準備發文和留言數據
    Map<String, String> request = new HashMap<>();
    request.put("postContent", "這是交易測試的發文內容");
    request.put("postImage", "http://example.com/transaction-test.jpg");
    request.put("commentContent", "這是對交易測試發文的留言");

    // 執行事務API請求
    MvcResult result = mockMvc.perform(post("/api/posts/with-comment")
        .header("Authorization", "Bearer " + testUserToken)
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.postId", notNullValue()))
        .andExpect(jsonPath("$.commentId", notNullValue()))
        .andReturn();

    // 從結果中解析發文ID和留言ID
    Map<String, Object> response = objectMapper.readValue(
        result.getResponse().getContentAsString(), Map.class);
    Integer postId = (Integer) response.get("postId");
    Integer commentId = (Integer) response.get("commentId");

    // 驗證發文是否成功創建
    mockMvc.perform(get("/api/posts/" + postId)
        .header("Authorization", "Bearer " + testUserToken))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.postId", is(postId)))
        .andExpect(jsonPath("$.content", is("這是交易測試的發文內容")))
        .andExpect(jsonPath("$.image", is("http://example.com/transaction-test.jpg")));

    // 驗證評論是否存在（通過API或直接從資料庫）
    // 這裡我們通過檢查評論列表間接驗證
    mockMvc.perform(get("/api/posts")
        .header("Authorization", "Bearer " + testUserToken))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[?(@.postId == " + postId + ")]", isA(java.util.Map.class)));
  }

  @Test
  void createPostAndComment_invalidTransaction_fails() throws Exception {
    // 準備無效的發文和留言數據（空的發文內容）
    Map<String, String> request = new HashMap<>();
    request.put("postContent", ""); // 空的發文內容，應該導致事務失敗
    request.put("postImage", "http://example.com/failed-transaction.jpg");
    request.put("commentContent", "這個留言不應該被保存");

    // 執行事務API請求，預期失敗
    mockMvc.perform(post("/api/posts/with-comment")
        .header("Authorization", "Bearer " + testUserToken)
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest())
        .andExpect(content().string(containsString("發文內容不能為空")));
  }

  @Test
  void createPostAndComment_unauthenticatedUser_fails() throws Exception {
    // 準備發文和留言數據
    Map<String, String> request = new HashMap<>();
    request.put("postContent", "這是未認證的交易測試");
    request.put("commentContent", "這是未認證的留言");

    // 執行事務API請求，不提供認證令牌
    mockMvc.perform(post("/api/posts/with-comment")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isForbidden());
  }

  @Test
  void getPostById_existingPost_returnsPost() throws Exception {
    // 首先創建一篇發文
    Map<String, String> createRequest = new HashMap<>();
    createRequest.put("content", "這是將被查詢的發文");
    createRequest.put("image", "http://example.com/queried-image.jpg");

    MvcResult createResult = mockMvc.perform(post("/api/posts")
        .header("Authorization", "Bearer " + testUserToken)
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(createRequest)))
        .andExpect(status().isCreated())
        .andReturn();

    Map<String, Object> createResponse = objectMapper.readValue(
        createResult.getResponse().getContentAsString(), Map.class);
    Integer postId = (Integer) createResponse.get("postId");

    // 然後查詢該發文
    mockMvc.perform(get("/api/posts/" + postId)
        .header("Authorization", "Bearer " + testUserToken))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.postId", is(postId)))
        .andExpect(jsonPath("$.content", is("這是將被查詢的發文")))
        .andExpect(jsonPath("$.image", is("http://example.com/queried-image.jpg")))
        .andExpect(jsonPath("$.createdAt", notNullValue()));
  }

  @Test
  void getPostById_nonExistingPost_returnsNotFound() throws Exception {
    // 嘗試查詢不存在的發文
    mockMvc.perform(get("/api/posts/99999")
        .header("Authorization", "Bearer " + testUserToken))
        .andExpect(status().isNotFound())
        .andExpect(content().string(containsString("找不到ID為 99999 的發文")));
  }

  @Test
  void getUserPosts_existingUser_returnsUserPosts() throws Exception {
    // 首先創建第一篇發文
    Map<String, String> createRequest1 = new HashMap<>();
    createRequest1.put("content", "用戶的第一篇發文");

    // 首先創建第二篇發文
    Map<String, String> createRequest2 = new HashMap<>();
    createRequest2.put("content", "用戶的第二篇發文");

    // 創建發文
    mockMvc.perform(post("/api/posts")
        .header("Authorization", "Bearer " + testUserToken)
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(createRequest1)))
        .andExpect(status().isCreated());

    mockMvc.perform(post("/api/posts")
        .header("Authorization", "Bearer " + testUserToken)
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(createRequest2)))
        .andExpect(status().isCreated());

    // 從登入回應中獲取用戶ID
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

    // 從data中獲取userId
    @SuppressWarnings("unchecked")
    Map<String, Object> dataMap = (Map<String, Object>) loginResponse.get("data");
    Integer userId = (Integer) dataMap.get("userId");

    // 查詢該用戶的所有發文
    mockMvc.perform(get("/api/posts/user/" + userId)
        .header("Authorization", "Bearer " + testUserToken))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", isA(java.util.List.class)))
        .andExpect(jsonPath("$.length()", greaterThanOrEqualTo(2)))
        .andExpect(jsonPath("$[*].userId", everyItem(is(userId))))
        .andExpect(jsonPath("$[*].content", hasItems(is("用戶的第一篇發文"), is("用戶的第二篇發文"))));
  }

  @Test
  void getUserPosts_userWithNoPosts_returnsEmptyList() throws Exception {
    // 創建一個新用戶但不發表任何發文
    Map<String, String> registerRequest = new HashMap<>();
    registerRequest.put("phoneNumber", "5555555555");
    registerRequest.put("userName", "無發文用戶");
    registerRequest.put("password", "password123");

    MvcResult registerResult = mockMvc.perform(post("/api/register")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(registerRequest)))
        .andExpect(status().isCreated())
        .andReturn();

    Map<String, Object> registerResponse = objectMapper.readValue(
        registerResult.getResponse().getContentAsString(), Map.class);

    // 從data中獲取userId
    @SuppressWarnings("unchecked")
    Map<String, Object> dataMap = (Map<String, Object>) registerResponse.get("data");
    Integer userId = (Integer) dataMap.get("userId");

    // 查詢該用戶的所有發文，應該返回空列表
    mockMvc.perform(get("/api/posts/user/" + userId)
        .header("Authorization", "Bearer " + testUserToken))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", isA(java.util.List.class)))
        .andExpect(jsonPath("$.length()", is(0)));
  }
}