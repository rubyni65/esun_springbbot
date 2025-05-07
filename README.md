# 社群媒體平台

這是一個使用Spring Boot作為後端、Vue.js作為前端的社群媒體平台專案。該專案支援使用者註冊、登入、發文和留言功能。

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

## 技術棧

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

## 本地開發環境設置

### 前提條件

請確保您的系統上已安裝以下軟體：

- Java 17或更高版本
- Maven 3.6或更高版本
- Node.js 14或更高版本
- npm 6或更高版本

### 後端開發

1. 克隆項目代碼庫：
   ```bash
   git clone <repository-url>
   cd social-backend
   ```

2. 使用Maven構建項目：
   ```bash
   mvn clean install
   ```

3. 運行Spring Boot應用：
   ```bash
   mvn spring-boot:run
   ```

後端服務將在 `http://localhost:8080` 上啟動。

### 前端開發

1. 進入前端目錄：
   ```bash
   cd frontend
   ```

2. 安裝依賴：
   ```bash
   npm install
   ```

3. 啟動開發服務器：
   ```bash
   npm run serve
   ```

前端開發服務器將在 `http://localhost:8081` 上啟動。

## 生產環境部署

如需完整的部署說明，包含Nginx配置和生產環境部署步驟，請參考 [部署說明文檔](docs/deployment.md)。

### 快速部署步驟摘要

1. 後端構建和運行：
   ```bash
   mvn clean package
   java -jar target/social-backend-0.0.1-SNAPSHOT.jar
   ```

2. 前端構建：
   ```bash
   cd frontend
   npm install
   npm run build
   ```

3. Nginx配置：
   - 將 `nginx.conf` 配置到您的Nginx服務器
   - 將前端構建結果（`frontend/dist/*`）複製到Nginx靜態資源目錄
   - 重新啟動Nginx

## 測試

### 後端測試

運行所有後端測試：
```bash
mvn test
```

執行特定測試：
```bash
mvn -Dtest=UserServiceTest test
```

### 前端測試

```bash
cd frontend
npm run test:unit
```

## 使用示例

1. 訪問註冊頁面：`http://localhost:8081/register`
2. 創建新帳戶
3. 使用新帳戶登入
4. 創建發文和留言

## 數據庫

本專案使用SQLite作為資料庫，無需額外配置。資料庫文件會自動在項目根目錄創建。

### 資料模型

- User（使用者）：儲存使用者資料，包含加密的密碼
- Post（發文）：儲存發文內容和關聯的使用者ID
- Comment（留言）：儲存留言內容和關聯的發文ID和使用者ID

## 貢獻指南

1. Fork此項目
2. 創建您的功能分支：`git checkout -b feature/amazing-feature`
3. 提交您的更改：`git commit -m 'Add some amazing feature'`
4. 推送到分支：`git push origin feature/amazing-feature`
5. 提交Pull Request

## 許可證

此項目採用MIT許可證 - 詳情見 [LICENSE](LICENSE) 文件 