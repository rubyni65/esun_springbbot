社群媒體平台專案待辦事項
本文件列出社群媒體平台專案的所有待辦任務，根據藍圖分為九個區塊（Chunks），每個區塊包含具體步驟（Steps）。每個步驟描述任務內容和預期輸出，初始狀態為未完成（[ ]）。完成後可勾選為 [x]。

區塊 1：專案設置

[ ] 步驟 1.1：初始化 Spring Boot 後端，包含 Maven 和 SQLite 依賴

描述：創建 Spring Boot 專案，使用 Maven，配置 SQLite 資料庫連線，加入必要依賴（Spring Boot Web、Spring Data JPA、SQLite JDBC、Spring Security、JWT、BCrypt）。
預期輸出：pom.xml、application.properties、目錄結構。


[ ] 步驟 1.2：創建 SQLite 資料庫結構

描述：創建 User、Post 和 Comment 表的 DDL 和測試數據，儲存於 backend/DB。
預期輸出：backend/DB/schema.sql、backend/DB/data.sql。


[ ] 步驟 1.3：初始化 Vue.js 前端，包含 Vue Router

描述：創建 Vue.js 專案（Vue 3），配置 Vue Router，設置 /register、/login、/posts 路由。
預期輸出：package.json、src/main.js、src/router/index.js、src/App.vue、目錄結構。


[ ] 步驟 1.4：設置專案目錄結構和測試數據

描述：組織專案目錄（social-media-platform/backend 和 frontend），更新 application.properties 執行 SQL 腳本，撰寫 README.md。
預期輸出：更新的 application.properties、README.md、目錄結構。



區塊 2：使用者註冊

[ ] 步驟 2.1：創建 User 實體和 JPA 儲存庫

描述：創建 User JPA 實體和儲存庫，映射資料庫 User 表，包含 findByPhoneNumber 方法。
預期輸出：backend/src/main/java/com/example/social/entity/User.java、UserRepository.java。


[ ] 步驟 2.2：實現註冊服務，包含 BCrypt 密碼加密

描述：創建 UserService，實現 registerUser 方法，驗證輸入並使用 BCrypt 加密密碼。
預期輸出：backend/src/main/java/com/example/social/service/UserService.java。


[ ] 步驟 2.3：創建註冊 REST 控制器，包含驗證

描述：創建 UserController，實現 POST /api/register 端點，處理輸入驗證和錯誤。
預期輸出：backend/src/main/java/com/example/social/controller/UserController.java。


[ ] 步驟 2.4：構建 Vue.js 註冊頁面，包含表單和 API 呼叫

描述：創建 Register.vue，實現註冊表單，呼叫 POST /api/register，處理成功和錯誤響應。
預期輸出：frontend/src/views/Register.vue、更新的 src/router/index.js、更新的 package.json。



區塊 3：使用者登入與認證

[ ] 步驟 3.1：實現 JWT 工具，用於生成和驗證令牌

描述：創建 JwtUtil，實現生成、驗證和提取 userId 的方法，配置密鑰。
預期輸出：backend/src/main/java/com/example/social/util/JwtUtil.java、更新的 application.properties。


[ ] 步驟 3.2：創建登入服務和 REST 控制器，發放 JWT

描述：更新 UserService 加入 loginUser，在 UserController 實現 POST /api/login，發放 JWT。
預期輸出：更新的 UserService.java、更新的 UserController.java。


[ ] 步驟 3.3：加入 Spring Security，實現基於 JWT 的認證

描述：配置 Spring Security，使用 JwtAuthenticationFilter 保護 API，允許公開 /api/register 和 /api/login。
預期輸出：backend/src/main/java/com/example/social/config/SecurityConfig.java、JwtAuthenticationFilter.java。


[ ] 步驟 3.4：構建 Vue.js 登入頁面，將 JWT 儲存於 localStorage

描述：創建 Login.vue，實現登入表單，呼叫 POST /api/login，儲存 JWT 並重定向。
預期輸出：frontend/src/views/Login.vue、更新的 src/router/index.js。



區塊 4：發文功能

[ ] 步驟 4.1：創建 Post 實體和 JPA 儲存庫

描述：創建 Post JPA 實體和儲存庫，映射資料庫 Post 表，包含排序方法。
預期輸出：backend/src/main/java/com/example/social/entity/Post.java、PostRepository.java。


[ ] 步驟 4.2：實現發文服務，支援 CRUD 操作

描述：創建 PostService，實現 createPost、getAllPosts、updatePost、deletePost 方法。
預期輸出：backend/src/main/java/com/example/social/service/PostService.java。


[ ] 步驟 4.3：創建發文 REST 控制器，包含 JWT 授權

描述：創建 PostController，實現 POST /api/posts、GET /api/posts、PUT /api/posts/{postId}、DELETE /api/posts/{postId} 端點。
預期輸出：backend/src/main/java/com/example/social/controller/PostController.java。


[ ] 步驟 4.4：增強 Vue.js 發文頁面，支援列出和創建發文

描述：創建 Post.vue，實現發文表單和列表，呼叫 POST /api/posts 和 GET /api/posts。
預期輸出：frontend/src/views/Post.vue、更新的 src/router/index.js。


[ ] 步驟 4.5：在發文頁面加入編輯和刪除功能

描述：更新 Post.vue，加入編輯和刪除按鈕，呼叫 PUT /api/posts/{postId} 和 DELETE /api/posts/{postId}。
預期輸出：更新的 frontend/src/views/Post.vue。



區塊 5：留言功能

[ ] 步驟 5.1：創建 Comment 實體和 JPA 儲存庫

描述：創建 Comment JPA 實體和儲存庫，映射資料庫 Comment 表。
預期輸出：backend/src/main/java/com/example/social/entity/Comment.java、CommentRepository.java。


[ ] 步驟 5.2：實現留言服務，支援創建留言

描述：創建 CommentService，實現 createComment 方法，驗證輸入並儲存留言。
預期輸出：backend/src/main/java/com/example/social/service/CommentService.java。


[ ] 步驟 5.3：創建留言 REST 控制器，包含 JWT 授權

描述：創建 CommentController，實現 POST /api/comments 端點，處理驗證和授權。
預期輸出：backend/src/main/java/com/example/social/controller/CommentController.java。


[ ] 步驟 5.4：在 Vue.js 發文頁面加入留言輸入表單

描述：更新 Post.vue，為每篇發文加入留言表單，呼叫 POST /api/comments。
預期輸出：更新的 frontend/src/views/Post.vue。



區塊 6：安全強化

[ ] 步驟 6.1：在後端整合 OWASP HTML Sanitizer，防範 XSS

描述：加入 OWASP HTML Sanitizer，更新服務以淨化使用者輸入（userName、biography、content、image）。
預期輸出：更新的 pom.xml、SanitizerUtil.java、更新的 UserService.java、PostService.java、CommentService.java。


[ ] 步驟 6.2：在 Vue.js 前端加入輸入淨化，防範 XSS

描述：使用 DOMPurify 淨化前端輸入和顯示內容，更新 Register.vue、Login.vue、Post.vue。
預期輸出：更新的 Register.vue、Login.vue、Post.vue、更新的 package.json。



區塊 7：交易操作

[ ] 步驟 7.1：為交易操作創建 Stored Procedure

描述：創建 create_post_and_comment Stored Procedure，確保交易一致性。
預期輸出：backend/DB/procedures.sql。


[ ] 步驟 7.2：將 Stored Procedure 整合進 Spring Boot

描述：更新 PostService 和 PostController，使用 JdbcTemplate 呼叫 Stored Procedure，加入 @Transactional。
預期輸出：更新的 PostService.java、更新的 PostController.java。



區塊 8：整合與測試

[ ] 步驟 8.1：配置前端代理 API 請求至後端

描述：配置 vue.config.js 代理 API 請求，更新前端 Axios 呼叫為相對路徑。
預期輸出：frontend/vue.config.js、更新的 Register.vue、Login.vue、Post.vue。


[ ] 步驟 8.2：為後端服務撰寫單元測試

描述：為 UserService、PostService、CommentService 撰寫單元測試，使用 JUnit 和 Mockito。
預期輸出：UserServiceTest.java、PostServiceTest.java、CommentServiceTest.java。


[ ] 步驟 8.3：為 API 端點撰寫整合測試

描述：為註冊、登入、發文、留言 API 撰寫整合測試，使用 @SpringBootTest 和 TestRestTemplate。
預期輸出：ApiIntegrationTest.java。



區塊 9：部署設置

[ ] 步驟 9.1：配置 Nginx 以提供前端並代理後端

描述：創建 nginx.conf，提供前端靜態檔案並代理 API 請求，包含構建和運行說明。
預期輸出：nginx.conf、部署說明。


[ ] 步驟 9.2：記錄本地部署說明

描述：更新 README.md，包含完整的本地部署說明（後端、前端、Nginx、資料庫）。
預期輸出：更新的 README.md。



