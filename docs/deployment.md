# 社群媒體平台部署說明

本文檔提供關於如何在本地或伺服器上部署社群媒體平台的完整說明。該平台由Spring Boot後端、Vue.js前端和Nginx組成，使用SQLite作為資料庫。

## 前提條件

請確保您的系統上已安裝以下軟體：

- Java 17或更高版本
- Maven 3.6或更高版本
- Node.js 14或更高版本
- npm 6或更高版本
- Nginx 1.18或更高版本

## 後端部署（Spring Boot）

1. 克隆項目代碼庫：
   ```bash
   git clone <repository-url>
   cd social-backend
   ```

2. 使用Maven構建項目：
   ```bash
   mvn clean package
   ```

3. 運行Spring Boot應用：
   ```bash
   java -jar target/social-backend-0.0.1-SNAPSHOT.jar
   ```

後端服務將在 `http://localhost:8080` 上啟動，並自動創建SQLite資料庫（如果不存在）。

## 前端部署（Vue.js）

1. 進入前端目錄：
   ```bash
   cd frontend
   ```

2. 安裝依賴：
   ```bash
   npm install
   ```

3. 構建生產版本：
   ```bash
   npm run build
   ```

構建後的靜態文件將保存在 `dist` 目錄中。

## Nginx設置

1. 安裝Nginx（根據您的操作系統有所不同）：

   - Ubuntu/Debian：
     ```bash
     sudo apt update
     sudo apt install nginx
     ```

   - CentOS/RHEL：
     ```bash
     sudo yum install epel-release
     sudo yum install nginx
     ```

   - macOS（使用Homebrew）：
     ```bash
     brew install nginx
     ```

2. 將項目的 `nginx.conf` 文件複製到Nginx配置目錄：

   - Ubuntu/Debian：
     ```bash
     sudo cp nginx.conf /etc/nginx/conf.d/social-media.conf
     ```

   - macOS（使用Homebrew）：
     ```bash
     cp nginx.conf /usr/local/etc/nginx/servers/social-media.conf
     ```

3. 將前端構建好的靜態文件複製到Nginx靜態文件目錄：

   - Ubuntu/Debian：
     ```bash
     sudo cp -r frontend/dist/* /usr/share/nginx/html/
     ```

   - macOS（使用Homebrew）：
     ```bash
     cp -r frontend/dist/* /usr/local/var/www/
     ```

4. 啟動或重新載入Nginx：

   - Ubuntu/Debian：
     ```bash
     sudo systemctl restart nginx
     ```

   - macOS（使用Homebrew）：
     ```bash
     brew services restart nginx
     ```

## 完整部署流程

以下是一個完整的部署流程示例：

1. 構建後端：
   ```bash
   cd social-backend
   mvn clean package
   ```

2. 啟動後端：
   ```bash
   java -jar target/social-backend-0.0.1-SNAPSHOT.jar &
   ```

3. 構建前端：
   ```bash
   cd ../frontend
   npm install
   npm run build
   ```

4. 配置並啟動Nginx：
   ```bash
   # 複製Nginx配置
   sudo cp ../nginx.conf /etc/nginx/conf.d/social-media.conf
   
   # 複製前端靜態文件
   sudo cp -r dist/* /usr/share/nginx/html/
   
   # 重啟Nginx
   sudo systemctl restart nginx
   ```

完成上述步驟後，您可以通過瀏覽器訪問 `http://localhost` 來使用社群媒體平台。API端點將通過 `http://localhost/api/` 自動代理到後端。

## 測試部署

為確保一切正常運作，請驗證以下連接：

1. 前端：在瀏覽器中訪問 `http://localhost`，您應該能看到登入頁面。

2. 後端API：使用curl或Postman訪問 `http://localhost/api/posts`，應返回JSON格式的發文列表。

## 故障排除

1. 如果無法訪問前端，請檢查Nginx日誌：
   ```bash
   sudo tail -f /var/log/nginx/error.log
   ```

2. 如果API請求失敗，請檢查後端日誌：
   ```bash
   tail -f social-backend.log
   ```

3. 確認端口可用性：
   ```bash
   # 檢查8080端口（後端）
   ss -tuln | grep 8080
   
   # 檢查80端口（Nginx）
   ss -tuln | grep 80
   ```

4. 如果Nginx顯示"Permission denied"錯誤，可能需要調整SELinux設置（僅限Linux）：
   ```bash
   sudo setsebool -P httpd_can_network_connect 1
   ```

## 安全考慮

1. 在生產環境中，建議使用HTTPS。您可以使用Let's Encrypt獲取免費的SSL證書。

2. 確保適當的防火牆設置，只允許必要的端口開放。

3. 定期備份SQLite資料庫文件。

4. 更新系統和所有依賴項，以獲取最新的安全補丁。 