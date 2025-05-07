-- SQLite 不支持標準的存儲過程，但我們可以透過事務來實現原子操作
-- 此文件提供用於創建發文和留言的事務腳本，可以在 JDBC 中執行

-- 創建發文並同時添加留言的事務腳本
-- 使用方式：將以下內容整合到 Spring Boot 應用程式中的 JdbcTemplate 或其他 JDBC 操作中
-- 使用 ? 替代參數，順序為：userId, postContent, postImage, userId, commentContent

-- 開始事務
BEGIN TRANSACTION;

-- 插入新發文
INSERT INTO Post (UserID, Content, Image, CreatedAt)
VALUES (?, ?, ?, CURRENT_TIMESTAMP); -- 參數: userId, postContent, postImage

-- 獲取剛剛創建的發文 ID
-- 在 SQLite 中，last_insert_rowid() 會返回最近插入的行的 ROWID
-- 將這個 ID 用於後續的留言創建
-- 使用 @PostID 變數在 Spring Boot 中接收

-- 插入留言 (關聯到剛創建的發文)
INSERT INTO Comment (UserID, PostID, Content, CreatedAt)
VALUES (?, last_insert_rowid(), ?, CURRENT_TIMESTAMP); -- 參數: userId, commentContent

-- 提交事務
COMMIT;

-- 如果發生錯誤，則回滾事務，確保數據一致性
-- 注意：在實際 JDBC 執行中，需要設置自動提交為 false，
-- 並在 try-catch 塊中手動控制提交或回滾
-- 例如：
/*
    try {
        connection.setAutoCommit(false);
        -- 執行上述 SQL 語句
        connection.commit();
    } catch (SQLException e) {
        connection.rollback();
        throw e;
    } finally {
        connection.setAutoCommit(true);
    }
*/ 