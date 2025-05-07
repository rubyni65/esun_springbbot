為了打造這個社群媒體平台，我們需要一個詳細的、逐步的藍圖，確保以最佳實踐、增量進展的方式完成，且每個步驟都與前一步整合。目標是將專案拆分成小而迭代的區塊，進一步細化為適當大小的步驟，並為每個步驟生成精確的代碼生成 LLM 提示。每個步驟都將基於前一步，確保沒有孤立代碼，並最終形成一個完整的產品。以下是完整的流程、細化步驟以及 LLM 提示，全部翻譯為繁體中文。

---

### 第一步：高階藍圖
該專案涉及使用 Vue.js 作為前端、Spring Boot 作為後端、SQLite 作為資料庫，並以 Maven 進行建置的社群媒體平台。藍圖遵循分層方法：

1. **設置階段**：
   - 初始化後端（Spring Boot 與 Maven）和前端（Vue.js）專案。
   - 配置 SQLite 資料庫，包括結構和測試數據。
2. **後端核心**：
   - 實現使用者註冊和登入功能，包含 JWT 認證。
   - 加入安全措施（BCrypt 密碼加密、OWASP 防 XSS、參數化查詢防 SQL Injection）。
3. **後端功能**：
   - 開發發文功能（新增、列出、編輯、刪除）。
   - 實現留言功能（新增）。
   - 針對交易操作使用 Stored Procedure。
4. **前端開發**：
   - 建立註冊、登入和發文頁面。
   - 整合 API 呼叫並在 localStorage 儲存 JWT。
   - 在使用者輸入中應用 XSS 防護。
5. **整合與部署**：
   - 通過 RESTful API 連接前端與後端。
   - 配置 Nginx 以提供前端並代理 API 請求。
   - 加入基本測試（單元測試和整合測試）。
6. **最終調整**：
   - 確保所有組件相互連接。
   - 驗證安全性和效能需求。

### 第二步：拆分成迭代區塊
為了使專案易於管理，我們將其分成以下迭代區塊，逐步構建：

1. **區塊 1：專案設置**
   - 初始化後端和前端專案。
   - 配置 SQLite 資料庫結構。
2. **區塊 2：使用者註冊**
   - 後端：實現使用者模型、儲存庫和註冊 API。
   - 前端：創建註冊頁面。
3. **區塊 3：使用者登入與認證**
   - 後端：加入帶 JWT 和 BCrypt 的登入 API。
   - 前端：創建登入頁面並儲存 JWT。
4. **區塊 4：發文功能**
   - 後端：實現發文模型、儲存庫和 API（新增、列出、編輯、刪除）。
   - 前端：構建發文頁面以創建和列出發文。
5. **區塊 5：留言功能**
   - 後端：實現留言模型、儲存庫和新增 API。
   - 前端：在發文頁面加入留言輸入框。
6. **區塊 6：安全強化**
   - 後端：加入 OWASP HTML Sanitizer 防 XSS。
   - 前端：應用輸入淨化處理。
7. **區塊 7：交易操作**
   - 後端：為交易操作實現 Stored Procedure。
8. **區塊 8：整合與測試**
   - 連接前端與後端。
   - 加入單元測試和整合測試。
9. **區塊 9：部署設置**
   - 配置 Nginx 和本地部署。

### 第三步：將區塊細化為適當大小的步驟
每個區塊被拆分成更小的步驟，確保可實施、安全且推動專案進展。經過多次迭代，這些步驟平衡了粒度和進展，確保每個步驟是原子的但有意義。以下是經過審查後的精細步驟：

#### 區塊 1：專案設置
- **步驟 1.1**：初始化 Spring Boot 後端，包含 Maven 和 SQLite 依賴。
- **步驟 1.2**：創建 SQLite 資料庫結構（`User`、`Post`、`Comment` 表）。
- **步驟 1.3**：初始化 Vue.js 前端，包含 Vue Router。
- **步驟 1.4**：設置專案目錄結構和測試數據。

#### 區塊 2：使用者註冊
- **步驟 2.1**：在後端創建 `User` 實體和 JPA 儲存庫。
- **步驟 2.2**：實現註冊服務，包含 BCrypt 密碼加密。
- **步驟 2.3**：創建註冊 REST 控制器，包含驗證。
- **步驟 2.4**：構建 Vue.js 註冊頁面，包含表單和 API 呼叫。

#### 區塊 3：使用者登入與認證
- **步驟 3.1**：實現 JWT 工具，用於生成和驗證令牌。
- **步驟 3.2**：創建登入服務和 REST 控制器，發放 JWT。
- **步驟 3.3**：加入 Spring Security，實現基於 JWT 的認證。
- **步驟 3.4**：構建 Vue.js 登入頁面，將 JWT 儲存於 localStorage。

#### 區塊 4：發文功能
- **步驟 4.1**：創建 `Post` 實體和 JPA 儲存庫。
- **步驟 4.2**：實現發文服務，支援 CRUD 操作。
- **步驟 4.3**：創建發文 REST 控制器，包含 JWT 授權。
- **步驟 4.4**：增強 Vue.js 發文頁面，支援列出和創建發文。
- **步驟 4.5**：在發文頁面加入編輯和刪除功能。

#### 區塊 5：留言功能
- **步驟 5.1**：創建 `Comment` 實體和 JPA 儲存庫。
- **步驟 5.2**：實現留言服務，支援創建留言。
- **步驟 5.3**：創建留言 REST 控制器，包含 JWT 授權。
- **步驟 5.4**：在 Vue.js 發文頁面加入留言輸入表單。

#### 區塊 6：安全強化
- **步驟 6.1**：在後端整合 OWASP HTML Sanitizer，防範 XSS。
- **步驟 6.2**：在 Vue.js 前端加入輸入淨化，防範 XSS。

#### 區塊 7：交易操作
- **步驟 7.1**：為交易操作創建 Stored Procedure。
- **步驟 7.2**：將 Stored Procedure 整合進 Spring Boot，使用 `@Transactional`。

#### 區塊 8：整合與測試
- **步驟 8.1**：配置前端代理 API 請求至後端。
- **步驟 8.2**：為後端服務撰寫單元測試（註冊、登入、發文）。
- **步驟 8.3**：為 API 端點撰寫整合測試。

#### 區塊 9：部署設置
- **步驟 9.1**：配置 Nginx 以提供前端並代理後端。
- **步驟 9.2**：記錄本地部署說明。

### 第四步：審查並最終確定步驟
經過迭代，這些步驟：
- **足夠小**：每個步驟專注於單一組件（例如實體、API、頁面）或任務（例如安全、測試），降低錯誤風險。
- **足夠大**：每個步驟交付功能性成果（例如工作中的 API、UI 頁面），與前一步整合。
- **安全**：步驟是增量的，沒有大的複雜性跳躍（例如在核心功能後加入安全）。
- **整合**：每個步驟基於或連接到前一步（例如前端頁面呼叫後端 API）。

這些步驟避免孤立代碼，確保每個組件都通過控制器、路由或測試融入專案。進展邏輯清晰，從設置開始，構建核心功能，加入安全，最終完成測試和部署。

### 第五步：為實施生成 LLM 提示
以下是每個步驟的提示，使用 markdown 格式和代碼塊，確保清晰、最佳實踐以及與前一步的整合。每個提示包含上下文、具體指令和預期輸出，使用文字標籤以確保清晰。提示設計為按順序執行，每個步驟生成可融入專案的代碼。

---

#### 步驟 1.1：初始化 Spring Boot 後端，包含 Maven 和 SQLite 依賴

**上下文**：這是第一步，為社群媒體平台設置後端，使用 Spring Boot、Maven 和 SQLite。專案採用三層式架構（Web 伺服器、應用伺服器、資料庫），並需要 RESTful API。

**提示**：
```text
為社群媒體平台後端創建一個 Spring Boot 專案，使用 Maven。包含以下內容：
- Maven `pom.xml`，包含 Spring Boot Web、Spring Data JPA、SQLite JDBC、Spring Security、JWT (jjwt) 和 BCrypt 的依賴。
- 基本專案結構：`src/main/java/com/example/social` 用於 Java 代碼，`src/main/resources` 用於配置。
- `application.properties` 配置 SQLite 資料庫連線（檔案：`social.db`）。
- 確保專案可使用 `mvn clean install` 構建，並以 `mvn spring-boot:run` 運行。
- 僅輸出必要檔案（`pom.xml`、`application.properties`）和目錄結構，放在 markdown 代碼塊中。
```

**預期輸出**：`pom.xml`、`application.properties` 和目錄結構。

---

#### 步驟 1.2：創建 SQLite 資料庫結構

**上下文**：後端專案已初始化。現在設置 SQLite 資料庫結構，包含 `User`、`Post` 和 `Comment` 表，符合專案需求。

**提示**：
```text
為社群媒體平台創建 SQLite 資料庫結構檔案，儲存在 Step 1.1 的 Spring Boot 專案的 `backend/DB` 目錄中。包含：
- `schema.sql`，包含 `User`、`Post` 和 `Comment` 表的 DDL，符合以下規格：
  - `User`：`UserID`（INTEGER，主鍵，自動遞增）、`PhoneNumber`（TEXT，唯一，10-12 字元）、`UserName`（TEXT，非空）、`Email`（TEXT）、`Password`（TEXT，非空）、`CoverImage`（TEXT）、`Biography`（TEXT）。
  - `Post`：`PostID`（INTEGER，主鍵，自動遞增）、`UserID`（INTEGER，外鍵參考 User，非空）、`Content`（TEXT，非空，<=500 字元）、`Image`（TEXT）、`CreatedAt`（DATETIME，非空，預設 CURRENT_TIMESTAMP）。
  - `Comment`：`CommentID`（INTEGER，主鍵，自動遞增）、`UserID`（INTEGER，外鍵參考 User，非空）、`PostID`（INTEGER，外鍵參考 Post，非空）、`Content`（TEXT，非空，<=500 字元）、`CreatedAt`（DATETIME，非空，預設 CURRENT_TIMESTAMP）。
- `data.sql`，包含測試數據：1 個使用者（PhoneNumber: '1234567890', UserName: 'TestUser', Password: 雜湊佔位符）和 2 篇發文（關聯該使用者）。
- 在 markdown 代碼塊中輸出兩個檔案，確保與 SQLite 和 Spring Boot 的自動執行 SQL 腳本相容。
```

**預期輸出**：`backend/DB/schema.sql`、`backend/DB/data.sql`。

---

#### 步驟 1.3：初始化 Vue.js 前端，包含 Vue Router

**上下文**：後端和資料庫結構已設置。現在初始化 Vue.js 前端，創建單頁應用（SPA）並支援導航。

**提示**：
```text
為社群媒體平台創建 Vue.js 前端專案，使用 Vue 3 和 Vue Router。包含：
- 使用 `vue create` 初始化專案（手動設置，選擇 Vue 3，包含 Router，無其他功能）。
- 基本結構：`src/components` 用於組件，`src/views` 用於頁面，`src/router/index.js` 用於路由。
- 三個空的路由佔位符：`/register`、`/login`、`/posts`。
- `App.vue` 包含 `<router-view>` 以渲染頁面。
- `package.json` 包含必要依賴和腳本（`serve`、`build`）。
- 在 markdown 代碼塊中輸出以下檔案：`package.json`、`src/main.js`、`src/router/index.js`、`src/App.vue` 和目錄結構。
- 確保專案可使用 `npm run serve` 運行。
```

**預期輸出**：`package.json`、`src/main.js`、`src/router/index.js`、`src/App.vue` 和目錄結構。

---

#### 步驟 1.4：設置專案目錄結構和測試數據

**上下文**：後端和前端專案已初始化。現在完成專案結構並確保測試數據就緒。

**提示**：
```text
組織社群媒體平台專案目錄結構，整合 Step 1.1 和 Step 1.3 的 Spring Boot 後端與 Vue.js 前端。包含：
- 根目錄：`social-media-platform`，包含 `backend` 和 `frontend` 子目錄。
- `backend/DB` 包含 Step 1.2 的 `schema.sql` 和 `data.sql`。
- `README.md`，包含基本設置說明：如何運行後端（`mvn spring-boot:run`）和前端（`npm install && npm run serve`）。
- 更新 `backend/src/main/resources/application.properties`，確保啟動時執行 `schema.sql` 和 `data.sql`。
- 在 markdown 代碼塊中輸出以下內容：更新的 `application.properties`、`README.md` 和完整目錄結構。
- 確保不更改先前步驟的現有檔案，僅更新配置。
```

**預期輸出**：更新的 `application.properties`、`README.md` 和目錄結構。

---

#### 步驟 2.1：創建 `User` 實體和 JPA 儲存庫

**上下文**：專案結構和資料庫結構已設置。開始構建註冊功能，創建 `User` 實體和儲存庫。

**提示**：
```text
在 Step 1.1 的 Spring Boot 專案中，創建 `User` 表的 JPA 實體和儲存庫，符合 Step 1.2 的結構。包含：
- 在 `com.example.social.entity` 中創建 `User` 實體，包含欄位：`userId`（Long，主鍵，自動遞增）、`phoneNumber`（String，唯一）、`userName`（String，非空）、`email`（String）、`password`（String，非空）、`coverImage`（String）、`biography`（String）。
- 使用 JPA 註解（`@Entity`、`@Id`、`@GeneratedValue`、`@Column`）。
- 在 `com.example.social.repository` 中創建 `UserRepository` 介面，擴展 `JpaRepository<User, Long>`，包含方法 `Optional<User> findByPhoneNumber(String phoneNumber)`。
- 在 markdown 代碼塊中輸出：`User.java`、`UserRepository.java`。
- 確保實體正確映射到 `schema.sql` 的 `User` 表。
```

**預期輸出**：`backend/src/main/java/com/example/social/entity/User.java`、`backend/src/main/java/com/example/social/repository/UserRepository.java`。

---

#### 步驟 2.2：實現註冊服務，包含 BCrypt 密碼加密

**上下文**：`User` 實體和儲存庫已就緒。實現帶密碼加密的註冊服務。

**提示**：
```text
在 Step 1.1 的 Spring Boot 專案中，創建社群媒體平台的註冊服務，使用 Step 2.1 的 `User` 實體和 `UserRepository`。包含：
- 在 `com.example.social.service` 中創建 `UserService` 類，包含 `registerUser` 方法。
- 方法輸入：`phoneNumber`、`userName`、`password`、`email`、`coverImage`、`biography`。
- 驗證：手機號碼格式（10-12 位數字）、手機號碼唯一性（通過儲存庫檢查）、userName 和 password 非空。
- 使用 BCrypt（Spring Security 的 `BCryptPasswordEncoder`）加密密碼。
- 通過 `UserRepository` 儲存使用者。
- 返回儲存的 `User` 對象（不含密碼）。
- 在 markdown 代碼塊中輸出：`UserService.java`。
- 確保 `pom.xml` 中包含 `spring-boot-starter-security` 依賴（來自 Step 1.1）。
```

**預期輸出**：`backend/src/main/java/com/example/social/service/UserService.java`。

---

#### 步驟 2.3：創建註冊 REST 控制器，包含驗證

**上下文**：註冊服務已實現。通過 REST API 公開該服務。

**提示**：
```text
在 Step 1.1 的 Spring Boot 專案中，創建使用者註冊的 REST 控制器，使用 Step 2.2 的 `UserService`。包含：
- 在 `com.example.social.controller` 中創建 `UserController` 類，包含 `POST /api/register` 端點。
- 請求主體：包含 `phoneNumber`、`userName`、`password`、`email`、`coverImage`、`biography` 的 JSON。
- 使用 `@Valid` 和 Bean Validation（`@NotBlank`、`@Size`）進行輸入驗證。
- 呼叫 `UserService.registerUser`，返回儲存的使用者（不含密碼），狀態碼 201。
- 處理錯誤：驗證錯誤返回 400，重複手機號碼返回 409。
- 在 markdown 代碼塊中輸出：`UserController.java`。
- 確保 `pom.xml` 中包含 `spring-boot-starter-validation` 依賴（若缺失則加入）。
```

**預期輸出**：`backend/src/main/java/com/example/social/controller/UserController.java`。

---

#### 步驟 2.4：構建 Vue.js 註冊頁面，包含表單和 API 呼叫

**上下文**：註冊 API 已就緒。創建前端註冊頁面與其互動。

**提示**：
```text
在 Step 1.3 的 Vue.js 專案中，創建註冊頁面組件，與 Step 2.3 的 `POST /api/register` 端點互動。包含：
- 在 `src/views` 中創建 `Register.vue`，包含表單：`phoneNumber`、`userName`、`password`、`email`、`coverImage`、`biography` 的輸入框。
- 基本客戶端驗證：phoneNumber（10-12 位數字）、userName 和 password 非空。
- 使用 Axios 呼叫 `POST /api/register`，傳送表單數據。
- 成功後，使用 Vue Router 重定向到 `/login`。
- 失敗時，顯示錯誤訊息（例如「手機號碼已存在」）。
- 更新 `src/router/index.js`，確保 `/register` 路由指向 `Register.vue`。
- 在 markdown 代碼塊中輸出：`Register.vue`、更新的 `src/router/index.js`。
- 在 `package.json` 中加入 Axios（包含安裝指令）。
```

**預期輸出**：`frontend/src/views/Register.vue`、更新的 `frontend/src/router/index.js`、更新的 `package.json`。

---

#### 步驟 3.1：實現 JWT 工具，用於生成和驗證令牌

**上下文**：註冊功能完成。開始登入功能，創建 JWT 工具。

**提示**：
```text
在 Step 1.1 的 Spring Boot 專案中，為社群媒體平台創建 JWT 工具。包含：
- 在 `com.example.social.util` 中創建 `JwtUtil` 類，包含方法：
  - `generateToken(Long userId)`：生成包含 `userId` 聲明的 JWT，1 小時過期，使用 HS256 簽名和密鑰。
  - `validateToken(String token)`：驗證令牌並返回 `userId`，或拋出異常。
  - `getUserIdFromToken(String token)`：從有效令牌中提取 `userId`。
- 使用 `io.jsonwebtoken:jjwt` 庫（包含於 Step 1.1 的 `pom.xml`）。
- 在 `application.properties` 中儲存密鑰（例如 `jwt.secret=your-secret-key`）。
- 在 markdown 代碼塊中輸出：`JwtUtil.java`、更新的 `application.properties`。
```

**預期輸出**：`backend/src/main/java/com/example/social/util/JwtUtil.java`、更新的 `application.properties`。

---

#### 步驟 3.2：創建登入服務和 REST 控制器，發放 JWT

**上下文**：JWT 工具已就緒。實現登入服務和 API。

**提示**：
```text
在 Step 1.1 的 Spring Boot 專案中，使用 Step 2.1 的 `UserRepository`、Step 2.2 的 `UserService` 和 Step 3.1 的 `JwtUtil`，創建登入服務和 REST 控制器。包含：
- 更新 `UserService`，加入 `loginUser` 方法：驗證 phoneNumber 和 password（使用 BCrypt），返回 `userId`。
- 在 `UserController` 中創建 `POST /api/login` 端點：
  - 請求主體：包含 `phoneNumber`、`password` 的 JSON。
  - 呼叫 `loginUser`，使用 `JwtUtil` 生成 JWT，返回 JWT，狀態碼 200。
- 處理錯誤：無效憑證返回 401。
- 在 markdown 代碼塊中輸出：更新的 `UserService.java`、更新的 `UserController.java`。
```

**預期輸出**：更新的 `backend/src/main/java/com/example/social/service/UserService.java`、更新的 `backend/src/main/java/com/example/social/controller/UserController.java`。

---

#### 步驟 3.3：加入 Spring Security，實現基於 JWT 的認證

**上下文**：登入 API 已實現。使用 JWT 保護 API。

**提示**：
```text
在 Step 1.1 的 Spring Boot 專案中，配置 Spring Security，使用 Step 3.1 的 JWT 保護 API。包含：
- 在 `com.example.social.config` 中創建 `SecurityConfig` 類：
  - 禁用 CSRF 和 session 管理。
  - 允許未認證訪問 `/api/register` 和 `/api/login`。
  - 要求認證所有其他 `/api/**` 端點。
  - 使用自定義 `JwtAuthenticationFilter` 驗證 `Authorization` 標頭中的 JWT。
- 在 `com.example.social.security` 中創建 `JwtAuthenticationFilter` 類：
  - 從 `Authorization: Bearer` 標頭提取 JWT。
  - 使用 `JwtUtil` 驗證並將 `userId` 設置到 SecurityContext。
- 在 markdown 代碼塊中輸出：`SecurityConfig.java`、`JwtAuthenticationFilter.java`。
- 確保 `pom.xml` 中包含 `spring-boot-starter-security` 和 `jjwt`（來自 Step 1.1）。
```

**預期輸出**：`backend/src/main/java/com/example/social/config/SecurityConfig.java`、`backend/src/main/java/com/example/social/security/JwtAuthenticationFilter.java`。

---

#### 步驟 3.4：構建 Vue.js 登入頁面，將 JWT 儲存於 localStorage

**上下文**：登入 API 和安全性已就緒。創建前端登入頁面。

**提示**：
```text
在 Step 1.3 的 Vue.js 專案中，創建登入頁面組件，與 Step 3.2 的 `POST /api/login` 端點互動。包含：
- 在 `src/views` 中創建 `Login.vue`，包含表單：`phoneNumber`、`password` 的輸入框。
- 基本客戶端驗證：phoneNumber 和 password 非空。
- 使用 Axios 呼叫 `POST /api/login`。
- 成功後，將 JWT 儲存於 localStorage，並使用 Vue Router 重定向到 `/posts`。
- 失敗時，顯示錯誤訊息（例如「無效憑證」）。
- 更新 `src/router/index.js`，確保 `/login` 路由指向 `Login.vue`。
- 在 markdown 代碼塊中輸出：`Login.vue`、更新的 `src/router/index.js`。
- 確保 `package.json` 中包含 Axios（來自 Step 2.4）。
```

**預期輸出**：`frontend/src/views/Login.vue`、更新的 `frontend/src/router/index.js`。

---

#### 步驟 4.1：創建 `Post` 實體和 JPA 儲存庫

**上下文**：認證功能完成。開始發文功能，創建 `Post` 實體。

**提示**：
```text
在 Step 1.1 的 Spring Boot 專案中，創建 `Post` 表的 JPA 實體和儲存庫，符合 Step 1.2 的結構。包含：
- 在 `com.example.social.entity` 中創建 `Post` 實體，包含欄位：`postId`（Long，主鍵，自動遞增）、`userId`（Long，外鍵，非空）、`content`（String，非空，<=500 字元）、`image`（String）、`createdAt`（LocalDateTime，非空，預設當前時間）。
- 使用 JPA 註解（`@Entity`、`@Id`、`@GeneratedValue`、`@Column`）。
- 在 `com.example.social.repository` 中創建 `PostRepository` 介面，擴展 `JpaRepository<Post, Long>`，包含方法 `List<Post> findAllByOrderByCreatedAtDesc()`。
- 在 markdown 代碼塊中輸出：`Post.java`、`PostRepository.java`。
- 確保實體正確映射到 `schema.sql` 的 `Post` 表。
```

**預期輸出**：`backend/src/main/java/com/example/social/entity/Post.java`、`backend/src/main/java/com/example/social/repository/PostRepository.java`。

---

#### 步驟 4.2：實現發文服務，支援 CRUD 操作

**上下文**：`Post` 實體已就緒。實現發文服務。

**提示**：
```text
在 Step 1.1 的 Spring Boot 專案中，使用 Step 4.1 的 `PostRepository` 和 Step 3.1 的 `JwtUtil`，創建發文 CRUD 操作的服務。包含：
- 在 `com.example.social.service` 中創建 `PostService` 類，包含方法：
  - `createPost(Long userId, String content, String image)`：驗證 content（<=500 字元），儲存發文，返回儲存的發文。
  - `getAllPosts()`：返回所有發文，按 `createdAt` 降序排序。
  - `updatePost(Long userId, Long postId, String content, String image)`：驗證 content，檢查使用者擁有權，更新發文。
  - `deletePost(Long userId, Long postId)`：檢查使用者擁有權，刪除發文。
- 在 markdown 代碼塊中輸出：`PostService.java`。
- 若需要，確保與 `UserRepository` 整合以驗證使用者。
```

**預期輸出**：`backend/src/main/java/com/example/social/service/PostService.java`。

---

#### 步驟 4.3：創建發文 REST 控制器，包含 JWT 授權

**上下文**：發文服務已實現。公開發文 API。

**提示**：
```text
在 Step 1.1 的 Spring Boot 專案中，使用 Step 4.2 的 `PostService`，創建發文操作的 REST 控制器。包含：
- 在 `com.example.social.controller` 中創建 `PostController` 類，包含端點：
  - `POST /api/posts`：創建發文（JSON：`content`、`image`），從 JWT 提取 `userId`，返回 201。
  - `GET /api/posts`：返回所有發文（按 `createdAt` 降序），返回 200。
  - `PUT /api/posts/{postId}`：更新發文（JSON：`content`、`image`），通過 JWT 檢查擁有權，返回 200。
  - `DELETE /api/posts/{postId}`：刪除發文，通過 JWT 檢查擁有權，返回 204。
- 使用 `@Valid` 進行輸入驗證。
- 處理錯誤：未認證返回 401，無權限返回 403，發文不存在返回 404。
- 在 markdown 代碼塊中輸出：`PostController.java`。
- 確保受 Step 3.3 的 `JwtAuthenticationFilter` 保護。
```

**預期輸出**：`backend/src/main/java/com/example/social/controller/PostController.java`。

---

#### 步驟 4.4：增強 Vue.js 發文頁面，支援列出和創建發文

**上下文**：發文 API 已就緒。構建前端發文頁面。

**提示**：
```text
在 Step 1.3 的 Vue.js 專案中，創建發文頁面組件，與 Step 4.3 的 `POST /api/posts` 和 `GET /api/posts` 端點互動。包含：
- 在 `src/views` 中創建 `Post.vue`，包含：
  - 創建發文的表單（`content`、`image` 輸入框）。
  - 顯示所有發文的列表（按 `createdAt` 降序）。
  - 客戶端驗證：content <=500 字元。
  - 使用 Axios 呼叫 `POST /api/posts`（在 `Authorization: Bearer` 中包含 JWT）和 `GET /api/posts`。
- 若 localStorage 中無 JWT，重定向到 `/login`。
- 更新 `src/router/index.js`，確保 `/posts` 路由指向 `Post.vue`。
- 在 markdown 代碼塊中輸出：`Post.vue`、更新的 `src/router/index.js`。
- 確保 Axios 可用（來自 Step 2.4）。
```

**預期輸出**：`frontend/src/views/Post.vue`、更新的 `frontend/src/router/index.js`。

---

#### 步驟 4.5：在發文頁面加入編輯和刪除功能

**上下文**：發文頁面支援列出和創建。加入編輯和刪除功能。

**提示**：
```text
在 Step 1.3 的 Vue.js 專案中，更新 Step 4.4 的 `Post.vue` 組件，支援使用 Step 4.3 的 `PUT /api/posts/{postId}` 和 `DELETE /api/posts/{postId}` 進行編輯和刪除發文。包含：
- 為每篇發文加入編輯按鈕，顯示預填資料（`content`、`image`）的表單。
- 為每篇發文加入刪除按鈕，包含確認提示。
- 使用 Axios 呼叫 `PUT` 和 `DELETE` 端點（在 `Authorization: Bearer` 中包含 JWT）。
- 編輯/刪除成功後，重新整理發文列表。
- 處理錯誤：顯示 403（無權限）、404（未找到）的錯誤訊息。
- 在 markdown 代碼塊中輸出：更新的 `Post.vue`。
- 確保不更改 `src/router/index.js`。
```

**預期輸出**：更新的 `frontend/src/views/Post.vue`。

---

#### 步驟 5.1：創建 `Comment` 實體和 JPA 儲存庫

**上下文**：發文功能完成。開始留言功能，創建 `Comment` 實體。

**提示**：
```text
在 Step 1.1 的 Spring Boot 專案中，創建 `Comment` 表的 JPA 實體和儲存庫，符合 Step 1.2 的結構。包含：
- 在 `com.example.social.entity` 中創建 `Comment` 實體，包含欄位：`commentId`（Long，主鍵，自動遞增）、`userId`（Long，外鍵，非空）、`postId`（Long，外鍵，非空）、`content`（String，非空，<=500 字元）、`createdAt`（LocalDateTime，非空，預設當前時間）。
- 使用 JPA 註解（`@Entity`、`@Id`、`@GeneratedValue`、`@Column`）。
- 在 `com.example.social.repository` 中創建 `CommentRepository` 介面，擴展 `JpaRepository<Comment, Long>`。
- 在 markdown 代碼塊中輸出：`Comment.java`、`CommentRepository.java`。
- 確保實體正確映射到 `schema.sql` 的 `Comment` 表。
```

**預期輸出**：`backend/src/main/java/com/example/social/entity/Comment.java`、`backend/src/main/java/com/example/social/repository/CommentRepository.java`。

---

#### 步驟 5.2：實現留言服務，支援創建留言

**上下文**：`Comment` 實體已就緒。實現留言服務。

**提示**：
```text
在 Step 1.1 的 Spring Boot 專案中，使用 Step 5.1 的 `CommentRepository`、Step 4.1 的 `PostRepository` 和 Step 3.1 的 `JwtUtil`，創建創建留言的服務。包含：
- 在 `com.example.social.service` 中創建 `CommentService` 類，包含 `createComment(Long userId, Long postId, String content)` 方法。
- 驗證：content <=500 字元，非空，postId 存在。
- 儲存留言到資料庫。
- 返回儲存的留言。
- 在 markdown 代碼塊中輸出：`CommentService.java`。
```

**預期輸出**：`backend/src/main/java/com/example/social/service/CommentService.java`。

---

#### 步驟 5.3：創建留言 REST 控制器，包含 JWT 授權

**上下文**：留言服務已實現。公開留言 API。

**提示**：
```text
在 Step 1.1 的 Spring Boot 專案中，使用 Step 5.2 的 `CommentService`，創建創建留言的 REST 控制器。包含：
- 在 `com.example.social.controller` 中創建 `CommentController` 類，包含 `POST /api/comments` 端點。
- 請求主體：包含 `postId`、`content` 的 JSON。
- 從 JWT 提取 `userId`，呼叫 `createComment`，返回 201。
- 使用 `@Valid` 進行輸入驗證。
- 處理錯誤：未認證返回 401，發文不存在返回 404，無效內容返回 400。
- 在 markdown 代碼塊中輸出：`CommentController.java`。
- 確保受 Step 3.3 的 `JwtAuthenticationFilter` 保護。
```

**預期輸出**：`backend/src/main/java/com/example/social/controller/CommentController.java`。

---

#### 步驟 5.4：在 Vue.js 發文頁面加入留言輸入表單

**上下文**：留言 API 已就緒。在發文頁面加入留言輸入。

**提示**：
```text
在 Step 1.3 的 Vue.js 專案中，更新 Step 4.5 的 `Post.vue` 組件，加入留言輸入表單，與 Step 5.3 的 `POST /api/comments` 端點互動。包含：
- 在每篇發文下方加入包含 `content` 輸入框的表單。
- 客戶端驗證：content <=500 字元，非空。
- 使用 Axios 呼叫 `POST /api/comments`（在 `Authorization: Bearer` 中包含 JWT）。
- 成功後，清空表單並顯示成功訊息。
- 處理錯誤：顯示 401、404、400 的錯誤訊息。
- 在 markdown 代碼塊中輸出：更新的 `Post.vue`。
- 確保不更改 `src/router/index.js`。
```

**預期輸出**：更新的 `frontend/src/views/Post.vue`。

---

#### 步驟 6.1：在後端整合 OWASP HTML Sanitizer，防範 XSS

**上下文**：核心功能已完成。加入後端 XSS 防護。

**提示**：
```text
在 Step 1.1 的 Spring Boot 專案中，整合 OWASP Java HTML Sanitizer，防範使用者輸入的 XSS 攻擊。包含：
- 在 `pom.xml` 中加入 `owasp-java-html-sanitizer` 依賴。
- 在 `com.example.social.util` 中創建 `SanitizerUtil` 類，包含 `sanitize(String input)` 方法，移除不安全的 HTML 標籤，僅允許安全文本。
- 更新 Step 3.2 的 `UserService`、Step 4.2 的 `PostService` 和 Step 5.2 的 `CommentService`，在儲存前對 `userName`、`biography`、`content` 和 `image` 欄位進行淨化。
- 在 markdown 代碼塊中輸出：更新的 `pom.xml`、`SanitizerUtil.java`、更新的 `UserService.java`、`PostService.java`、`CommentService.java`。
```

**預期輸出**：更新的 `pom.xml`、`backend/src/main/java/com/example/social/util/SanitizerUtil.java`、更新的服務檔案。

---

#### 步驟 6.2：在 Vue.js 前端加入輸入淨化，防範 XSS

**上下文**：後端 XSS 防護已實現。加入前端淨化。

**提示**：
```text
在 Step 1.3 的 Vue.js 專案中，為 Step 2.4 的 `Register.vue`、Step 3.4 的 `Login.vue` 和 Step 5.4 的 `Post.vue` 加入客戶端 XSS 防護。包含：
- 使用 `DOMPurify` 庫在發送 API 請求前淨化使用者輸入。
- 更新表單提交，對 `phoneNumber`、`userName`、`password`、`email`、`coverImage`、`biography`、`content`、`image` 進行淨化。
- 更新發文顯示，對 `content` 和 `image` 在渲染前進行淨化。
- 在 `package.json` 中加入 `DOMPurify`（包含安裝指令）。
- 在 markdown 代碼塊中輸出：更新的 `Register.vue`、`Login.vue`、`Post.vue`、更新的 `package.json`。
```

**預期輸出**：更新的 `Register.vue`、`Login.vue`、`Post.vue`、更新的 `package.json`。

---

#### 步驟 7.1：為交易操作創建 Stored Procedure

**上下文**：安全性已增強。為交易操作實現 Stored Procedure。

**提示**：
```text
在 Step 1.1 的 Spring Boot 專案中，為交易操作創建 SQLite Stored Procedure，儲存在 `backend/DB/procedures.sql`。包含：
- 一個名為 `create_post_and_comment` 的程序：
  - 插入一篇發文（`userId`、`content`、`image`）。
  - 插入一則留言（`userId`、`postId`、`content`）。
  - 使用交易（`BEGIN TRANSACTION` 和 `COMMIT`）確保一致性。
- 確保與 SQLite 相容。
- 在 markdown 代碼塊中輸出：`procedures.sql`。
```

**預期輸出**：`backend/DB/procedures.sql`。

---

#### 步驟 7.2：將 Stored Procedure 整合進 Spring Boot

**上下文**：Stored Procedure 已創建。將其整合進後端。

**提示**：
```text
在 Step 1.1 的 Spring Boot 專案中，整合 Step 7.1 的 `create_post_and_comment` Stored Procedure。包含：
- 更新 `PostService`，加入 `createPostAndComment` 方法，使用 Spring 的 `JdbcTemplate` 呼叫該程序。
- 使用 `@Transactional` 註解確保一致性。
- 在 `PostController` 中創建測試端點：`POST /api/posts-and-comments`，呼叫該方法（用於測試）。
- 在 markdown 代碼塊中輸出：更新的 `PostService.java`、更新的 `PostController.java`。
- 確保 `pom.xml` 中包含 `spring-boot-starter-jdbc` 依賴（若缺失則加入）。
```

**預期輸出**：更新的 `PostService.java`、更新的 `PostController.java`。

---

#### 步驟 8.1：配置前端代理 API 請求至後端

**上下文**：所有功能已實現。配置前端與後端整合。

**提示**：
```text
在 Step 1.3 的 Vue.js 專案中，配置 API 請求代理至 Step 1.1 的 Spring Boot 後端。包含：
- 加入 `vue.config.js`，在開發期間將 `/api/*` 請求代理至 `http://localhost:8080`。
- 更新 `Register.vue`、`Login.vue` 和 `Post.vue` 中的 Axios 呼叫，使用相對路徑（例如 `/api/register` 而非完整 URL）。
- 在 markdown 代碼塊中輸出：`vue.config.js`、更新的 `Register.vue`、`Login.vue`、`Post.vue`。
- 確保不更改後端代碼。
```

**預期輸出**：`frontend/vue.config.js`、更新的 `Register.vue`、`Login.vue`、`Post.vue`。

---

#### 步驟 8.2：為後端服務撰寫單元測試

**上下文**：整合已設置。為服務加入單元測試。

**提示**：
```text
在 Step 1.1 的 Spring Boot 專案中，為 Step 3.2 的 `UserService`、Step 4.2 的 `PostService` 和 Step 5.2 的 `CommentService` 創建單元測試。包含：
- 在 `src/test/java/com/example/social/service` 中創建 `UserServiceTest` 類，測試 `registerUser` 和 `loginUser`。
- 創建 `PostServiceTest` 類，測試 `createPost`、`getAllPosts`。
- 創建 `CommentServiceTest` 類，測試 `createComment`。
- 使用 JUnit 5 和 Mockito 模擬 `UserRepository`、`PostRepository`、`CommentRepository`。
- 在 markdown 代碼塊中輸出：`UserServiceTest.java`、`PostServiceTest.java`、`CommentServiceTest.java`。
- 確保 `pom.xml` 中包含 `spring-boot-starter-test` 依賴（若缺失則加入）。
```

**預期輸出**：`backend/src/test/java/com/example/social/service/UserServiceTest.java`、`PostServiceTest.java`、`CommentServiceTest.java`。

---

#### 步驟 8.3：為 API 端點撰寫整合測試

**上下文**：單元測試已加入。為 API 加入整合測試。

**提示**：
```text
在 Step 1.1 的 Spring Boot 專案中，為 Step 2.3、3.2、4.3、5.3 的 API 端點創建整合測試。包含：
- 在 `src/test/java/com/example/social/controller` 中創建 `ApiIntegrationTest` 類，測試：
  - `POST /api/register`：有效和重複手機號碼。
  - `POST /api/login`：有效和無效憑證。
  - `POST /api/posts`：認證和未認證請求。
  - `POST /api/comments`：有效和無效 postId。
- 使用 `@SpringBootTest` 和 `TestRestTemplate` 測試真實 HTTP 請求。
- 在 markdown 代碼塊中輸出：`ApiIntegrationTest.java`。
- 確保 `pom.xml` 中包含 `spring-boot-starter-test` 依賴（來自 Step 8.2）。
```

**預期輸出**：`backend/src/test/java/com/example/social/controller/ApiIntegrationTest.java`。

---

#### 步驟 9.1：配置 Nginx 以提供前端並代理後端

**上下文**：測試已實現。配置 Nginx 以進行部署。

**提示**：
```text
為社群媒體平台創建 Nginx 配置文件，提供 Step 1.3 的 Vue.js 前端並將 API 請求代理至 Step 1.1 的 Spring Boot 後端。包含：
- `nginx.conf`，執行以下操作：
  - 從 `frontend/dist` 提供前端靜態檔案（使用 `npm run build` 構建）。
  - 將 `/api/*` 請求代理至 `http://localhost:8080`。
  - 處理 SPA 路由，將非 API 請求重定向至 `index.html`。
- 在 markdown 代碼塊中輸出：`nginx.conf`。
- 包含構建前端（`npm run build`）和啟動 Nginx 的說明。
```

**預期輸出**：`nginx.conf` 和部署說明。

---

#### 步驟 9.2：記錄本地部署說明

**上下文**：Nginx 已配置。完成部署文件。

**提示**：
```text
更新 Step 1.4 的社群媒體平台專案中的 `README.md`，包含完整的本地部署說明。包含：
- 前提條件：Java、Maven、Node.js、Nginx。
- 後端設置：`mvn clean install`、`mvn spring-boot:run`。
- 前端設置：`npm install`、`npm run build`。
- Nginx 設置：複製 Step 9.1 的 `nginx.conf`，啟動 Nginx。
- 資料庫：說明 SQLite（`social.db`）會自動創建。
- 測試訪問：訪問 `http://localhost` 進入前端，API 在 `http://localhost/api`。
- 在 markdown 代碼塊中輸出：更新的 `README.md`。
```

**預期輸出**：更新的 `README.md`。

---

### 第六步：最終備註
- **最佳實踐**：每個提示強制模組化（例如分離實體、服務、控制器）、安全性（例如 JWT、XSS 防護）和測試（單元和整合）。
- **增量進展**：步驟邏輯構建，從設置開始，添加功能，強化安全，最後完成測試和部署。
- **整合**：每個步驟與前一步整合（例如前端呼叫後端 API，測試涵蓋已實現功能）。
- **無孤立代碼**：所有代碼通過控制器、路由或測試融入專案。
- **適當大小的步驟**：步驟粒度細（例如單一實體、單一 API），但交付功能進展（例如工作頁面、安全 API）。

這些提示可按順序提供給代碼生成 LLM，生成一個完全符合規格的社群媒體平台。如果需要進一步細化或開始為特定步驟生成代碼，請告訴我！