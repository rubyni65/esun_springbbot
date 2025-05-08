# 社群媒體平台

這是一個使用Spring Boot作為後端、Vue.js作為前端的社群媒體平台專案。該專案支援使用者註冊、登入、發文和留言功能。

## 快速上手指南 (從 Clone 到執行)

根據以下步驟在新的開發環境中設定並執行此專案：

1.  **克隆 (Clone) 專案儲存庫**:
    首先，使用 Git 將專案複製到您的本地電腦。打開您的終端機或命令提示字元，並執行以下指令：
    ```bash
    git clone <repository-url>
    ```
    將 `<repository-url>` 替換為實際的儲存庫 URL。

2.  **進入專案目錄**:
    ```bash
    cd social-media-platform # 或者您 clone 下來的專案根目錄名稱
    ```

3.  **設定並執行後端 (Spring Boot)**:
    *   進入後端專案目錄：
        ```bash
        cd backend # 或 social-backend，取決於您的目錄結構
        ```
    *   使用 Maven 安裝依賴並建置專案：
        ```bash
        ./mvnw clean install  # macOS/Linux
        # mvnw.cmd clean install # Windows
        ```
        (如果您的專案根目錄中直接有 `mvnw`，則在專案根目錄執行 `./mvnw -f backend/pom.xml clean install` 或進入 `backend` 後執行 `./mvnw clean install`)
    *   執行 Spring Boot 應用程式：
        ```bash
        ./mvnw spring-boot:run # macOS/Linux
        # mvnw.cmd spring-boot:run # Windows
        ```
        (同樣，根據 `mvnw` 的位置調整，例如在專案根目錄執行 `./mvnw -f backend/pom.xml spring-boot:run`)
    後端服務預設會在 `http://localhost:8080` 啟動。

4.  **設定並執行前端 (Vue.js)**:
    *   打開一個新的終端機視窗/分頁。
    *   進入前端專案目錄 (從專案根目錄開始)：
        ```bash
        cd frontend
        ```
    *   安裝前端依賴套件：
        ```bash
        npm install
        ```
    *   啟動前端開發伺服器：
        ```bash
        npm run serve
        ```
    前端開發伺服器預設會在 `http://localhost:8081` 啟動。

5.  **驗證**:
    *   打開瀏覽器，訪問前端應用程式 `http://localhost:8081`。
    *   您應該能夠看到登入/註冊頁面，並可以開始使用應用程式的功能。

**重要前提條件**:
在開始之前，請確保您的系統已安裝以下軟體 (詳細版本請參考下方的「本地開發環境設置」)：
*   Java JDK (建議版本 17 或更高)
*   Maven
*   Node.js
*   npm

---

## 專案結構

```
social-media-platform/
├── backend/               # Spring Boot 後端
│   ├── src/               # 原始碼
│   │   ├── main/
│   │   │   ├── java/      # Java 程式碼
│   │   │   └── resources/ # 資源檔案
│   │   │       ├── sql/   # 資料庫相關檔案
│   │   │       │   ├── schema.sql
│   │   │       │   ├── data.sql
│   │   │       │   └── procedures.sql
│   │   │       └── application.properties
│   │   └── test/          # 測試程式碼
│   └── pom.xml            # Maven 依賴
├── frontend/              # Vue.js 前端
│   ├── src/               # 原始碼
│   │   ├── components/    # 組件
│   │   ├── views/         # 頁面視圖
│   │   ├── services/      # API服務
│   │   ├── utils/         # 工具函數
│   │   ├── router/        # 路由設定
│   │   ├── App.vue        # 主應用組件
│   │   └── main.js        # 入口檔案
│   ├── package.json       # npm 依賴
│   └── vue.config.js      # Vue 設定
├── docs/                  # 文件
│   └── deployment.md      # 部署說明
└── nginx.conf             # Nginx配置
```

## 技術

- 後端：Spring Boot, Spring Data JPA, Spring Security, JWT
- 前端：Vue.js 3, Vue Router, Axios
- 資料庫：SQLite
- 安全：BCrypt密碼加密, OWASP HTML Sanitizer, DOMPurify
- 測試：JUnit, Mockito, Spring Test
- 部署：Nginx

## 功能特點

- 使用者註冊和登入，使用JWT認證
- 創建、閱讀、更新和刪除發文
- 對發文進行留言
- 密碼安全存儲使用BCrypt加密
- 防範XSS攻擊（後端和前端）
- 事務一致性確保發文和留言操作的原子性

## 數據庫

本專案使用SQLite作為資料庫，無需額外配置。資料庫文件會自動在項目根目錄創建。

### 資料模型

- User（使用者）：儲存使用者資料，包含加密的密碼
- Post（發文）：儲存發文內容和關聯的使用者ID
- Comment（留言）：儲存留言內容和關聯的發文ID和使用者ID
