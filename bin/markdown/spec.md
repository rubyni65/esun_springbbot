社群媒體平台規格文件
1. 概述
本文件描述一個簡易社群媒體平台的規格，包含註冊、登入、發文和留言功能，採用三層式架構（Web Server + Application Server + SQLite 資料庫），使用 Vue.js 作為前端，Spring Boot 實現後端 RESTful API，並以 Maven 進行專案建置。系統需防止 SQL Injection 和 XSS 攻擊，並在必要時使用交易和 Stored Procedure 確保資料一致性。
2. 功能需求
2.1 註冊功能

描述：使用者透過手機號碼註冊帳號。
輸入：
手機號碼（10-12 位數字，必填）。
使用者名稱（必填）。
密碼（必填，後端加鹽雜湊儲存）。
電子郵件（選填）。
封面照片 URL（選填）。
自我介紹（選填）。


驗證：
檢查手機號碼格式（正則表達式）。
確保手機號碼唯一。


輸出：成功註冊後返回使用者資料（不含密碼）。
錯誤處理：
手機號碼格式錯誤：返回 400 Bad Request。
手機號碼已存在：返回 409 Conflict。



2.2 登入驗證功能

描述：使用者透過手機號碼和密碼登入，生成 JWT 驗證身份。
輸入：
手機號碼（必填）。
密碼（必填）。


驗證：
檢查手機號碼是否存在。
比對加鹽雜湊密碼。


輸出：
成功：返回包含 UserID 的 JWT（有效期 1 小時），儲存於前端 localStorage。
失敗：返回 401 Unauthorized。


錯誤處理：
手機號碼或密碼錯誤：返回 401 Unauthorized。



2.3 發文功能

描述：登入使用者可新增、列出、編輯或刪除發文。
子功能：
新增發文 (POST /posts)：
輸入：Content（必填，500 字上限）、Image（選填，圖片 URL）。
驗證：檢查字數、登入狀態（JWT）。
輸出：返回新發文資料。


列出所有發文 (GET /posts)：
輸出：按 CreatedAt 降序返回發文列表。
無分頁。


編輯發文 (PUT /posts/{postId})：
輸入：Content（必填，500 字上限）、Image（選填）。
驗證：僅限發文作者編輯。
輸出：返回更新後的發文。


刪除發文 (DELETE /posts/{postId})：
驗證：僅限發文作者刪除。
輸出：返回 204 No Content。




錯誤處理：
未登入：返回 401 Unauthorized。
無權限：返回 403 Forbidden。
發文不存在：返回 404 Not Found。



2.4 留言功能

描述：登入使用者可在指定發文下新增留言。
子功能：
新增留言 (POST /comments)：
輸入：PostID（必填）、Content（必填，500 字上限）。 ）。
驗證：檢查字數、登入狀態（JWT）、PostID 存在。
輸出：返回新留言資料。




錯誤處理：
未登入：返回 401 Unauthorized。
發文不存在：返回 404 Not Found。
內容為空或超限：返回 400 Bad Request。



3. 非功能需求
3.1 安全性

SQL Injection 防護：使用 Spring Data JPA 參數化查詢和 Stored Procedure。
XSS 防護：
前端（Vue.js）：對使用者輸入進行轉義。
後端（Spring Boot）：使用 OWASP Java HTML Sanitizer 過濾 HTML 標籤。


密碼安全：密碼加鹽雜湊儲存（使用 BCrypt）。
身份驗證：使用 JWT 確保只有登入使用者可發文或留言。

3.2 效能

API 響應時間：單一請求應在 500ms 內完成（本地環境）。
資料庫查詢：針對交易操作使用 Stored Procedure 優化效能。

3.3 可維護性

後端分層設計（展示層、業務層、資料層、共用層）確保代碼模組化。
使用 Maven 管理依賴，簡化建置流程。

3.4 可移植性

使用 SQLite 作為資料庫，便於本地開發和測試。
所有 DDL/DML 腳本存放在 \DB 資料夾，確保資料庫可重現。

4. 系統架構
4.1 三層式架構圖
graph TD
    A[Client Browser] -->|HTTP/RESTful API| B[Web Server: Nginx]
    B -->|Forward Requests| C[Application Server: Spring Boot]
    C -->|JDBC| D[Database: SQLite]
    
    subgraph Frontend
        A -->|Vue.js SPA| E[Register Page]
        A -->|Vue.js SPA| F[Login Page]
        A -->|Vue.js SPA| G[Post Page]
    end
    
    subgraph Backend
        C --> H[Presentation Layer: REST Controllers]
        H --> I[Business Layer: Services]
        I --> J[Data Layer: Repositories]
        J --> K[Common Layer: Utilities]
    end
    
    J -->|Stored Procedures for Transactions| D

4.2 架構說明

前端：Vue.js 單一頁面應用，包含註冊、登入和發文頁面，使用 Vue Router 導航，JWT 儲存於 localStorage。
Web Server：Nginx 處理靜態資源和反向代理。
Application Server：Spring Boot 實現 RESTful API，分為展示層（處理請求）、業務層（邏輯處理）、資料層（資料庫操作）和共用層（JWT、密碼雜湊等工具）。
資料庫：SQLite 儲存 User、Post 和 Comment 資料表，交易操作使用 Stored Procedure。

5. 資料庫設計
5.1 DDL（存放在 \DB\schema.sql）
-- Creating User table
CREATE TABLE User (
    UserID INTEGER PRIMARY KEY AUTOINCREMENT,
    PhoneNumber TEXT NOT NULL UNIQUE CHECK(length(PhoneNumber) BETWEEN 10 AND 12),
    UserName TEXT NOT NULL,
    Email TEXT,
    Password TEXT NOT NULL,
    CoverImage TEXT,
    Biography TEXT
);

-- Creating Post table
CREATE TABLE Post (
    PostID INTEGER PRIMARY KEY AUTOINCREMENT,
    UserID INTEGER NOT NULL,
    Content TEXT NOT NULL CHECK(length(Content) <= 500),
    Image TEXT,
    CreatedAt DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (UserID) REFERENCES User(UserID)
);

-- Creating Comment table
CREATE TABLE Comment (
    CommentID INTEGER PRIMARY KEY AUTOINCREMENT,
    UserID INTEGER NOT NULL,
    PostID INTEGER NOT NULL,
    Content TEXT NOT NULL CHECK(length(Content) <= 500),
    CreatedAt DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (UserID) REFERENCES User(UserID),
    FOREIGN KEY (PostID) REFERENCES Post(PostID)
);

5.2 DML（存放在 \DB\data.sql）
-- Insert test user
INSERT INTO User (PhoneNumber, UserName, Email, Password, CoverImage, Biography)
VALUES ('1234567890', 'TestUser', 'test@example.com', '$2a$10$exampleHashedPassword', NULL, 'Hello, I am a test user!');

-- Insert test posts
INSERT INTO Post (UserID, Content, Image, CreatedAt)
VALUES (1, 'This is my first post!', NULL, '2025-05-07 10:00:00'),
       (1, 'Another test post.', 'https://example.com/image.jpg', '2025-05-07 11:00:00');

6. API 規格
6.1 端點清單

註冊：POST /api/register
請求：{ "phoneNumber": "1234567890", "userName": "TestUser", "password": "password123", "email": "test@example.com", "coverImage": null, "biography": null }
響應：201 Created, 返回使用者資料（無密碼）。


登入：POST /api/login
請求：{ "phoneNumber": "1234567890", "password": "password123" }
響應：200 OK, 返回 JWT。


新增發文：POST /api/posts
請求：{ "content": "Hello world!", "image": null }
響應：201 Created, 返回發文資料。


列出發文：GET /api/posts
響應：200 OK, 返回發文列表（按 CreatedAt 降序）。


編輯發文：PUT /api/posts/{postId}
請求：{ "content": "Updated post!", "image": null }
響應：200 OK, 返回更新後的發文。


刪除發文：DELETE /api/posts/{postId}
響應：204 No Content。


新增留言：POST /api/comments
請求：{ "postId": 1, "content": "Great post!" }
響應：201 Created, 返回留言資料。



7. 專案結構
social-media-platform/
├── frontend/
│   ├── src/
│   │   ├── components/
│   │   │   ├── Register.vue
│   │   │   ├── Login.vue
│   │   │   ├── Post.vue
│   │   ├── App.vue
│   │   ├── main.js
│   ├── public/
│   ├── package.json
├── backend/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/
│   │   │   │   ├── controller/
│   │   │   │   ├── service/
│   │   │   │   ├── repository/
│   │   │   │   ├── util/
│   │   │   ├── resources/
│   │   │   │   ├── application.properties
│   ├── DB/
│   │   ├── schema.sql
│   │   ├── data.sql
│   ├── pom.xml
├── README.md

8. 部署建議

本地運行：
後端：mvn spring-boot:run（預設端口 8080）。
前端：npm install && npm run serve（預設端口 8080，需配置代理至後端）。


資料庫：SQLite 檔案自動生成於專案根目錄（social.db）。
Nginx：配置反向代理，將 /api 路由至後端，其他路由至前端。

9. 測試用例

單元測試：
後端：測試密碼雜湊、JWT 生成、字數驗證。
資料層：測試 CRUD 操作和交易一致性。


整合測試：
測試 API 端點（註冊、登入、發文、留言）。
驗證錯誤處理（未登入、內容超限）。


安全測試：
模擬 SQL Injection 和 XSS 攻擊，確保防護有效。



