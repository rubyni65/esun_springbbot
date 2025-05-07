-- SQLite不支持標準存儲過程，此文件提供用於事務操作的SQL腳本
-- 這些SQL語句將在PostService中的@Transactional方法內使用

-- 1. 插入發文
-- INSERT INTO Post (user_id, content, image, created_at) VALUES (?, ?, ?, CURRENT_TIMESTAMP);

-- 2. 獲取最新插入的發文ID
-- SELECT last_insert_rowid();

-- 3. 插入留言
-- INSERT INTO Comment (user_id, post_id, content, created_at) VALUES (?, ?, ?, CURRENT_TIMESTAMP);

-- 4. 獲取最新插入的留言ID
-- SELECT last_insert_rowid();

-- 在Java中使用@Transactional註解和JdbcTemplate來執行上述SQL，確保事務的原子性 